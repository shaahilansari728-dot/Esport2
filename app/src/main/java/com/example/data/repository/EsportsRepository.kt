package com.example.data.repository

import com.example.data.local.EsportsDao
import com.example.data.local.TestSeedData
import com.example.data.model.AdminConfig
import com.example.data.model.EsportsMatch
import com.example.data.model.MatchStatus
import com.example.data.model.MatchTeamResult
import com.example.data.model.MatchTeamScore
import com.example.data.model.MatchWithResults
import com.example.data.model.Player
import com.example.data.model.Team
import com.example.data.model.TeamLeaderboardEntry
import com.example.data.model.TeamWithPlayers
import com.example.data.model.Tournament
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class EsportsRepository(private val dao: EsportsDao) {

    val allTournaments: Flow<List<Tournament>> = dao.getAllTournaments()
    val adminConfig: Flow<AdminConfig?> = dao.getAdminConfig()

    // Seed initial test data on startup if database is empty
    suspend fun seedInitialDataIfEmpty() = withContext(Dispatchers.IO) {
        val existingTournaments = dao.getFirstTournamentDirect()
        if (existingTournaments == null) {
            forceResetAndSeed()
        } else {
            // Ensure AdminConfig exists
            val config = dao.getAdminConfigDirect()
            if (config == null) {
                dao.insertOrUpdateAdminConfig(AdminConfig(id = 1, pin = "739284", activeTournamentId = existingTournaments.id))
            } else if (config.pin == "1234") {
                dao.insertOrUpdateAdminConfig(config.copy(pin = "739284"))
            }
        }
    }

    suspend fun forceResetAndSeed() = withContext(Dispatchers.IO) {
        dao.deleteAllResults()
        dao.deleteAllMatches()
        dao.deleteAllPlayers()
        dao.deleteAllTeams()
        dao.deleteAllTournaments()

        // 1. Create Default Tournament
        val tournament = TestSeedData.getDefaultTournament()
        val tournamentId = dao.insertTournament(tournament)

        // 2. Set Admin Config
        dao.insertOrUpdateAdminConfig(AdminConfig(id = 1, pin = "739284", activeTournamentId = tournamentId))

        // 3. Create 16 Teams with 4 Players each
        val slotToTeamId = mutableMapOf<Int, Long>()
        TestSeedData.sampleTeamDefs.forEach { teamDef ->
            val team = Team(
                tournamentId = tournamentId,
                name = teamDef.name,
                tag = teamDef.tag,
                logoColorHex = teamDef.colorHex,
                slotNumber = teamDef.slot
            )
            val teamId = dao.insertTeam(team)
            slotToTeamId[teamDef.slot] = teamId

            teamDef.players.forEachIndexed { index, ign ->
                val player = Player(
                    teamId = teamId,
                    ign = ign,
                    role = when (index) {
                        0 -> "Captain / ICL"
                        1 -> "Rusher"
                        2 -> "Sniper"
                        else -> "Support"
                    },
                    isCaptain = (index == 0)
                )
                dao.insertPlayer(player)
            }
        }

        // 4. Create 5 Matches
        val matchIds = mutableListOf<Long>()
        TestSeedData.sampleMatches.forEach { matchDef ->
            val match = matchDef.copy(tournamentId = tournamentId)
            val matchId = dao.insertMatch(match)
            matchIds.add(matchId)
        }

        // 5. Populate Results for Matches 1, 2, 3
        if (matchIds.size >= 3) {
            val match1Id = matchIds[0]
            TestSeedData.match1Placements.forEach { p ->
                val teamId = slotToTeamId[p.teamSlot] ?: return@forEach
                dao.insertOrUpdateResult(
                    MatchTeamResult(
                        matchId = match1Id,
                        teamId = teamId,
                        placement = p.placement,
                        kills = p.kills,
                        booyah = p.booyah
                    )
                )
            }

            val match2Id = matchIds[1]
            TestSeedData.match2Placements.forEach { p ->
                val teamId = slotToTeamId[p.teamSlot] ?: return@forEach
                dao.insertOrUpdateResult(
                    MatchTeamResult(
                        matchId = match2Id,
                        teamId = teamId,
                        placement = p.placement,
                        kills = p.kills,
                        booyah = p.booyah
                    )
                )
            }

            val match3Id = matchIds[2]
            TestSeedData.match3Placements.forEach { p ->
                val teamId = slotToTeamId[p.teamSlot] ?: return@forEach
                dao.insertOrUpdateResult(
                    MatchTeamResult(
                        matchId = match3Id,
                        teamId = teamId,
                        placement = p.placement,
                        kills = p.kills,
                        booyah = p.booyah
                    )
                )
            }
        }
    }

    // Active Tournament Flow
    fun getActiveTournament(tournamentId: Long): Flow<Tournament?> {
        return dao.getTournamentById(tournamentId)
    }

    // Teams for a given tournament
    fun getTeamsByTournament(tournamentId: Long): Flow<List<Team>> {
        return dao.getTeamsByTournament(tournamentId)
    }

    // Players for a given tournament
    fun getPlayersForTournament(tournamentId: Long): Flow<List<Player>> {
        return dao.getPlayersForTournament(tournamentId)
    }

    // Matches for a given tournament
    fun getMatchesByTournament(tournamentId: Long): Flow<List<EsportsMatch>> {
        return dao.getMatchesByTournament(tournamentId)
    }

    // Results for a given tournament
    fun getResultsByTournament(tournamentId: Long): Flow<List<MatchTeamResult>> {
        return dao.getResultsByTournament(tournamentId)
    }

    // Calculated Leaderboard Flow
    fun getLeaderboard(tournamentId: Long): Flow<List<TeamLeaderboardEntry>> {
        return combine(
            dao.getTournamentById(tournamentId),
            dao.getTeamsByTournament(tournamentId),
            dao.getMatchesByTournament(tournamentId),
            dao.getResultsByTournament(tournamentId),
            dao.getPlayersForTournament(tournamentId)
        ) { tournament, teams, matches, results, players ->
            if (tournament == null || teams.isEmpty()) return@combine emptyList()

            val finishedMatchIds = matches.filter { it.status == MatchStatus.FINISHED }.map { it.id }.toSet()
            val validResults = results.filter { it.matchId in finishedMatchIds }

            val teamPlayersMap = players.groupBy { it.teamId }

            val entries = teams.map { team ->
                val teamResults = validResults.filter { it.teamId == team.id }
                val matchesPlayed = teamResults.count { it.placement > 0 }
                val booyahCount = teamResults.count { it.booyah || it.placement == 1 }
                val totalKills = teamResults.sumOf { it.kills }
                val killPoints = totalKills * tournament.killPointMultiplier
                val placementPoints = teamResults.sumOf { tournament.getPointsForPlacement(it.placement) }
                val totalPoints = killPoints + placementPoints

                val captainOrTopPlayer = teamPlayersMap[team.id]?.firstOrNull { it.isCaptain }?.ign
                    ?: teamPlayersMap[team.id]?.firstOrNull()?.ign

                TeamLeaderboardEntry(
                    team = team,
                    matchesPlayed = matchesPlayed,
                    booyahCount = booyahCount,
                    totalKills = totalKills,
                    totalKillPoints = killPoints,
                    totalPlacementPoints = placementPoints,
                    totalPoints = totalPoints,
                    topPlayer = captainOrTopPlayer
                )
            }

            // Rank by Total Points DESC, Booyah Count DESC, Total Kills DESC, Name ASC
            val sorted = entries.sortedWith(
                compareByDescending<TeamLeaderboardEntry> { it.totalPoints }
                    .thenByDescending { it.booyahCount }
                    .thenByDescending { it.totalKills }
                    .thenBy { it.team.name }
            )

            sorted.mapIndexed { index, entry ->
                entry.copy(rank = index + 1)
            }
        }
    }

    // Teams with Players and Leaderboard Stats
    fun getTeamsWithPlayers(tournamentId: Long): Flow<List<TeamWithPlayers>> {
        return combine(
            dao.getTeamsByTournament(tournamentId),
            dao.getPlayersForTournament(tournamentId),
            getLeaderboard(tournamentId)
        ) { teams, players, leaderboard ->
            val playerMap = players.groupBy { it.teamId }
            val statsMap = leaderboard.associateBy { it.team.id }

            teams.map { team ->
                TeamWithPlayers(
                    team = team,
                    players = playerMap[team.id] ?: emptyList(),
                    leaderboardStats = statsMap[team.id]
                )
            }
        }
    }

    // Matches with Team Scores for a tournament
    fun getMatchesWithResults(tournamentId: Long): Flow<List<MatchWithResults>> {
        return combine(
            dao.getTournamentById(tournamentId),
            dao.getMatchesByTournament(tournamentId),
            dao.getTeamsByTournament(tournamentId),
            dao.getResultsByTournament(tournamentId)
        ) { tournament, matches, teams, results ->
            if (tournament == null) return@combine emptyList()

            val resultMap = results.groupBy { it.matchId }
            val teamMap = teams.associateBy { it.id }

            matches.map { match ->
                val matchResults = resultMap[match.id] ?: emptyList()
                val teamScores = teams.map { team ->
                    val result = matchResults.find { it.teamId == team.id }
                    val placement = result?.placement ?: 0
                    val kills = result?.kills ?: 0
                    val booyah = (placement == 1) || (result?.booyah == true)
                    val placementPts = tournament.getPointsForPlacement(placement)
                    val totalPts = (kills * tournament.killPointMultiplier) + placementPts

                    MatchTeamScore(
                        resultId = result?.id ?: 0,
                        matchId = match.id,
                        team = team,
                        placement = placement,
                        kills = kills,
                        placementPoints = placementPts,
                        totalPoints = totalPts,
                        booyah = booyah
                    )
                }.sortedWith(
                    compareBy<MatchTeamScore> { if (it.placement > 0) it.placement else 999 }
                        .thenByDescending { it.totalPoints }
                        .thenByDescending { it.kills }
                        .thenBy { it.team.name }
                )

                MatchWithResults(
                    match = match,
                    results = teamScores
                )
            }
        }
    }

    // --- CRUD Actions ---

    suspend fun createTournament(tournament: Tournament): Long = withContext(Dispatchers.IO) {
        val id = dao.insertTournament(tournament)
        val config = dao.getAdminConfigDirect() ?: AdminConfig(id = 1, pin = "739284", activeTournamentId = id)
        dao.insertOrUpdateAdminConfig(config.copy(activeTournamentId = id))
        id
    }

    suspend fun updateTournament(tournament: Tournament) = withContext(Dispatchers.IO) {
        dao.updateTournament(tournament)
    }

    suspend fun deleteTournament(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteMatchesByTournament(id)
        dao.deleteTeamsByTournament(id)
        dao.deleteTournament(id)

        // Switch active tournament if needed
        val remaining = dao.getFirstTournamentDirect()
        if (remaining != null) {
            val config = dao.getAdminConfigDirect() ?: AdminConfig(id = 1)
            dao.insertOrUpdateAdminConfig(config.copy(activeTournamentId = remaining.id))
        }
    }

    suspend fun setActiveTournamentId(tournamentId: Long) = withContext(Dispatchers.IO) {
        val config = dao.getAdminConfigDirect() ?: AdminConfig(id = 1, pin = "739284", activeTournamentId = tournamentId)
        dao.insertOrUpdateAdminConfig(config.copy(activeTournamentId = tournamentId))
    }

    suspend fun addTeam(team: Team): Long = withContext(Dispatchers.IO) {
        dao.insertTeam(team)
    }

    suspend fun updateTeam(team: Team) = withContext(Dispatchers.IO) {
        dao.updateTeam(team)
    }

    suspend fun deleteTeam(teamId: Long) = withContext(Dispatchers.IO) {
        dao.deletePlayersByTeam(teamId)
        dao.deleteResultsForTeam(teamId)
        dao.deleteTeam(teamId)
    }

    suspend fun addPlayer(player: Player): Long = withContext(Dispatchers.IO) {
        dao.insertPlayer(player)
    }

    suspend fun deletePlayer(playerId: Long) = withContext(Dispatchers.IO) {
        dao.deletePlayer(playerId)
    }

    suspend fun addMatch(match: EsportsMatch): Long = withContext(Dispatchers.IO) {
        dao.insertMatch(match)
    }

    suspend fun updateMatch(match: EsportsMatch) = withContext(Dispatchers.IO) {
        dao.updateMatch(match)
    }

    suspend fun deleteMatch(matchId: Long) = withContext(Dispatchers.IO) {
        dao.deleteResultsForMatch(matchId)
        dao.deleteMatch(matchId)
    }

    suspend fun saveMatchResults(matchId: Long, teamScores: List<Pair<Long, Pair<Int, Int>>>) = withContext(Dispatchers.IO) {
        // teamScores is List<Pair<TeamId, Pair<Placement, Kills>>>
        teamScores.forEach { (teamId, score) ->
            val placement = score.first
            val kills = score.second
            val booyah = (placement == 1)

            val existingResults = dao.getResultsByMatchDirect(matchId)
            val existing = existingResults.find { it.teamId == teamId }

            val result = MatchTeamResult(
                id = existing?.id ?: 0,
                matchId = matchId,
                teamId = teamId,
                placement = placement,
                kills = kills,
                booyah = booyah
            )
            dao.insertOrUpdateResult(result)
        }
    }

    suspend fun resetMatchScores(matchId: Long) = withContext(Dispatchers.IO) {
        dao.deleteResultsForMatch(matchId)
        val match = dao.getMatchById(matchId)
        // Reset status to UPCOMING if needed
    }

    suspend fun updateAdminPin(newPin: String) = withContext(Dispatchers.IO) {
        val config = dao.getAdminConfigDirect() ?: AdminConfig(id = 1)
        dao.insertOrUpdateAdminConfig(config.copy(pin = newPin))
    }
}
