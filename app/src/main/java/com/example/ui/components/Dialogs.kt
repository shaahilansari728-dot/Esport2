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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ui.theme.EsportsCyan
import com.example.ui.theme.EsportsGold
import com.example.ui.theme.EsportsLiveRed
import com.example.ui.theme.EsportsSurface
import com.example.ui.theme.EsportsSurfaceVariant
import com.example.ui.theme.EsportsTextPrimary
import com.example.ui.theme.EsportsTextSecondary

// 1. Create Tournament Dialog
@Composable
fun CreateTournamentDialog(
    onCreate: (name: String, gameTitle: String, description: String, killMultiplier: Int, placementPointsCsv: String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var gameTitle by remember { mutableStateOf("Free Fire") }
    var description by remember { mutableStateOf("") }
    var killMultiplier by remember { mutableIntStateOf(1) }
    var pointsCsv by remember { mutableStateOf("12,9,8,7,6,5,4,3,2,1,0,0,0,0,0,0") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = EsportsSurface,
        title = {
            Text("Create New Tournament", color = EsportsTextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tournament Name") },
                    placeholder = { Text("e.g. Free Fire Pro Series") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EsportsCyan,
                        focusedTextColor = EsportsTextPrimary,
                        unfocusedTextColor = EsportsTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("tournament_name_input")
                )

                OutlinedTextField(
                    value = gameTitle,
                    onValueChange = { gameTitle = it },
                    label = { Text("Game Title / Match Type") },
                    placeholder = { Text("e.g. Free Fire / Custom Match") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EsportsCyan,
                        focusedTextColor = EsportsTextPrimary,
                        unfocusedTextColor = EsportsTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("game_title_input")
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    placeholder = { Text("e.g. 16 Teams, 5 Matches Battle Royale") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EsportsCyan,
                        focusedTextColor = EsportsTextPrimary,
                        unfocusedTextColor = EsportsTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("tournament_desc_input")
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Kill Point Value:", color = EsportsTextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = killMultiplier.toString(),
                        onValueChange = { killMultiplier = it.toIntOrNull() ?: 1 },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EsportsCyan,
                            focusedTextColor = EsportsTextPrimary,
                            unfocusedTextColor = EsportsTextPrimary
                        ),
                        modifier = Modifier.width(70.dp).testTag("kill_pts_input")
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("pt/kill", color = EsportsTextSecondary, fontSize = 12.sp)
                }

                OutlinedTextField(
                    value = pointsCsv,
                    onValueChange = { pointsCsv = it },
                    label = { Text("Placement Points Table (1st..16th CSV)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EsportsCyan,
                        focusedTextColor = EsportsTextPrimary,
                        unfocusedTextColor = EsportsTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("placement_csv_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onCreate(name, gameTitle, description, killMultiplier, pointsCsv)
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EsportsCyan, contentColor = Color(0xFF381E72)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("submit_create_tournament")
            ) {
                Text("Create Tournament", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(8.dp)) {
                Text("Cancel", color = EsportsTextSecondary)
            }
        }
    )
}

// 2. Create Team Dialog
@Composable
fun CreateTeamDialog(
    existingTeamCount: Int,
    onCreate: (name: String, tag: String, colorHex: String, slotNumber: Int) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var tag by remember { mutableStateOf("") }
    var slotNumber by remember { mutableIntStateOf(existingTeamCount + 1) }

    val presetColors = listOf("#00E5FF", "#FFB800", "#FF2A5F", "#22C55E", "#8B5CF6", "#EC4899", "#3B82F6", "#F59E0B")
    var selectedColor by remember { mutableStateOf(presetColors[existingTeamCount % presetColors.size]) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = EsportsSurface,
        title = { Text("Add Team", color = EsportsTextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Team Name") },
                    placeholder = { Text("e.g. EVOS Divine") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EsportsCyan,
                        focusedTextColor = EsportsTextPrimary,
                        unfocusedTextColor = EsportsTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("team_name_input")
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = tag,
                        onValueChange = { tag = it.take(6).uppercase() },
                        label = { Text("Tag/Code") },
                        placeholder = { Text("EVOS") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EsportsCyan,
                            focusedTextColor = EsportsTextPrimary,
                            unfocusedTextColor = EsportsTextPrimary
                        ),
                        modifier = Modifier.weight(1f).testTag("team_tag_input")
                    )

                    OutlinedTextField(
                        value = slotNumber.toString(),
                        onValueChange = { slotNumber = it.toIntOrNull() ?: 1 },
                        label = { Text("Slot #") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EsportsCyan,
                            focusedTextColor = EsportsTextPrimary,
                            unfocusedTextColor = EsportsTextPrimary
                        ),
                        modifier = Modifier.width(80.dp).testTag("team_slot_input")
                    )
                }

                Text("Team Badge Color:", color = EsportsTextSecondary, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    presetColors.forEach { hex ->
                        val color = try { Color(android.graphics.Color.parseColor(hex)) } catch (e: Exception) { EsportsGold }
                        Box(
                            modifier = Modifier
                                .width(28.dp)
                                .height(28.dp)
                                .background(color, CircleShape)
                                .clickable { selectedColor = hex }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && tag.isNotBlank()) {
                        onCreate(name, tag, selectedColor, slotNumber)
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EsportsCyan, contentColor = Color(0xFF381E72)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("submit_create_team")
            ) {
                Text("Add Team", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(8.dp)) {
                Text("Cancel", color = EsportsTextSecondary)
            }
        }
    )
}

// 3. Add Player Dialog
@Composable
fun AddPlayerDialog(
    teamName: String,
    onAdd: (ign: String, role: String, isCaptain: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var ign by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("Rusher") }
    var isCaptain by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = EsportsSurface,
        title = { Text("Add Player to $teamName", color = EsportsTextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = ign,
                    onValueChange = { ign = it },
                    label = { Text("In-Game Name (IGN)") },
                    placeholder = { Text("e.g. EVOS_Sam13") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EsportsCyan,
                        focusedTextColor = EsportsTextPrimary,
                        unfocusedTextColor = EsportsTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("player_ign_input")
                )

                OutlinedTextField(
                    value = role,
                    onValueChange = { role = it },
                    label = { Text("Role") },
                    placeholder = { Text("e.g. Captain, Rusher, Sniper") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EsportsCyan,
                        focusedTextColor = EsportsTextPrimary,
                        unfocusedTextColor = EsportsTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("player_role_input")
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isCaptain,
                        onCheckedChange = { isCaptain = it },
                        colors = CheckboxDefaults.colors(checkedColor = EsportsCyan)
                    )
                    Text("Set as Team Captain / IGL", color = EsportsTextPrimary, fontSize = 14.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (ign.isNotBlank()) {
                        onAdd(ign, role, isCaptain)
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EsportsCyan, contentColor = Color(0xFF381E72)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("submit_add_player")
            ) {
                Text("Add Player", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(8.dp)) {
                Text("Cancel", color = EsportsTextSecondary)
            }
        }
    )
}

// 4. Create Match Dialog
@Composable
fun CreateMatchDialog(
    matchNumber: Int,
    onCreate: (matchName: String, mapName: String) -> Unit,
    onDismiss: () -> Unit
) {
    var matchName by remember { mutableStateOf("Match $matchNumber") }
    var mapName by remember { mutableStateOf("Bermuda") }

    val presetMaps = listOf("Bermuda", "Purgatory", "Kalahari", "Alpine", "Nexterra", "Custom Map")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = EsportsSurface,
        title = { Text("Create New Match", color = EsportsTextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = matchName,
                    onValueChange = { matchName = it },
                    label = { Text("Match Title") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EsportsCyan,
                        focusedTextColor = EsportsTextPrimary,
                        unfocusedTextColor = EsportsTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("match_title_input")
                )

                OutlinedTextField(
                    value = mapName,
                    onValueChange = { mapName = it },
                    label = { Text("Map Name") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EsportsCyan,
                        focusedTextColor = EsportsTextPrimary,
                        unfocusedTextColor = EsportsTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("match_map_input")
                )

                Text("Quick Select Map:", color = EsportsTextSecondary, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    presetMaps.take(3).forEach { m ->
                        Button(
                            onClick = {
                                mapName = m
                                matchName = "Match $matchNumber - $m"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EsportsSurfaceVariant, contentColor = EsportsCyan),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(m, fontSize = 11.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onCreate(matchName, mapName)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = EsportsCyan, contentColor = Color(0xFF381E72)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("submit_create_match")
            ) {
                Text("Create Match", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(8.dp)) {
                Text("Cancel", color = EsportsTextSecondary)
            }
        }
    )
}

// 5. Change Admin PIN Dialog
@Composable
fun ChangePinDialog(
    onChangePin: (newPin: String) -> Unit,
    onDismiss: () -> Unit
) {
    var newPin by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = EsportsSurface,
        title = { Text("Change Admin PIN", color = EsportsTextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
        text = {
            Column {
                Text("Set a new security PIN for Admin management controls.", color = EsportsTextSecondary, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = newPin,
                    onValueChange = { newPin = it },
                    label = { Text("New Admin PIN") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EsportsCyan,
                        focusedTextColor = EsportsTextPrimary,
                        unfocusedTextColor = EsportsTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("new_pin_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newPin.isNotBlank()) {
                        onChangePin(newPin)
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EsportsCyan, contentColor = Color(0xFF381E72)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Save PIN", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(8.dp)) {
                Text("Cancel", color = EsportsTextSecondary)
            }
        }
    )
}
