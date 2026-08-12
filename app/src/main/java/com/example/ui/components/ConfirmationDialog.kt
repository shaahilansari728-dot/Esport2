package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ConfirmationType
import com.example.ui.theme.EsportsBackground
import com.example.ui.theme.EsportsLiveRed
import com.example.ui.theme.EsportsSurface
import com.example.ui.theme.EsportsTextPrimary
import com.example.ui.theme.EsportsTextSecondary

@Composable
fun ConfirmationDialog(
    confirmationType: ConfirmationType,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val (title, message, confirmText, isDanger) = when (confirmationType) {
        is ConfirmationType.DeleteTeam -> Quadruple(
            "Delete Team?",
            "Are you sure you want to delete team '${confirmationType.team.name}' (${confirmationType.team.tag})? This will remove all associated players and match scores permanently.",
            "Delete Team",
            true
        )
        is ConfirmationType.DeleteMatch -> Quadruple(
            "Delete Match?",
            "Are you sure you want to delete '${confirmationType.match.matchName}'? All score records for this match will be lost.",
            "Delete Match",
            true
        )
        is ConfirmationType.ResetMatchScore -> Quadruple(
            "Reset Match Score?",
            "Are you sure you want to reset all team placements and kills for '${confirmationType.match.matchName}'? Scores will be cleared.",
            "Reset Score",
            true
        )
        is ConfirmationType.EndMatch -> Quadruple(
            "End Match?",
            "Are you sure you want to mark '${confirmationType.match.matchName}' as COMPLETED? Official standings will be calculated.",
            "End Match",
            false
        )
        is ConfirmationType.DeleteTournament -> Quadruple(
            "Delete Tournament?",
            "Are you sure you want to delete tournament '${confirmationType.tournament.name}'? ALL teams, matches, and standings in this tournament will be deleted permanently.",
            "Delete Tournament",
            true
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = EsportsSurface,
        shape = RoundedCornerShape(16.dp),
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Warning",
                tint = if (isDanger) EsportsLiveRed else Color(0xFFFFB800)
            )
        },
        title = {
            Text(
                text = title,
                color = EsportsTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
            Text(
                text = message,
                color = EsportsTextSecondary,
                fontSize = 14.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDanger) EsportsLiveRed else Color(0xFF00E5FF),
                    contentColor = if (isDanger) Color.White else Color.Black
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("confirm_dialog_btn")
            ) {
                Text(text = confirmText, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("dismiss_dialog_btn")
            ) {
                Text(text = "Cancel", color = EsportsTextSecondary)
            }
        }
    )
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
