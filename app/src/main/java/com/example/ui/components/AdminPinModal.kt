package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EsportsCyan
import com.example.ui.theme.EsportsLiveRed
import com.example.ui.theme.EsportsSurface
import com.example.ui.theme.EsportsTextPrimary
import com.example.ui.theme.EsportsTextSecondary

@Composable
fun AdminPinModal(
    onUnlock: (String) -> Boolean,
    onDismiss: () -> Unit
) {
    var pinText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var pinVisible by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = EsportsSurface,
        shape = RoundedCornerShape(16.dp),
        icon = {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = "Admin Shield",
                tint = EsportsCyan
            )
        },
        title = {
            Text(
                text = "Admin Authentication",
                color = EsportsTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
            Column {
                Text(
                    text = "Enter Admin PIN to manage tournaments, teams, matches, and scores. (Default PIN: 739284)",
                    color = EsportsTextSecondary,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = pinText,
                    onValueChange = {
                        pinText = it
                        errorMessage = null
                    },
                    label = { Text("Admin PIN") },
                    placeholder = { Text("e.g. 739284") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = if (pinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { pinVisible = !pinVisible }) {
                            Icon(
                                imageVector = if (pinVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle PIN Visibility",
                                tint = EsportsTextSecondary
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EsportsCyan,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedLabelColor = EsportsCyan,
                        focusedTextColor = EsportsTextPrimary,
                        unfocusedTextColor = EsportsTextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_pin_input")
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage!!,
                        color = EsportsLiveRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val success = onUnlock(pinText)
                    if (!success) {
                        errorMessage = "Incorrect Admin PIN. Please try again."
                    } else {
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = EsportsCyan,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("admin_unlock_submit")
            ) {
                Text("Unlock Admin", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Cancel", color = EsportsTextSecondary)
            }
        }
    )
}
