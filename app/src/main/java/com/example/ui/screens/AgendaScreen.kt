package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ActivityEntity
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.MasterOperationsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaScreen(
    viewModel: MasterOperationsViewModel,
    modifier: Modifier = Modifier
) {
    val activities by viewModel.activities.collectAsState()
    var selectedBlockType by remember { mutableStateOf("TODOS") }
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredActivities = remember(activities, selectedBlockType) {
        if (selectedBlockType == "TODOS") activities
        else activities.filter { it.blockType == selectedBlockType }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = GoldAccent,
                contentColor = NavyDark,
                modifier = Modifier.testTag("fab_add_activity")
            ) {
                Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AddAlarm, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Nueva Actividad", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // AUT-007 Calendar Sync Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CyanGlow)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Sync, contentDescription = null, tint = CyanAccent)
                        Column {
                            Text("AUT-007 Sincronización Google Calendar", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0C4A6E))
                            Text("Calendario: funreb@gmail.com (Trigger 15 min)", fontSize = 11.sp, color = Color(0xFF0369A1))
                        }
                    }
                    StatusBadge("ACTIVO")
                }
            }

            // Block type filter chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val blockTypes = listOf("TODOS", "FOCO", "SEGUIMIENTO", "ADMINISTRATIVO", "PERSONAL", "REUNION", "DOCENCIA")
                items(blockTypes) { b ->
                    FilterChip(
                        selected = selectedBlockType == b,
                        onClick = { selectedBlockType = b },
                        label = { Text(b, fontSize = 12.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Activities list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(filteredActivities, key = { it.id }) { act ->
                    ActivityItemCard(
                        activity = act,
                        onToggleDone = { viewModel.toggleActivityDone(act) }
                    )
                }
            }
        }

        if (showAddDialog) {
            AddActivityDialog(
                viewModel = viewModel,
                onDismiss = { showAddDialog = false }
            )
        }
    }
}

@Composable
fun ActivityItemCard(
    activity: ActivityEntity,
    onToggleDone: () -> Unit
) {
    val isDone = activity.termState == "COMPLETADA"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("activity_${activity.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDone) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isDone,
                onCheckedChange = { onToggleDone() },
                modifier = Modifier.testTag("check_done_${activity.id}")
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = activity.id,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent
                        )
                        StatusBadge(activity.blockType)
                        StatusBadge(activity.priority)
                    }
                    StatusBadge(activity.termState)
                }

                Text(
                    text = activity.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isDone) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Fecha: ${activity.date} ${activity.startTime} - ${activity.endTime}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (activity.syncCalendar) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            Icon(Icons.Default.CloudQueue, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(12.dp))
                            Text("Calendar", fontSize = 10.sp, color = CyanAccent)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddActivityDialog(
    viewModel: MasterOperationsViewModel,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var module by remember { mutableStateOf("OPERACION") }
    var blockType by remember { mutableStateOf("FOCO") }
    var date by remember { mutableStateOf("2026-09-02") }
    var startTime by remember { mutableStateOf("09:00") }
    var endTime by remember { mutableStateOf("10:00") }
    var priority by remember { mutableStateOf("ALTA") }
    var syncCalendar by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agendar Nueva Actividad (ACT)", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Título de la tarea") },
                        modifier = Modifier.fillMaxWidth().testTag("input_act_title")
                    )
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = module,
                            onValueChange = { module = it },
                            label = { Text("Módulo (CRM, JURIDICO, OPERACION)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = blockType,
                            onValueChange = { blockType = it },
                            label = { Text("Tipo Bloque (FOCO, SEGUIMIENTO)") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("Fecha (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = startTime,
                            onValueChange = { startTime = it },
                            label = { Text("Hora Inicio") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = endTime,
                            onValueChange = { endTime = it },
                            label = { Text("Hora Fin") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = syncCalendar, onCheckedChange = { syncCalendar = it })
                        Text("Sincronizar con Google Calendar (AUT-007)", fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.addActivity(
                        title = title,
                        module = module,
                        blockType = blockType,
                        date = date,
                        startTime = startTime,
                        endTime = endTime,
                        priority = priority,
                        syncCalendar = syncCalendar,
                        onSuccess = onDismiss
                    )
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
            ) {
                Text("Agendar ACT", color = NavyDark, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
