package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EsportsMatch
import com.example.data.model.MatchStatus
import com.example.data.model.MatchWithResults
import com.example.data.model.Tournament
import com.example.ui.ConfirmationType
import com.example.ui.components.CreateMatchDialog
import com.example.ui.components.MatchScoreInputDialog
import com.example.ui.theme.EsportsBooyahGreen
import com.example.ui.theme.EsportsCardBorder
import com.example.ui.theme.EsportsCyan
import com.example.ui.theme.EsportsGold
import com.example.ui.theme.EsportsLiveRed
import com.example.ui.theme.EsportsSurface
import com.example.ui.theme.EsportsSurfaceVariant
import com.example.ui.theme.EsportsTextMuted
import com.example.ui.theme.EsportsTextPrimary
import com.example.ui.theme.EsportsTextSecondary

@Composable
fun MatchesScreen(
    tournament: Tournament?,
    matches: List<MatchWithResults>,
    isAdminUnlocked: Boolean,
    onSaveScores: (Long, List<Pair<Long, Pair<Int, Int>>>) -> Unit,
    onRequestConfirmation: (ConfirmationType) -> Unit,
    onAddMatch: (matchName: String, mapName: String) -> Unit,
    onUpdateMatchStatus: (EsportsMatch, MatchStatus) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("ALL") }
    var matchToScore by remember { mutableStateOf<MatchWithResults?>(null) }
    var showCreateMatchDialog by remember { mutableStateOf(false) }

    val filteredMatches = remember(matches, selectedFilter) {
        when (selectedFilter) {
            "LIVE" -> matches.filter { it.match.status == MatchStatus.LIVE }
            "FINISHED" -> matches.filter { it.match.status == MatchStatus.FINISHED }
            "UPCOMING" -> matches.filter { it.match.status == MatchStatus.UPCOMING }
            else -> matches
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Filter Pills Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("ALL", "LIVE", "FINISHED", "UPCOMING").forEach { filter ->
                    val isSelected = selectedFilter == filter
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (isSelected) EsportsCyan else EsportsSurfaceVariant,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable { selectedFilter = filter }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                            .testTag("filter_match_$filter"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = filter,
                            color = if (isSelected) Color(0xFF381E72) else EsportsTextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredMatches.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No matches found in '$selectedFilter' category.",
                        color = EsportsTextSecondary,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredMatches, key = { it.match.id }) { matchWithResults ->
                        MatchCardItem(
                            matchWithResults = matchWithResults,
                            isAdminUnlocked = isAdminUnlocked,
                            onEnterScores = { matchToScore = matchWithResults },
                            onEndMatch = { onRequestConfirmation(ConfirmationType.EndMatch(matchWithResults.match)) },
                            onResetScores = { onRequestConfirmation(ConfirmationType.ResetMatchScore(matchWithResults.match)) },
                            onDeleteMatch = { onRequestConfirmation(ConfirmationType.DeleteMatch(matchWithResults.match)) },
                            onStartMatch = { onUpdateMatchStatus(matchWithResults.match, MatchStatus.LIVE) }
                        )
                    }
                }
            }
        }

        // Floating Action Button to Add Match (Admin only)
        if (isAdminUnlocked) {
            FloatingActionButton(
                onClick = { showCreateMatchDialog = true },
                containerColor = EsportsCyan,
                contentColor = Color(0xFF381E72),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .testTag("add_match_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Match")
            }
        }
    }

    // Match Score Entry Sheet/Dialog
    matchToScore?.let { matchWithRes ->
        tournament?.let { tourney ->
            MatchScoreInputDialog(
                match = matchWithRes.match,
                tournament = tourney,
                existingScores = matchWithRes.results,
                onSaveScores = { teamScores ->
                    onSaveScores(matchWithRes.match.id, teamScores)
                },
                onDismiss = { matchToScore = null }
            )
        }
    }

    // Create Match Dialog
    if (showCreateMatchDialog) {
        CreateMatchDialog(
            matchNumber = matches.size + 1,
            onCreate = { name, map ->
                onAddMatch(name, map)
            },
            onDismiss = { showCreateMatchDialog = false }
        )
    }
}

