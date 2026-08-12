package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.data.model.Tournament
import com.example.ui.theme.EsportsBooyahGreen
import com.example.ui.theme.EsportsCyan
import com.example.ui.theme.EsportsGold
import com.example.ui.theme.EsportsLiveRed
import com.example.ui.theme.EsportsSurface
import com.example.ui.theme.EsportsSurfaceVariant
import com.example.ui.theme.EsportsTextMuted
import com.example.ui.theme.EsportsTextPrimary
import com.example.ui.theme.EsportsTextSecondary

@Composable
fun EsportsHeader(
    activeTournament: Tournament?,
    allTournaments: List<Tournament>,
    isAdminUnlocked: Boolean,
    onSelectTournament: (Long) -> Unit,
    onToggleAdminLock: () -> Unit,
    onOpenShareDialog: () -> Unit
) {
    var dropdownExpanded by remember { mutableStateOf(false) }

    Surface(
        color = EsportsSurface,
        tonalElevation = 6.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Logo & App Identity
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .height(36.dp)
                            .width(36.dp)
                            .background(
                                color = EsportsCyan.copy(alpha = 0.2f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Esports Shield",
                            tint = EsportsCyan,
                            modifier = Modifier.height(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "ESPORTS HUB",
                            color = EsportsCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "LIVE SCOREBOARD",
                            color = EsportsTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Right Admin Lock Badge & Share Button
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Admin Auth Badge
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (isAdminUnlocked) EsportsBooyahGreen.copy(alpha = 0.2f) else EsportsSurfaceVariant,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable { onToggleAdminLock() }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .testTag("admin_lock_badge"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isAdminUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                                contentDescription = "Lock",
                                tint = if (isAdminUnlocked) EsportsBooyahGreen else EsportsTextSecondary,
                                modifier = Modifier.height(14.dp).width(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isAdminUnlocked) "ADMIN MODE" else "PUBLIC READ-ONLY",
                                color = if (isAdminUnlocked) EsportsBooyahGreen else EsportsTextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onOpenShareDialog,
                        modifier = Modifier.testTag("share_standings_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = EsportsGold,
                            modifier = Modifier.height(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tournament Selector Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EsportsSurfaceVariant, RoundedCornerShape(10.dp))
                    .clickable { dropdownExpanded = true }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .testTag("tournament_dropdown_trigger")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Text(
                            text = "TOURNAMENT: ",
                            color = EsportsTextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = activeTournament?.name ?: "Select Tournament",
                            color = EsportsGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown",
                        tint = EsportsGold
                    )
                }

                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .background(EsportsSurface)
                ) {
                    allTournaments.forEach { tourney ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(
                                        text = tourney.name,
                                        color = if (tourney.id == activeTournament?.id) EsportsCyan else EsportsTextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${tourney.gameTitle} | ${tourney.description}",
                                        color = EsportsTextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            },
                            onClick = {
                                onSelectTournament(tourney.id)
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
