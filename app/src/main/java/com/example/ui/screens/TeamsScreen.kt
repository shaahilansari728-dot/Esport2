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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
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
import com.example.data.model.Player
import com.example.data.model.Team
import com.example.data.model.TeamWithPlayers
import com.example.data.model.Tournament
import com.example.ui.ConfirmationType
import com.example.ui.components.AddPlayerDialog
import com.example.ui.components.CreateTeamDialog
import com.example.ui.theme.EsportsBooyahGreen
import com.example.ui.theme.EsportsCardBorder
import com.example.ui.theme.EsportsCyan
import com.example.ui.theme.EsportsGold
import com.example.ui.theme.EsportsLiveRed
import com.example.ui.theme.EsportsSurface
import com.example.ui.theme.EsportsSurfaceVariant
import com.example.ui.theme.EsportsTextPrimary
import com.example.ui.theme.EsportsTextSecondary

@Composable
fun TeamsScreen(
    tournament: Tournament?,
    teamsWithPlayers: List<TeamWithPlayers>,
    isAdminUnlocked: Boolean,
    onAddTeam: (name: String, tag: String, colorHex: String, slotNumber: Int) -> Unit,
    onAddPlayer: (teamId: Long, ign: String, role: String, isCaptain: Boolean) -> Unit,
    onDeletePlayer: (playerId: Long) -> Unit,
    onRequestConfirmation: (ConfirmationType) -> Unit
) {
    var showCreateTeamDialog by remember { mutableStateOf(false) }
    var teamForNewPlayer by remember { mutableStateOf<Team?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PARTICIPATING TEAMS (${teamsWithPlayers.size})",
                    color = EsportsCyan,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                if (isAdminUnlocked) {
                    Button(
                        onClick = { showCreateTeamDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = EsportsCyan, contentColor = Color(0xFF381E72)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("add_team_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add", modifier = Modifier.height(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Team", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (teamsWithPlayers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No teams registered in this tournament yet.", color = EsportsTextSecondary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(teamsWithPlayers, key = { it.team.id }) { item ->
                        TeamCardItem(
                            teamWithPlayers = item,
                            isAdminUnlocked = isAdminUnlocked,
                            onAddPlayerToTeam = { teamForNewPlayer = item.team },
                            onDeletePlayer = onDeletePlayer,
                            onDeleteTeam = { onRequestConfirmation(ConfirmationType.DeleteTeam(item.team)) }
                        )
                    }
                }
            }
        }
    }

    // Create Team Dialog
    if (showCreateTeamDialog) {
        CreateTeamDialog(
            existingTeamCount = teamsWithPlayers.size,
            onCreate = { name, tag, colorHex, slot ->
                onAddTeam(name, tag, colorHex, slot)
            },
            onDismiss = { showCreateTeamDialog = false }
        )
    }

    // Add Player Dialog
    teamForNewPlayer?.let { team ->
        AddPlayerDialog(
            teamName = team.name,
            onAdd = { ign, role, isCaptain ->
                onAddPlayer(team.id, ign, role, isCaptain)
            },
            onDismiss = { teamForNewPlayer = null }
        )
    }
}

@Composable
private fun TeamCardItem(
    teamWithPlayers: TeamWithPlayers,
    isAdminUnlocked: Boolean,
    onAddPlayerToTeam: () -> Unit,
    onDeletePlayer: (Long) -> Unit,
    onDeleteTeam: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val team = teamWithPlayers.team
    val players = teamWithPlayers.players
    val stats = teamWithPlayers.leaderboardStats

    Card(
        colors = CardDefaults.cardColors(
            containerColor = EsportsSurface,
            contentColor = EsportsTextPrimary,
            disabledContainerColor = EsportsSurface,
            disabledContentColor = EsportsTextPrimary
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, EsportsCardBorder),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("team_card_${team.tag}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Team Badge & Name
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .width(14.dp)
                            .height(14.dp)
                            .background(
                                color = try { Color(android.graphics.Color.parseColor(team.logoColorHex)) } catch (e: Exception) { EsportsGold },
                                shape = CircleShape
                            )
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${team.tag} (${team.name})",
                                color = EsportsTextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Slot #${team.slotNumber} | ${players.size} Players",
                            color = EsportsTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                // Stats & Expand Button
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (stats != null) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "#${stats.rank} RANK",
                                color = EsportsGold,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "${stats.totalPoints} PTS",
                                color = EsportsCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Expand Roster",
                            tint = EsportsTextSecondary
                        )
                    }
                }
            }

            // Expanded Roster
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PLAYER ROSTER:",
                            color = EsportsCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )

                        if (isAdminUnlocked) {
                            Button(
                                onClick = onAddPlayerToTeam,
                                colors = ButtonDefaults.buttonColors(containerColor = EsportsSurfaceVariant, contentColor = EsportsCyan),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.testTag("add_player_btn_${team.tag}")
                            ) {
                                Icon(imageVector = Icons.Default.PersonAdd, contentDescription = "Add", modifier = Modifier.height(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Player", fontSize = 10.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    if (players.isEmpty()) {
                        Text("No players assigned to roster.", color = EsportsTextSecondary, fontSize = 12.sp)
                    } else {
                        players.forEach { player ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(EsportsSurfaceVariant, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                    .padding(bottom = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (player.isCaptain) Icons.Default.MilitaryTech else Icons.Default.Person,
                                        contentDescription = "Role",
                                        tint = if (player.isCaptain) EsportsGold else EsportsTextSecondary,
                                        modifier = Modifier.height(16.dp).width(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = player.ign,
                                        color = EsportsTextPrimary,
                                        fontWeight = if (player.isCaptain) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 13.sp
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = player.role,
                                        color = EsportsTextSecondary,
                                        fontSize = 11.sp
                                    )

                                    if (isAdminUnlocked) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        IconButton(
                                            onClick = { onDeletePlayer(player.id) },
                                            modifier = Modifier.height(24.dp).width(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete Player",
                                                tint = EsportsLiveRed,
                                                modifier = Modifier.height(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }

                    if (isAdminUnlocked) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            OutlinedButton(
                                onClick = onDeleteTeam,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = EsportsLiveRed),
                                border = androidx.compose.foundation.BorderStroke(1.dp, EsportsLiveRed),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.testTag("delete_team_btn_${team.tag}")
                            ) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.height(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Delete Team", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
