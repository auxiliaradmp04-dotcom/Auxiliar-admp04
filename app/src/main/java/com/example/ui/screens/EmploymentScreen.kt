package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.EmploymentProcessEntity
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.MasterOperationsViewModel

@Composable
fun EmploymentScreen(
    viewModel: MasterOperationsViewModel,
    modifier: Modifier = Modifier
) {
    val processes by viewModel.employmentProcesses.collectAsState()
    var showOnlyActive by remember { mutableStateOf(true) }
    var selectedProcessForUpdate by remember { mutableStateOf<EmploymentProcessEntity?>(null) }

    val filteredList = remember(processes, showOnlyActive) {
        if (showOnlyActive) processes.filter { it.stage != "CERRADA" }
        else processes
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Convocatorias y Procesos (${filteredList.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = showOnlyActive,
                    onCheckedChange = { showOnlyActive = it }
                )
                Text("Solo Activos", fontSize = 12.sp)
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(filteredList, key = { it.id }) { process ->
                EmploymentProcessCard(
                    process = process,
                    onUpdateClick = { selectedProcessForUpdate = process }
                )
            }
        }
    }

    selectedProcessForUpdate?.let { proc ->
        UpdateEmploymentStageDialog(
            process = proc,
            onDismiss = { selectedProcessForUpdate = null },
            onConfirm = { newStage, notes ->
                viewModel.updateEmploymentStage(proc, newStage, notes)
                selectedProcessForUpdate = null
            }
        )
    }
}

@Composable
fun EmploymentProcessCard(
    process: EmploymentProcessEntity,
    onUpdateClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("employment_${process.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = process.id,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )
                    StatusBadge(process.priority)
                }
                StatusBadge(process.stage)
            }

            Text(
                text = "${process.position} · ${process.organization}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (process.submissionDate.isNotBlank()) {
                Text(
                    text = "Fecha de Postulación: ${process.submissionDate}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (process.deadline.isNotBlank()) {
                Text(
                    text = "Límite: ${process.deadline}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (process.supportDocs.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.AttachFile, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(14.dp))
                    Text(
                        text = process.supportDocs,
                        fontSize = 11.sp,
                        color = CyanAccent
                    )
                }
            }

            if (process.nextAction.isNotBlank()) {
                Text(
                    text = "Siguiente Acción: ${process.nextAction} (${process.nextActionDate})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(
                    onClick = onUpdateClick,
                    colors = ButtonDefaults.buttonColors(containerColor = NavyDark)
                ) {
                    Text("Actualizar Estado", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun UpdateEmploymentStageDialog(
    process: EmploymentProcessEntity,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var stage by remember { mutableStateOf(process.stage) }
    var notes by remember { mutableStateOf(process.notes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Actualizar Etapa de Proceso", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("${process.id} · ${process.organization}", fontSize = 12.sp, color = GoldAccent)
                OutlinedTextField(
                    value = stage,
                    onValueChange = { stage = it },
                    label = { Text("Etapa (IDENTIFICADA, POSTULADA, ENTREVISTA, CERRADA)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Observaciones / Notas") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(stage, notes) },
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
            ) {
                Text("Actualizar", color = NavyDark, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
