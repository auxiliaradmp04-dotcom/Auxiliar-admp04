package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.data.local.entity.ClientEntity
import com.example.data.local.entity.LegalActionEntity
import com.example.data.local.entity.LegalCaseEntity
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.MasterOperationsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalScreen(
    viewModel: MasterOperationsViewModel,
    modifier: Modifier = Modifier
) {
    val cases by viewModel.legalCases.collectAsState()
    val clients by viewModel.clients.collectAsState()
    var filterTermState by remember { mutableStateOf("TODOS") }
    var selectedCaseForAction by remember { mutableStateOf<LegalCaseEntity?>(null) }
    var selectedCaseForDetails by remember { mutableStateOf<LegalCaseEntity?>(null) }
    var showCreateCaseDialog by remember { mutableStateOf(false) }

    val filteredCases = remember(cases, filterTermState) {
        if (filterTermState == "TODOS") cases
        else cases.filter { it.termState == filterTermState }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateCaseDialog = true },
                containerColor = GoldAccent,
                contentColor = NavyDark,
                modifier = Modifier.testTag("fab_radicar_caso")
            ) {
                Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Gavel, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Radicar Caso", fontWeight = FontWeight.Bold)
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
            // Filter chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf("TODOS", "EN_TERMINO", "VENCE_HOY", "VENCIDO", "RESPONDIDO")
                items(filters) { f ->
                    FilterChip(
                        selected = filterTermState == f,
                        onClick = { filterTermState = f },
                        label = { Text(f.replace("_", " "), fontSize = 12.sp) }
                    )
                }
            }

            Text(
                text = "Mostrando ${filteredCases.size} casos y trámites jurídicos",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // Cases list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 4.dp, bottom = 80.dp)
            ) {
                items(filteredCases, key = { it.id }) { legalCase ->
                    LegalCaseCard(
                        legalCase = legalCase,
                        onAddAction = { selectedCaseForAction = legalCase },
                        onViewDetails = { selectedCaseForDetails = legalCase },
                        onStatusChange = { newStatus, newTerm ->
                            viewModel.updateLegalCaseState(legalCase, newStatus, newTerm)
                        }
                    )
                }
            }
        }
    }

    if (showCreateCaseDialog) {
        CreateLegalCaseDialog(
            clients = clients,
            onDismiss = { showCreateCaseDialog = false },
            onConfirm = { clientId, matter, targetEntity, radicado, termDays, risk, nextAction ->
                viewModel.createLegalCaseStandalone(
                    clientId = clientId,
                    serviceId = "SIN_SERVICIO",
                    matter = matter,
                    targetEntity = targetEntity,
                    radicado = radicado,
                    termType = "HABILES_$termDays",
                    termDays = termDays,
                    dueDate = "",
                    risk = risk,
                    nextAction = nextAction,
                    notes = "Radicación jurídica desde Módulo Legal",
                    onSuccess = { showCreateCaseDialog = false }
                )
            }
        )
    }

    selectedCaseForAction?.let { c ->
        AddLegalActionDialog(
            caseEntity = c,
            onDismiss = { selectedCaseForAction = null },
            onConfirm = { type, desc, rad ->
                viewModel.addLegalAction(c.id, type, desc, rad)
                selectedCaseForAction = null
            }
        )
    }

    selectedCaseForDetails?.let { c ->
        CaseDetailsDialog(
            caseEntity = c,
            viewModel = viewModel,
            onDismiss = { selectedCaseForDetails = null }
        )
    }
}

