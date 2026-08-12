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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.window.Dialog
import com.example.data.model.TeamLeaderboardEntry
import com.example.data.model.Tournament
import com.example.ui.theme.EsportsBooyahGreen
import com.example.ui.theme.EsportsBronze
import com.example.ui.theme.EsportsCardBorder
import com.example.ui.theme.EsportsCyan
import com.example.ui.theme.EsportsGold
import com.example.ui.theme.EsportsSilver
import com.example.ui.theme.EsportsSurface
import com.example.ui.theme.EsportsSurfaceVariant
import com.example.ui.theme.EsportsTextMuted
import com.example.ui.theme.EsportsTextPrimary
import com.example.ui.theme.EsportsTextSecondary

@Composable
fun LeaderboardScreen(
    tournament: Tournament?,
    leaderboard: List<TeamLeaderboardEntry>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    var selectedTeamForDetail by remember { mutableStateOf<TeamLeaderboardEntry?>(null) }

    val filteredList = remember(leaderboard, searchQuery) {
        if (searchQuery.isBlank()) leaderboard
        else leaderboard.filter {
            it.team.name.contains(searchQuery, ignoreCase = true) ||
                    it.team.tag.contains(searchQuery, ignoreCase = true)
        }
    }

    val top1 = leaderboard.getOrNull(0)
    val top2 = leaderboard.getOrNull(1)
    val top3 = leaderboard.getOrNull(2)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Search Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Search team by name or tag...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = EsportsCyan
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = EsportsTextSecondary)
                    }
                }
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EsportsCyan,
                unfocusedBorderColor = EsportsCardBorder,
                focusedContainerColor = EsportsSurfaceVariant,
                unfocusedContainerColor = EsportsSurfaceVariant,
                focusedTextColor = EsportsTextPrimary,
                unfocusedTextColor = EsportsTextPrimary
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("leaderboard_search_input")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Top 3 Podium Row (only shown if search is empty and we have at least 3 teams)
        if (searchQuery.isBlank() && leaderboard.size >= 3) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                // 2nd Place
                PodiumCard(
                    entry = top2,
                    rank = 2,
                    accentColor = EsportsSilver,
                    modifier = Modifier.weight(1f)
                ) {
                    selectedTeamForDetail = top2
                }

                // 1st Place (Center Hero)
                PodiumCard(
                    entry = top1,
                    rank = 1,
                    accentColor = EsportsGold,
                    isHero = true,
                    modifier = Modifier.weight(1.1f)
                ) {
                    selectedTeamForDetail = top1
                }

                // 3rd Place
                PodiumCard(
                    entry = top3,
                    rank = 3,
                    accentColor = EsportsBronze,
                    modifier = Modifier.weight(1f)
                ) {
                    selectedTeamForDetail = top3
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        // Table Column Headers
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(EsportsSurfaceVariant, RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("#", color = EsportsTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(28.dp))
            Text("TEAM", color = EsportsTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text("MP", color = EsportsTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(32.dp))
            Text("BOOYAH", color = EsportsTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(55.dp))
            Text("KILLS", color = EsportsTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(48.dp))
            Text("PTS", color = EsportsCyan, fontSize = 12.sp, fontWeight = FontWeight.Black, modifier = Modifier.width(44.dp))
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Full Standings List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            itemsIndexed(filteredList, key = { _, item -> item.team.id }) { index, entry ->
                LeaderboardRow(
                    entry = entry,
                    onClick = { selectedTeamForDetail = entry }
                )
            }
        }
    }

    // Detail Modal when team clicked
    selectedTeamForDetail?.let { entry ->
        TeamDetailDialog(
            entry = entry,
            onDismiss = { selectedTeamForDetail = null }
        )
    }
}

