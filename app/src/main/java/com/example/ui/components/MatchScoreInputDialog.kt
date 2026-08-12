package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.EsportsMatch
import com.example.data.model.MatchTeamScore
import com.example.data.model.Tournament
import com.example.ui.theme.EsportsBackground
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
fun MatchScoreInputDialog(
    match: EsportsMatch,
    tournament: Tournament,
    existingScores: List<MatchTeamScore>,
    onSaveScores: (List<Pair<Long, Pair<Int, Int>>>) -> Unit, // List<Pair<TeamId, Pair<Placement, Kills>>>
    onDismiss: () -> Unit
) {
    // Local state map: teamId -> Pair(PlacementStr, KillsStr)
    val scoresMap = remember(existingScores) {
        val map = mutableStateMapOf<Long, Pair<String, String>>()
        existingScores.forEach { score ->
            map[score.team.id] = Pair(
                if (score.placement > 0) score.placement.toString() else "",
                if (score.kills > 0 || score.placement > 0) score.kills.toString() else "0"
            )
        }
        map
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f),
            shape = RoundedCornerShape(20.dp),
            color = EsportsSurface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Score Entry: ${match.matchName}",
                            color = EsportsTextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Map: ${match.mapName} | Enter Placement (1-16) & Kills per team",
                            color = EsportsCyan,
                            fontSize = 12.sp
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_score_input")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = EsportsTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Table Header Column Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(EsportsSurfaceVariant, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("#", color = EsportsTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(30.dp))
                    Text("TEAM", color = EsportsTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Text("RANK (1-16)", color = EsportsTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(85.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("KILLS", color = EsportsTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(75.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Scrollable Teams Input List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(existingScores, key = { _, score -> score.team.id }) { index, score ->
                        val team = score.team
                        val currentPair = scoresMap[team.id] ?: Pair("", "0")

                        val placementStr = currentPair.first
                        val killsStr = currentPair.second

                        val placementInt = placementStr.toIntOrNull() ?: 0
                        val killsInt = killsStr.toIntOrNull() ?: 0

                        val calculatedPlacementPts = tournament.getPointsForPlacement(placementInt)
                        val calculatedTotalPts = (killsInt * tournament.killPointMultiplier) + calculatedPlacementPts

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (placementInt == 1) Color(0xFF1E2818) else EsportsSurfaceVariant
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                width = 1.dp,
                                color = if (placementInt == 1) EsportsBooyahGreen else EsportsCardBorder
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Slot / Index
                                Text(
                                    text = "${team.slotNumber}",
                                    color = EsportsTextMuted,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(30.dp)
                                )

                                // Team Tag / Name
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .width(10.dp)
                                                .height(10.dp)
                                                .background(
                                                    color = try { Color(android.graphics.Color.parseColor(team.logoColorHex)) } catch (e: Exception) { EsportsGold },
                                                    shape = CircleShape
                                                )
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = team.tag,
                                            color = EsportsTextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        if (placementInt == 1) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Icon(
                                                imageVector = Icons.Default.EmojiEvents,
                                                contentDescription = "Booyah",
                                                tint = EsportsGold,
                                                modifier = Modifier.height(16.dp).width(16.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${calculatedTotalPts} pts (${calculatedPlacementPts} place + ${killsInt} kills)",
                                        color = if (placementInt > 0) EsportsCyan else EsportsTextMuted,
                                        fontSize = 11.sp
                                    )
                                }

                                // Placement Input TextField
                                OutlinedTextField(
                                    value = placementStr,
                                    onValueChange = { newVal ->
                                        if (newVal.isEmpty() || (newVal.toIntOrNull() != null && newVal.toInt() in 1..16)) {
                                            scoresMap[team.id] = Pair(newVal, killsStr)
                                        }
                                    },
                                    placeholder = { Text("-") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = EsportsCyan,
                                        unfocusedBorderColor = EsportsCardBorder,
                                        focusedTextColor = EsportsTextPrimary,
                                        unfocusedTextColor = EsportsTextPrimary
                                    ),
                                    modifier = Modifier
                                        .width(85.dp)
                                        .testTag("placement_input_${team.tag}")
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                // Kills Input TextField
                                OutlinedTextField(
                                    value = killsStr,
                                    onValueChange = { newVal ->
                                        if (newVal.isEmpty() || newVal.toIntOrNull() != null) {
                                            scoresMap[team.id] = Pair(placementStr, newVal)
                                        }
                                    },
                                    placeholder = { Text("0") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = EsportsCyan,
                                        unfocusedBorderColor = EsportsCardBorder,
                                        focusedTextColor = EsportsTextPrimary,
                                        unfocusedTextColor = EsportsTextPrimary
                                    ),
                                    modifier = Modifier
                                        .width(75.dp)
                                        .testTag("kills_input_${team.tag}")
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Cancel", color = EsportsTextSecondary)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = {
                            val resultList = existingScores.map { score ->
                                val pair = scoresMap[score.team.id] ?: Pair("0", "0")
                                val placement = pair.first.toIntOrNull() ?: 0
                                val kills = pair.second.toIntOrNull() ?: 0
                                Pair(score.team.id, Pair(placement, kills))
                            }
                            onSaveScores(resultList)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EsportsCyan,
                            contentColor = Color(0xFF381E72)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("save_scores_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Save",
                            modifier = Modifier.height(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save Scores", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
