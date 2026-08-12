package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.AdminConfig
import com.example.data.model.EsportsMatch
import com.example.data.model.MatchStatus
import com.example.data.model.MatchWithResults
import com.example.data.model.Player
import com.example.data.model.Team
import com.example.data.model.TeamLeaderboardEntry
import com.example.data.model.TeamWithPlayers
import com.example.data.model.Tournament
import com.example.data.repository.EsportsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class ConfirmationType {
    data class DeleteTeam(val team: Team) : ConfirmationType()
    data class DeleteMatch(val match: EsportsMatch) : ConfirmationType()
    data class ResetMatchScore(val match: EsportsMatch) : ConfirmationType()
    data class EndMatch(val match: EsportsMatch) : ConfirmationType()
    data class DeleteTournament(val tournament: Tournament) : ConfirmationType()
}

class EsportsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: EsportsRepository

    init {
        val dao = AppDatabase.getInstance(application).esportsDao()
        repository = EsportsRepository(dao)

        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    val allTournaments: StateFlow<List<Tournament>> = repository.allTournaments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminConfig: StateFlow<AdminConfig?> = repository.adminConfig
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _selectedTournamentId = MutableStateFlow<Long?>(null)

    // Active Tournament ID (uses explicit selection or falls back to adminConfig active id or first tournament)
    @OptIn(ExperimentalCoroutinesApi::class)
    val activeTournament: StateFlow<Tournament?> = combine(
        allTournaments,
        adminConfig,
        _selectedTournamentId
    ) { tournaments, config, selectedId ->
        val idToUse = selectedId ?: config?.activeTournamentId ?: tournaments.firstOrNull()?.id ?: 1L
        tournaments.find { it.id == idToUse } ?: tournaments.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val leaderboard: StateFlow<List<TeamLeaderboardEntry>> = activeTournament
        .flatMapLatest { tournament ->
            if (tournament != null) repository.getLeaderboard(tournament.id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val teamsWithPlayers: StateFlow<List<TeamWithPlayers>> = activeTournament
        .flatMapLatest { tournament ->
            if (tournament != null) repository.getTeamsWithPlayers(tournament.id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val matchesWithResults: StateFlow<List<MatchWithResults>> = activeTournament
        .flatMapLatest { tournament ->
            if (tournament != null) repository.getMatchesWithResults(tournament.id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin Security Lock state
    private val _isAdminUnlocked = MutableStateFlow(false)
    val isAdminUnlocked: StateFlow<Boolean> = _isAdminUnlocked.asStateFlow()

    // Confirmation Dialog state
    private val _activeConfirmation = MutableStateFlow<ConfirmationType?>(null)
    val activeConfirmation: StateFlow<ConfirmationType?> = _activeConfirmation.asStateFlow()

    // Search / Filter query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Selected Navigation Tab
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    fun setSelectedTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
    }

    // Admin Auth Actions
    fun unlockAdmin(pin: String): Boolean {
        val currentPin = adminConfig.value?.pin ?: "739284"
        if (pin == currentPin) {
            _isAdminUnlocked.value = true
            return true
        }
        return false
    }

    fun lockAdmin() {
        _isAdminUnlocked.value = false
    }

    fun changeAdminPin(newPin: String) {
        viewModelScope.launch {
            repository.updateAdminPin(newPin)
        }
    }

    fun switchActiveTournament(tournamentId: Long) {
        _selectedTournamentId.value = tournamentId
        viewModelScope.launch {
            repository.setActiveTournamentId(tournamentId)
        }
    }

    // Tournament Management
    fun createTournament(
        name: String,
        gameTitle: String,
        description: String,
        killMultiplier: Int,
        placementPointsCsv: String
    ) {
        viewModelScope.launch {
            val t = Tournament(
                name = name,
                gameTitle = gameTitle.ifBlank { "Free Fire" },
                description = description,
                killPointMultiplier = killMultiplier,
                placementPointsCsv = placementPointsCsv.ifBlank { "12,9,8,7,6,5,4,3,2,1,0,0,0,0,0,0" }
            )
            val newId = repository.createTournament(t)
            _selectedTournamentId.value = newId
        }
    }

    fun updateTournament(tournament: Tournament) {
        viewModelScope.launch {
            repository.updateTournament(tournament)
        }
    }

    // Confirmation Triggers & Executions
    fun requestConfirmation(type: ConfirmationType) {
        _activeConfirmation.value = type
    }

    fun dismissConfirmation() {
        _activeConfirmation.value = null
    }

    fun executeConfirmedAction() {
        val confirmation = _activeConfirmation.value ?: return
        viewModelScope.launch {
            when (confirmation) {
                is ConfirmationType.DeleteTeam -> {
                    repository.deleteTeam(confirmation.team.id)
                }
                is ConfirmationType.DeleteMatch -> {
                    repository.deleteMatch(confirmation.match.id)
                }
                is ConfirmationType.ResetMatchScore -> {
                    repository.resetMatchScores(confirmation.match.id)
                    repository.updateMatch(confirmation.match.copy(status = MatchStatus.UPCOMING))
                }
                is ConfirmationType.EndMatch -> {
                    repository.updateMatch(confirmation.match.copy(status = MatchStatus.FINISHED))
                }
                is ConfirmationType.DeleteTournament -> {
                    repository.deleteTournament(confirmation.tournament.id)
                }
            }
            _activeConfirmation.value = null
        }
    }

    // Team Actions
    fun addTeam(name: String, tag: String, colorHex: String, slotNumber: Int) {
        val tournamentId = activeTournament.value?.id ?: return
        viewModelScope.launch {
            val team = Team(
                tournamentId = tournamentId,
                name = name,
                tag = tag,
                logoColorHex = colorHex,
                slotNumber = slotNumber
            )
            val teamId = repository.addTeam(team)
            // Add 4 default player placeholders if requested
            listOf("Player 1", "Player 2", "Player 3", "Player 4").forEachIndexed { idx, playerIgn ->
                repository.addPlayer(
                    Player(
                        teamId = teamId,
                        ign = "${tag}_$playerIgn",
                        role = if (idx == 0) "Captain" else "Rusher",
                        isCaptain = (idx == 0)
                    )
                )
            }
        }
    }

    fun addPlayer(teamId: Long, ign: String, role: String, isCaptain: Boolean) {
        viewModelScope.launch {
            repository.addPlayer(
                Player(
                    teamId = teamId,
                    ign = ign,
                    role = role,
                    isCaptain = isCaptain
                )
            )
        }
    }

    fun deletePlayer(playerId: Long) {
        viewModelScope.launch {
            repository.deletePlayer(playerId)
        }
    }

    // Match Actions
    fun addMatch(matchName: String, mapName: String) {
        val tournament = activeTournament.value ?: return
        val currentMatchCount = matchesWithResults.value.size
        viewModelScope.launch {
            val match = EsportsMatch(
                tournamentId = tournament.id,
                matchNumber = currentMatchCount + 1,
                matchName = matchName.ifBlank { "Match ${currentMatchCount + 1} - $mapName" },
                mapName = mapName.ifBlank { "Bermuda" },
                status = MatchStatus.UPCOMING
            )
            repository.addMatch(match)
        }
    }

    fun updateMatchStatus(match: EsportsMatch, newStatus: MatchStatus) {
        viewModelScope.launch {
            repository.updateMatch(match.copy(status = newStatus))
        }
    }

    fun saveMatchResults(matchId: Long, teamScores: List<Pair<Long, Pair<Int, Int>>>) {
        viewModelScope.launch {
            repository.saveMatchResults(matchId, teamScores)
            // Mark match as LIVE or FINISHED if scores entered
            val matchWithResults = matchesWithResults.value.find { it.match.id == matchId }
            if (matchWithResults != null && matchWithResults.match.status == MatchStatus.UPCOMING) {
                repository.updateMatch(matchWithResults.match.copy(status = MatchStatus.LIVE))
            }
        }
    }

    // Seed / Reset Database
    fun resetDatabaseToTestData() {
        viewModelScope.launch {
            repository.forceResetAndSeed()
        }
    }
}
