package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.EsportsViewModel
import com.example.ui.components.AdminPinModal
import com.example.ui.components.ConfirmationDialog
import com.example.ui.components.EsportsBottomNav
import com.example.ui.components.EsportsHeader
import com.example.ui.components.ShareSummaryDialog
import com.example.ui.screens.AdminCenterScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.MatchesScreen
import com.example.ui.screens.TeamsScreen
import com.example.ui.theme.EsportsBackground
import com.example.ui.theme.EsportsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EsportsTheme {
                EsportsApp()
            }
        }
    }
}

@Composable
fun EsportsApp(
    viewModel: EsportsViewModel = viewModel()
) {
    val activeTournament by viewModel.activeTournament.collectAsStateWithLifecycle()
    val allTournaments by viewModel.allTournaments.collectAsStateWithLifecycle()
    val leaderboard by viewModel.leaderboard.collectAsStateWithLifecycle()
    val teamsWithPlayers by viewModel.teamsWithPlayers.collectAsStateWithLifecycle()
    val matchesWithResults by viewModel.matchesWithResults.collectAsStateWithLifecycle()
    val isAdminUnlocked by viewModel.isAdminUnlocked.collectAsStateWithLifecycle()
    val activeConfirmation by viewModel.activeConfirmation.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()

    var showShareModal by remember { mutableStateOf(false) }
    var showAdminPinModal by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(EsportsBackground),
        containerColor = EsportsBackground,
        topBar = {
            EsportsHeader(
                activeTournament = activeTournament,
                allTournaments = allTournaments,
                isAdminUnlocked = isAdminUnlocked,
                onSelectTournament = { id -> viewModel.switchActiveTournament(id) },
                onToggleAdminLock = {
                    if (isAdminUnlocked) {
                        viewModel.lockAdmin()
                    } else {
                        showAdminPinModal = true
                    }
                },
                onOpenShareDialog = { showShareModal = true }
            )
        },
        bottomBar = {
            EsportsBottomNav(
                selectedTab = selectedTab,
                onTabSelected = { tab -> viewModel.setSelectedTab(tab) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(EsportsBackground)
        ) {
            when (selectedTab) {
                0 -> LeaderboardScreen(
                    tournament = activeTournament,
                    leaderboard = leaderboard,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { q -> viewModel.updateSearchQuery(q) }
                )
                1 -> MatchesScreen(
                    tournament = activeTournament,
                    matches = matchesWithResults,
                    isAdminUnlocked = isAdminUnlocked,
                    onSaveScores = { matchId, scores -> viewModel.saveMatchResults(matchId, scores) },
                    onRequestConfirmation = { type -> viewModel.requestConfirmation(type) },
                    onAddMatch = { name, map -> viewModel.addMatch(name, map) },
                    onUpdateMatchStatus = { match, status -> viewModel.updateMatchStatus(match, status) }
                )
                2 -> TeamsScreen(
                    tournament = activeTournament,
                    teamsWithPlayers = teamsWithPlayers,
                    isAdminUnlocked = isAdminUnlocked,
                    onAddTeam = { name, tag, hex, slot -> viewModel.addTeam(name, tag, hex, slot) },
                    onAddPlayer = { teamId, ign, role, isCaptain -> viewModel.addPlayer(teamId, ign, role, isCaptain) },
                    onDeletePlayer = { id -> viewModel.deletePlayer(id) },
                    onRequestConfirmation = { type -> viewModel.requestConfirmation(type) }
                )
                3 -> AdminCenterScreen(
                    isAdminUnlocked = isAdminUnlocked,
                    activeTournament = activeTournament,
                    allTournaments = allTournaments,
                    onUnlockAdmin = { pin -> viewModel.unlockAdmin(pin) },
                    onLockAdmin = { viewModel.lockAdmin() },
                    onChangePin = { newPin -> viewModel.changeAdminPin(newPin) },
                    onCreateTournament = { name, game, desc, mult, csv ->
                        viewModel.createTournament(name, game, desc, mult, csv)
                    },
                    onSelectTournament = { id -> viewModel.switchActiveTournament(id) },
                    onRequestConfirmation = { type -> viewModel.requestConfirmation(type) },
                    onResetTestData = { viewModel.resetDatabaseToTestData() }
                )
            }
        }
    }

    // Modal Confirmation Dialogs for destructive / critical admin actions
    activeConfirmation?.let { confirmation ->
        ConfirmationDialog(
            confirmationType = confirmation,
            onConfirm = { viewModel.executeConfirmedAction() },
            onDismiss = { viewModel.dismissConfirmation() }
        )
    }

    // Admin PIN Modal
    if (showAdminPinModal) {
        AdminPinModal(
            onUnlock = { pin ->
                val ok = viewModel.unlockAdmin(pin)
                ok
            },
            onDismiss = { showAdminPinModal = false }
        )
    }

    // Share Standings Modal
    if (showShareModal) {
        ShareSummaryDialog(
            tournament = activeTournament,
            leaderboard = leaderboard,
            onDismiss = { showShareModal = false }
        )
    }
}
