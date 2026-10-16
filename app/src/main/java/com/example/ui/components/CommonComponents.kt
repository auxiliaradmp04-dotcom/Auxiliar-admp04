package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color = GoldAccent,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .testTag("metric_card_${title.lowercase().replace(" ", "_")}")
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(accentColor.copy(alpha = 0.15f), shape = RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (status.uppercase()) {
        "EN_TERMINO", "ACTIVO", "VERIFICADA", "CONFIRMADO", "PAGADA", "COMPLETADO", "COMPLETADA" ->
            Triple(SuccessGreenLight, Color(0xFF065F46), status)
        "VENCE_HOY", "HOY", "CARACTERIZADO", "EN_PREPARACION", "ALTA" ->
            Triple(WarningAmberLight, Color(0xFF92400E), status)
        "VENCIDO", "VENCIDA", "CRITICA", "EXPIRADO", "BLOQUEADO" ->
            Triple(DangerRedLight, Color(0xFF991B1B), status)
        "POSTULADA", "RESPONDIDO", "PROGRAMADA", "COMERCIAL" ->
            Triple(InfoBlueLight, Color(0xFF1E40AF), status)
        "PARCIAL", "PENDIENTE", "EN_PROCESO", "NUEVO" ->
            Triple(Color(0xFFEDE9FE), Color(0xFF5B21B6), status)
        else ->
            Triple(Color(0xFFF1F5F9), Color(0xFF475569), status)
    }

    Box(
        modifier = modifier
            .background(bgColor, shape = RoundedCornerShape(8.dp))
            .border(0.8.dp, textColor.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label.replace("_", " "),
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SystemQaBadge(
    score: Int = 100,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                Brush.horizontalGradient(listOf(Color(0xFF064E3B), Color(0xFF065F46))),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "QA Status",
            tint = SuccessGreenLight,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = "QA $score/100 APROBADO",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Gatekeeper dialog enforcing the SM6 / Master Operations safety confirmation:
 * User must explicitly enter "CONFIRMAR" to execute the transactional commit.
 */
@Composable
fun Sm6ConfirmCommitDialog(
    actionTitle: String,
    operationDescription: String,
    affectedEntity: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    var confirmationText by remember { mutableStateOf("") }
    val isConfirmed = confirmationText.trim().equals("CONFIRMAR", ignoreCase = false)

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(Icons.Default.Warning, contentDescription = null, tint = GoldAccent)
        },
        title = {
            Text(text = "Puerta SM6: $actionTitle", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = operationDescription,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Afectación: $affectedEntity",
                    style = MaterialTheme.typography.bodySmall,
                    color = GoldDark,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Para validar la escritura en el Sistema Maestro, escriba exactamente 'CONFIRMAR':",
                    style = MaterialTheme.typography.bodySmall
                )
                OutlinedTextField(
                    value = confirmationText,
                    onValueChange = { confirmationText = it },
                    placeholder = { Text("CONFIRMAR") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("sm6_confirm_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = isConfirmed,
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                modifier = Modifier.testTag("sm6_confirm_button")
            ) {
                Text("Confirmar Commit", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