@Composable
private fun MatchCardItem(
    matchWithResults: MatchWithResults,
    isAdminUnlocked: Boolean,
    onEnterScores: () -> Unit,
    onEndMatch: () -> Unit,
    onResetScores: () -> Unit,
    onDeleteMatch: () -> Unit,
    onStartMatch: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val match = matchWithResults.match
    val scores = matchWithResults.results

    val statusBg = when (match.status) {
        MatchStatus.LIVE -> EsportsLiveRed
        MatchStatus.FINISHED -> EsportsBooyahGreen
        MatchStatus.UPCOMING -> Color(0xFF3B82F6)
    }

    val statusText = when (match.status) {
        MatchStatus.LIVE -> "LIVE"
        MatchStatus.FINISHED -> "FINISHED"
        MatchStatus.UPCOMING -> "UPCOMING"
    }

    val booyahTeam = scores.find { it.booyah || it.placement == 1 }

    Card(
        colors = CardDefaults.cardColors(containerColor = EsportsSurface),
        border = androidx.compose.foundation.BorderStroke(
            width = if (match.status == MatchStatus.LIVE) 2.dp else 1.dp,
            color = if (match.status == MatchStatus.LIVE) EsportsLiveRed else EsportsCardBorder
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("match_card_${match.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Card Top Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .background(statusBg, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = statusText,
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .background(EsportsSurfaceVariant, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "MAP: ${match.mapName}",
                                color = EsportsCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = match.matchName,
                        color = EsportsTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.testTag("expand_match_${match.id}")
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand",
                        tint = EsportsTextSecondary
                    )
                }
            }

            // Winner/Booyah banner if finished
            if (match.status == MatchStatus.FINISHED && booyahTeam != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E2818), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Booyah",
                        tint = EsportsGold,
                        modifier = Modifier.height(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "BOOYAH: ${booyahTeam.team.name} (${booyahTeam.kills} Kills)",
                        color = EsportsBooyahGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            // Expanded Scoreboard Breakdown
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Text(
                        text = "MATCH SCORECARD & PLACEMENTS:",
                        color = EsportsCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    scores.take(16).forEach { score ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (score.placement > 0) "#${score.placement}" else "-",
                                    color = if (score.placement == 1) EsportsGold else EsportsTextMuted,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.width(30.dp)
                                )
                                Text(
                                    text = "${score.team.tag} (${score.team.name})",
                                    color = EsportsTextPrimary,
                                    fontSize = 13.sp
                                )
                            }

                            Text(
                                text = "${score.totalPoints} PTS (${score.kills} Kills)",
                                color = EsportsCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Admin Action Row
            if (isAdminUnlocked) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = onEnterScores,
                            colors = ButtonDefaults.buttonColors(containerColor = EsportsCyan, contentColor = Color(0xFF381E72)),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.testTag("enter_scores_btn_${match.id}")
                        ) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.height(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Scores", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        if (match.status == MatchStatus.UPCOMING) {
                            Button(
                                onClick = onStartMatch,
                                colors = ButtonDefaults.buttonColors(containerColor = EsportsLiveRed, contentColor = Color.White),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Start", modifier = Modifier.height(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Go Live", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        } else if (match.status == MatchStatus.LIVE) {
                            Button(
                                onClick = onEndMatch,
                                colors = ButtonDefaults.buttonColors(containerColor = EsportsBooyahGreen, contentColor = Color.Black),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "End", modifier = Modifier.height(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Finish", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(onClick = onResetScores, modifier = Modifier.testTag("reset_scores_btn_${match.id}")) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset", tint = EsportsGold)
                        }
                        IconButton(onClick = onDeleteMatch, modifier = Modifier.testTag("delete_match_btn_${match.id}")) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = EsportsLiveRed)
                        }
                    }
                }
            }
        }
    }
}
