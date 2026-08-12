package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Tournament
import com.example.ui.ConfirmationType
import com.example.ui.components.ChangePinDialog
import com.example.ui.components.CreateTournamentDialog
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
fun AdminCenterScreen(
    isAdminUnlocked: Boolean,
    activeTournament: Tournament?,
    allTournaments: List<Tournament>,
    onUnlockAdmin: (String) -> Boolean,
    onLockAdmin: () -> Unit,
    onChangePin: (String) -> Unit,
    onCreateTournament: (name: String, gameTitle: String, description: String, killMultiplier: Int, placementPointsCsv: String) -> Unit,
    onSelectTournament: (Long) -> Unit,
    onRequestConfirmation: (ConfirmationType) -> Unit,
    onResetTestData: () -> Unit
) {
    val context = LocalContext.current
    var pinInput by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf<String?>(null) }
    var showCreateTournamentDialog by remember { mutableStateOf(false) }
    var showChangePinDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Admin Auth Status Card
        Card(
            colors = CardDefaults.cardColors(containerColor = EsportsSurface),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.5.dp,
                color = if (isAdminUnlocked) EsportsBooyahGreen else EsportsLiveRed
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .height(40.dp)
                                .width(40.dp)
                                .background(
                                    color = if (isAdminUnlocked) EsportsBooyahGreen.copy(alpha = 0.2f) else EsportsLiveRed.copy(alpha = 0.2f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isAdminUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                                contentDescription = "Security",
                                tint = if (isAdminUnlocked) EsportsBooyahGreen else EsportsLiveRed
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = if (isAdminUnlocked) "ADMIN MODE UNLOCKED" else "ADMIN MODE LOCKED",
                                color = if (isAdminUnlocked) EsportsBooyahGreen else EsportsLiveRed,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                            Text(
                                text = if (isAdminUnlocked) "You have full edit & score management access." else "Enter Admin PIN to enable management mode.",
                                color = EsportsTextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    if (isAdminUnlocked) {
                        Button(
                            onClick = onLockAdmin,
                            colors = ButtonDefaults.buttonColors(containerColor = EsportsLiveRed, contentColor = Color.White),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("lock_admin_btn")
                        ) {
                            Text("Lock Admin", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (!isAdminUnlocked) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = pinInput,
                            onValueChange = {
                                pinInput = it
                                pinError = null
                            },
                            label = { Text("Enter Admin PIN (Default: 739284)") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EsportsCyan,
                                unfocusedBorderColor = EsportsCardBorder,
                                focusedTextColor = EsportsTextPrimary,
                                unfocusedTextColor = EsportsTextPrimary
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("admin_center_pin_input")
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Button(
                            onClick = {
                                val success = onUnlockAdmin(pinInput)
                                if (!success) {
                                    pinError = "Incorrect PIN"
                                } else {
                                    pinInput = ""
                                    Toast.makeText(context, "Admin unlocked!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EsportsCyan, contentColor = Color(0xFF381E72)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("admin_center_unlock_btn")
                        ) {
                            Text("Unlock", fontWeight = FontWeight.Bold)
                        }
                    }

                    if (pinError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = pinError!!, color = EsportsLiveRed, fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Management Sections (Visible if unlocked)
        if (isAdminUnlocked) {
            // 1. Tournament Control Panel
            Card(
                colors = CardDefaults.cardColors(containerColor = EsportsSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, EsportsCardBorder),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.SportsEsports, contentDescription = "Tournament", tint = EsportsCyan)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Tournament Manager", color = EsportsTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                        Button(
                            onClick = { showCreateTournamentDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = EsportsCyan, contentColor = Color(0xFF381E72)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("create_new_tournament_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add", modifier = Modifier.height(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Tournament", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Switch Active Tournament:", color = EsportsTextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))

                    allTournaments.forEach { tourney ->
                        val isCurrent = tourney.id == activeTournament?.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = if (isCurrent) Color(0xFF1B2A38) else EsportsSurfaceVariant,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                                .padding(bottom = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = tourney.name,
                                    color = if (isCurrent) EsportsCyan else EsportsTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Game: ${tourney.gameTitle} | Points: 1st=${tourney.getPointsForPlacement(1)}pts, Kills=${tourney.killPointMultiplier}pt/kill",
                                    color = EsportsTextSecondary,
                                    fontSize = 11.sp
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (!isCurrent) {
                                    Button(
                                        onClick = { onSelectTournament(tourney.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = EsportsSurfaceVariant, contentColor = EsportsCyan),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text("Select", fontSize = 10.sp)
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                }

                                IconButton(
                                    onClick = { onRequestConfirmation(ConfirmationType.DeleteTournament(tourney)) },
                                    modifier = Modifier.testTag("delete_tournament_btn_${tourney.id}")
                                ) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = EsportsLiveRed)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Sample Test Data Generator / Reseeder
            Card(
                colors = CardDefaults.cardColors(containerColor = EsportsSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, EsportsCardBorder),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Seed", tint = EsportsGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Reset & Seed Sample Test Data", color = EsportsTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Wipes current database and creates standard sample esports tournament data with 16 Teams, 64 Players (4/team), 5 Matches (3 finished with scores, 1 live, 1 upcoming).",
                        color = EsportsTextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            onResetTestData()
                            Toast.makeText(context, "Database re-seeded with 16 teams and 5 matches!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = EsportsGold),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EsportsGold),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("reseed_test_data_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Load 16 Teams & 5 Sample Matches", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Security Credentials Settings
            Card(
                colors = CardDefaults.cardColors(containerColor = EsportsSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, EsportsCardBorder),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Key, contentDescription = "Security", tint = EsportsCyan)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Security & Admin PIN", color = EsportsTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Change the Admin PIN used to unlock data editing permissions.",
                        color = EsportsTextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { showChangePinDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = EsportsCyan),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EsportsCyan),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("change_pin_btn")
                    ) {
                        Text("Change Admin PIN", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Dialogs
    if (showCreateTournamentDialog) {
        CreateTournamentDialog(
            onCreate = { name, game, desc, killMult, csv ->
                onCreateTournament(name, game, desc, killMult, csv)
            },
            onDismiss = { showCreateTournamentDialog = false }
        )
    }

    if (showChangePinDialog) {
        ChangePinDialog(
            onChangePin = { newPin ->
                onChangePin(newPin)
                Toast.makeText(context, "Admin PIN updated successfully!", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showChangePinDialog = false }
        )
    }
}