@Composable
private fun PodiumCard(
    entry: TeamLeaderboardEntry?,
    rank: Int,
    accentColor: Color,
    isHero: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    if (entry == null) return

    Card(
        modifier = modifier
            .clickable { onClick() }
            .testTag("podium_card_rank_$rank"),
        colors = CardDefaults.cardColors(containerColor = EsportsSurfaceVariant),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isHero) 2.dp else 1.dp,
            color = accentColor
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = if (isHero) 12.dp else 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Rank Crown Badge
            Box(
                modifier = Modifier
                    .height(24.dp)
                    .width(24.dp)
                    .background(accentColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$rank",
                    color = Color.Black,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = entry.team.tag,
                color = EsportsTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = if (isHero) 16.sp else 14.sp
            )

            Text(
                text = entry.team.name,
                color = EsportsTextSecondary,
                fontSize = 10.sp,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Booyah",
                    tint = EsportsBooyahGreen,
                    modifier = Modifier.height(14.dp).width(14.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "${entry.booyahCount} W",
                    color = EsportsBooyahGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(6.dp))

                Icon(
                    imageVector = Icons.Default.Whatshot,
                    contentDescription = "Kills",
                    tint = EsportsCyan,
                    modifier = Modifier.height(14.dp).width(14.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "${entry.totalKills}",
                    color = EsportsCyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${entry.totalPoints} PTS",
                color = accentColor,
                fontWeight = FontWeight.Black,
                fontSize = if (isHero) 16.sp else 13.sp
            )
        }
    }
}

@Composable
private fun LeaderboardRow(
    entry: TeamLeaderboardEntry,
    onClick: () -> Unit
) {
    val rankColor = when (entry.rank) {
        1 -> EsportsGold
        2 -> EsportsSilver
        3 -> EsportsBronze
        else -> EsportsTextSecondary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("leaderboard_row_${entry.team.tag}"),
        colors = CardDefaults.cardColors(containerColor = EsportsSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, EsportsCardBorder),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank Number
            Text(
                text = "${entry.rank}",
                color = rankColor,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                modifier = Modifier.width(28.dp)
            )

            // Team Badge & Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .width(10.dp)
                        .height(10.dp)
                        .background(
                            color = try { Color(android.graphics.Color.parseColor(entry.team.logoColorHex)) } catch (e: Exception) { EsportsGold },
                            shape = CircleShape
                        )
                )
                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = entry.team.tag,
                            color = EsportsTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        if (entry.rank == 1) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "Crown",
                                tint = EsportsGold,
                                modifier = Modifier.height(14.dp).width(14.dp)
                            )
                        }
                    }
                    Text(
                        text = entry.team.name,
                        color = EsportsTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            // MP
            Text(
                text = "${entry.matchesPlayed}",
                color = EsportsTextPrimary,
                fontSize = 13.sp,
                modifier = Modifier.width(32.dp)
            )

            // Booyah
            Text(
                text = "${entry.booyahCount}",
                color = EsportsBooyahGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier.width(55.dp)
            )

            // Kills
            Text(
                text = "${entry.totalKills}",
                color = EsportsTextPrimary,
                fontSize = 13.sp,
                modifier = Modifier.width(48.dp)
            )

            // Total Points
            Text(
                text = "${entry.totalPoints}",
                color = EsportsCyan,
                fontWeight = FontWeight.Black,
                fontSize = 15.sp,
                modifier = Modifier.width(44.dp)
            )
        }
    }
}

@Composable
private fun TeamDetailDialog(
    entry: TeamLeaderboardEntry,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = EsportsSurface,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = entry.team.name,
                            color = EsportsTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "TAG: ${entry.team.tag} | Slot #${entry.team.slotNumber}",
                            color = EsportsCyan,
                            fontSize = 12.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = EsportsTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(EsportsSurfaceVariant, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("RANK", color = EsportsTextMuted, fontSize = 10.sp)
                        Text("#${entry.rank}", color = EsportsGold, fontWeight = FontWeight.Black, fontSize = 18.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("TOTAL PTS", color = EsportsTextMuted, fontSize = 10.sp)
                        Text("${entry.totalPoints}", color = EsportsCyan, fontWeight = FontWeight.Black, fontSize = 18.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("BOOYAH", color = EsportsTextMuted, fontSize = 10.sp)
                        Text("${entry.booyahCount}", color = EsportsBooyahGreen, fontWeight = FontWeight.Black, fontSize = 18.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("KILLS", color = EsportsTextMuted, fontSize = 10.sp)
                        Text("${entry.totalKills}", color = EsportsTextPrimary, fontWeight = FontWeight.Black, fontSize = 18.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                entry.topPlayer?.let { playerIgn ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(EsportsSurfaceVariant, RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MilitaryTech,
                            contentDescription = "Captain",
                            tint = EsportsGold,
                            modifier = Modifier.height(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Captain / IGL: $playerIgn",
                            color = EsportsTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