@Composable
fun LegalCaseCard(
    legalCase: LegalCaseEntity,
    onAddAction: () -> Unit,
    onViewDetails: () -> Unit,
    onStatusChange: (String, String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("legal_case_${legalCase.id}"),
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
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = legalCase.id,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )
                    StatusBadge(legalCase.risk)
                }
                StatusBadge(legalCase.termState)
            }

            Text(
                text = "${legalCase.matter.replace("_", " ")} vs ${legalCase.targetEntity}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Cliente: ${legalCase.clientId} | Rad: ${legalCase.radicado}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Vence: ${legalCase.dueDate}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (legalCase.termState == "VENCIDO") DangerRed else MaterialTheme.colorScheme.onSurface
                )
            }

            if (legalCase.nextAction.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        Text(
                            text = "Siguiente Acción:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent
                        )
                        Text(
                            text = "${legalCase.nextAction} (${legalCase.nextActionDate})",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onViewDetails) {
                    Text("Ver Detalle", fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Button(
                    onClick = onAddAction,
                    colors = ButtonDefaults.buttonColors(containerColor = NavyDark)
                ) {
                    Icon(Icons.Default.AddCircleOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Actuación (JUR)", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun CreateLegalCaseDialog(
    clients: List<ClientEntity>,
    onDismiss: () -> Unit,
    onConfirm: (clientId: String, matter: String, targetEntity: String, radicado: String, termDays: Int, risk: String, nextAction: String) -> Unit
) {
    var clientSearch by remember { mutableStateOf("") }
    var selectedClient by remember { mutableStateOf(clients.firstOrNull()) }
    var matter by remember { mutableStateOf("DERECHO_PETICION") }
    var targetEntity by remember { mutableStateOf("") }
    var radicado by remember { mutableStateOf("") }
    var termDays by remember { mutableIntStateOf(15) }
    var risk by remember { mutableStateOf("MEDIO") }
    var nextAction by remember { mutableStateOf("Revisión de respuesta dentro de términos de ley") }

    val filteredClients = remember(clients, clientSearch) {
        if (clientSearch.isBlank()) clients.take(15)
        else clients.filter {
            it.fullName.contains(clientSearch, ignoreCase = true) ||
            it.documentNumber.contains(clientSearch) ||
            it.id.contains(clientSearch, ignoreCase = true)
        }.take(15)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Radicar Nuevo Caso Jurídico", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Client selector
                OutlinedTextField(
                    value = clientSearch,
                    onValueChange = { clientSearch = it },
                    placeholder = { Text("Buscar cliente (nombre o CC)...") },
                    leadingIcon = { Icon(Icons.Default.PersonSearch, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text(
                    text = "Cliente seleccionado: ${selectedClient?.fullName ?: "Ninguno"} (${selectedClient?.id ?: ""})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldDark
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(filteredClients) { c ->
                        FilterChip(
                            selected = selectedClient?.id == c.id,
                            onClick = { selectedClient = c },
                            label = { Text("${c.id} - ${c.fullName.split(" ").first()}", fontSize = 10.sp) }
                        )
                    }
                }

                // Matter
                Text("Asunto:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val matters = listOf("DERECHO_PETICION", "HABEAS_DATA", "TUTELA", "TRANSITO", "RECLAMACION_PQR")
                    items(matters) { m ->
                        FilterChip(
                            selected = matter == m,
                            onClick = { matter = m },
                            label = { Text(m.replace("_", " "), fontSize = 10.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = targetEntity,
                    onValueChange = { targetEntity = it },
                    label = { Text("Entidad Contraparte") },
                    placeholder = { Text("Bancolombia, Claro, Movistar, FGA, Datacrédito...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = radicado,
                    onValueChange = { radicado = it },
                    label = { Text("Radicado / Nro. Radicación") },
                    placeholder = { Text("Ej. 2026-DP-11029") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Term
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(15 to "15 Días Hábiles", 10 to "10 Días (Tutela)", 30 to "30 Días (Fondo)").forEach { (days, label) ->
                        FilterChip(
                            selected = termDays == days,
                            onClick = { termDays = days },
                            label = { Text(label, fontSize = 10.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = nextAction,
                    onValueChange = { nextAction = it },
                    label = { Text("Siguiente Acción Operativa") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val cli = selectedClient ?: return@Button
                    onConfirm(
                        cli.id,
                        matter,
                        targetEntity.ifBlank { "Entidad No Especificada" },
                        radicado.ifBlank { "EXP-${System.currentTimeMillis() % 100000}" },
                        termDays,
                        risk,
                        nextAction
                    )
                },
                enabled = selectedClient != null,
                colors = ButtonDefaults.buttonColors(containerColor = NavyDark)
            ) {
                Text("Radicar Caso Formal")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
fun AddLegalActionDialog(
    caseEntity: LegalCaseEntity,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var actionType by remember { mutableStateOf("PETICION_INICIAL") }
    var description by remember { mutableStateOf("") }
    var radicado by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar Actuación Jurídica (JUR)", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Caso: ${caseEntity.id} - ${caseEntity.targetEntity}", fontSize = 12.sp, color = GoldAccent)
                OutlinedTextField(
                    value = actionType,
                    onValueChange = { actionType = it },
                    label = { Text("Tipo (PETICION_INICIAL, RECURSO, RESPUESTA)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción del hecho / actuación") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = radicado,
                    onValueChange = { radicado = it },
                    label = { Text("Radicado / Token") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(actionType, description, radicado) },
                enabled = description.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
            ) {
                Text("Registrar JUR", color = NavyDark, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
fun CaseDetailsDialog(
    caseEntity: LegalCaseEntity,
    viewModel: MasterOperationsViewModel,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("${caseEntity.id} · ${caseEntity.targetEntity}", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Materia: ${caseEntity.matter}", fontWeight = FontWeight.SemiBold)
                Text("Cliente: ${caseEntity.clientId}")
                Text("Radicado: ${caseEntity.radicado}")
                Text("Término: ${caseEntity.termType} (${caseEntity.termDays} días)")
                Text("Fecha Vencimiento: ${caseEntity.dueDate}")
                Text("Estado Término: ${caseEntity.termState}")
                Text("Riesgo: ${caseEntity.risk}")
                Text("Siguiente acción: ${caseEntity.nextAction}")
                Text("Observaciones: ${caseEntity.notes}")
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Cerrar") }
        }
    )
}
