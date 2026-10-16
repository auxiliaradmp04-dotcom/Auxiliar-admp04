package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.CatalogItemEntity
import com.example.data.local.entity.PaymentObligationEntity
import com.example.data.local.entity.ServiceEntity
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.MasterOperationsViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Vista360Screen(
    viewModel: MasterOperationsViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.selectedClient360.collectAsState()
    val catalogItems by viewModel.catalogItems.collectAsState()
    var searchInput by remember { mutableStateOf("") }
    val context = LocalContext.current

    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply {
        maximumFractionDigits = 0
    }

    // Modal dialog states
    var showAddServiceDialog by remember { mutableStateOf(false) }
    var showAddCaseDialog by remember { mutableStateOf(false) }
    var showPaymentDialog by remember { mutableStateOf(false) }
    var showAddActivityDialog by remember { mutableStateOf(false) }
    var showAddDocumentDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Universal Search Bar
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Buscador Universal de Expediente (AUT-013)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = searchInput,
                        onValueChange = { searchInput = it },
                        placeholder = { Text("CLI, Cédula, Nombre o Teléfono...") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        modifier = Modifier.weight(1f).testTag("universal_search_input")
                    )

                    Button(
                        onClick = { viewModel.loadClient360ById(searchInput) },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                        modifier = Modifier.testTag("universal_search_button")
                    ) {
                        Text("Buscar", color = NavyDark, fontWeight = FontWeight.Bold)
                    }
                }

                // Quick presets
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val presets = listOf(
                        "Jhon Henry (CLI-000109)" to "CLI-000109",
                        "Marlyn Vanin (CLI-000121)" to "CLI-000121",
                        "Diana Lorena (CLI-000110)" to "CLI-000110",
                        "Yiver Mosquera (CLI-000103)" to "CLI-000103",
                        "Jhon Fredy (CLI-000001)" to "CLI-000001"
                    )
                    items(presets) { (label, id) ->
                        SuggestionChip(
                            onClick = {
                                searchInput = id
                                viewModel.loadClient360ById(id)
                            },
                            label = { Text(label, fontSize = 11.sp) }
                        )
                    }
                }
            }
        }

        // Empty state vs Loaded client
        if (state.client == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(
                        imageVector = Icons.Default.FolderShared,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(72.dp)
                    )
                    Text(
                        text = "Seleccione un cliente o busque por cédula/ID para abrir su Ficha 360",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            val cli = state.client!!
            val per = state.person

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(top = 4.dp, bottom = 32.dp)
            ) {
                // Client Master Header
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("client_360_header"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = NavyDark)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = cli.id,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldAccent
                                    )
                                    StatusBadge(cli.segment)
                                }
                                StatusBadge(cli.state)
                            }

                            Text(
                                text = cli.fullName,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Text(
                                text = "Documento: CC ${cli.documentNumber} (${per?.identityState ?: "VERIFICADA"})",
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )

                            Text(
                                text = "Teléfono: ${cli.phone} | Correo: ${cli.email}",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.75f)
                            )

                            if (cli.notes.isNotBlank()) {
                                Text(
                                    text = "Notas / Siguiente paso: ${cli.notes}",
                                    fontSize = 11.sp,
                                    color = GoldLight
                                )
                            }

                            // Drive URL affordance
                            if (per?.notes?.contains("http") == true || cli.notes.contains("http")) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(Icons.Default.CloudQueue, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                                        Text("Expediente en Google Drive", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    TextButton(
                                        onClick = {
                                            val url = "https://drive.google.com/drive/folders/1Gqc1Z2hjtmTt29qc4ZU7jKZOz3HQM2_k"
                                            try {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                                context.startActivity(intent)
                                            } catch (_: Exception) {}
                                        },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text("Abrir Drive", color = GoldAccent, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                // Operational Quick Action Buttons
                item {
                    Text(
                        text = "Acciones Operativas Directas",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = NavyDark
                    )
                }

                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            Button(
                                onClick = { showAddServiceDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = NavyDark),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.AddBusiness, contentDescription = null, modifier = Modifier.size(16.dp), tint = GoldAccent)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Contratar Servicio", fontSize = 12.sp)
                            }
                        }
                        item {
                            Button(
                                onClick = { showAddCaseDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = NavyDark),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.Gavel, contentDescription = null, modifier = Modifier.size(16.dp), tint = GoldAccent)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Radicar Caso", fontSize = 12.sp)
                            }
                        }
                        item {
                            Button(
                                onClick = { showPaymentDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.Paid, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Registrar Abono", fontSize = 12.sp, color = Color.White)
                            }
                        }
                        item {
                            Button(
                                onClick = { showAddActivityDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = NavyCard),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp), tint = GoldAccent)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Agendar Tarea", fontSize = 12.sp)
                            }
                        }
                        item {
                            OutlinedButton(
                                onClick = { showAddDocumentDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.AttachFile, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Adjuntar Doc", fontSize = 12.sp)
                            }
                        }
                    }
                }

                // MANDATORY SECTION: Resumen Financiero por Servicio (Table format)
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Resumen Financiero por Servicio",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                        Text(
                            text = "Servicio | Valor contratado | Pagado confirmado | Saldo pendiente | Estado",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("financial_summary_table"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Table Header
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Servicio", fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(2f))
                                Text("Contratado", fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1.5f), textAlign = TextAlign.End)
                                Text("Pagado", fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1.5f), textAlign = TextAlign.End)
                                Text("Saldo", fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1.5f), textAlign = TextAlign.End)
                                Text("Estado", fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1.5f), textAlign = TextAlign.End)
                            }

                            if (state.services.isEmpty()) {
                                Text(
                                    text = "El cliente no tiene servicios contratados aún. Use 'Contratar Servicio' para iniciar.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(8.dp)
                                )
                            } else {
                                state.services.forEach { serv ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(2f)) {
                                            Text(serv.id, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = GoldDark)
                                            Text(serv.serviceType.replace("_", " "), fontSize = 10.sp, maxLines = 1)
                                        }
                                        Text(
                                            currencyFormat.format(serv.agreedValue),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.weight(1.5f),
                                            textAlign = TextAlign.End
                                        )
                                        Text(
                                            currencyFormat.format(serv.paidValue),
                                            fontSize = 11.sp,
                                            color = SuccessGreen,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.weight(1.5f),
                                            textAlign = TextAlign.End
                                        )
                                        Text(
                                            currencyFormat.format(serv.balance),
                                            fontSize = 11.sp,
                                            color = if (serv.balance > 0) DangerRed else MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.weight(1.5f),
                                            textAlign = TextAlign.End
                                        )
                                        Box(modifier = Modifier.weight(1.5f), contentAlignment = Alignment.CenterEnd) {
                                            StatusBadge(serv.state)
                                        }
                                    }
                                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                                }

                                // Total Row
                                val totalContracted = state.services.sumOf { it.agreedValue }
                                val totalPaid = state.services.sumOf { it.paidValue }
                                val totalBalance = state.services.sumOf { it.balance }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(NavyDark.copy(alpha = 0.05f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("TOTALES", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NavyDark, modifier = Modifier.weight(2f))
                                    Text(currencyFormat.format(totalContracted), fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1.5f), textAlign = TextAlign.End)
                                    Text(currencyFormat.format(totalPaid), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SuccessGreen, modifier = Modifier.weight(1.5f), textAlign = TextAlign.End)
                                    Text(currencyFormat.format(totalBalance), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (totalBalance > 0) DangerRed else SuccessGreen, modifier = Modifier.weight(1.5f), textAlign = TextAlign.End)
                                    Text(if (totalBalance <= 0) "AL DÍA" else "CARTERA", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = if (totalBalance <= 0) SuccessGreen else DangerRed, modifier = Modifier.weight(1.5f), textAlign = TextAlign.End)
                                }
                            }
                        }
                    }
                }

                // Section: Casos Jurídicos Vinculados
                item {
                    Text(
                        text = "Casos y Trámites Jurídicos (${state.legalCases.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = NavyDark
                    )
                }

                if (state.legalCases.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "No registra casos jurídicos vinculados. Puede radicar uno con el botón 'Radicar Caso'.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                } else {
                    items(state.legalCases, key = { it.id }) { caseEntity ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "${caseEntity.id} · vs ${caseEntity.targetEntity}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    StatusBadge(caseEntity.termState)
                                }
                                Text(text = "Asunto: ${caseEntity.matter.replace("_", " ")} | Radicado: ${caseEntity.radicado}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = "Vence: ${caseEntity.dueDate} (${caseEntity.termType})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = if (caseEntity.termState == "VENCIDO") DangerRed else MaterialTheme.colorScheme.onSurface)
                                if (caseEntity.nextAction.isNotBlank()) {
                                    Text(text = "Siguiente acción: ${caseEntity.nextAction} (${caseEntity.nextActionDate})", fontSize = 11.sp, color = CyanAccent)
                                }
                            }
                        }
                    }
                }

                // Section: Documentos y Archivos Vinculados
                item {
                    Text(
                        text = "Documentos y Trazabilidad (${state.documents.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = NavyDark
                    )
                }

                if (state.documents.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "No registra documentos anexos directos.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                } else {
                    items(state.documents, key = { it.id }) { doc ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = "${doc.id} · ${doc.title}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text(text = "Tipo: ${doc.docType} | Versión: ${doc.version} | Fecha: ${doc.date}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                if (doc.driveUrl.isNotBlank()) {
                                    IconButton(onClick = {
                                        try {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(doc.driveUrl))
                                            context.startActivity(intent)
                                        } catch (_: Exception) {}
                                    }) {
                                        Icon(Icons.Default.OpenInNew, contentDescription = "Abrir documento", tint = GoldAccent)
                                    }
                                }
                            }
                        }
                    }
                }

                // Section: Actividades en Agenda
                item {
                    Text(
                        text = "Tareas y Agenda (${state.activities.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = NavyDark
                    )
                }

                if (state.activities.isEmpty()) {
                    item {
                        Text("No hay actividades pendientes en agenda.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    items(state.activities, key = { it.id }) { act ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = "${act.id} · ${act.title}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(text = "Fecha: ${act.date} (${act.blockType})", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                StatusBadge(act.termState)
                            }
                        }
                    }
                }
            }
        }
    }

    // === MODAL DIALOGS ===

    // 1. Contratar Servicio Dialog
    if (showAddServiceDialog && state.client != null) {
        val cli = state.client!!
        var selectedItem by remember { mutableStateOf<CatalogItemEntity?>(catalogItems.firstOrNull()) }
        var agreedPriceText by remember { mutableStateOf(selectedItem?.referencePrice?.toInt()?.toString() ?: "350000") }
        var paymentMethod by remember { mutableStateOf("TRANSFERENCIA") }
        var catalogSearch by remember { mutableStateOf("") }

        val filteredItems = remember(catalogItems, catalogSearch) {
            if (catalogSearch.isBlank()) catalogItems
            else catalogItems.filter { it.name.contains(catalogSearch, ignoreCase = true) || it.line.contains(catalogSearch, ignoreCase = true) }
        }

        AlertDialog(
            onDismissRequest = { showAddServiceDialog = false },
            title = { Text("Contratar Servicio para ${cli.id}", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = catalogSearch,
                        onValueChange = { catalogSearch = it },
                        placeholder = { Text("Buscar en 8 líneas del catálogo...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Text("Seleccione servicio del catálogo:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    LazyColumn(modifier = Modifier.height(150.dp)) {
                        items(filteredItems) { item ->
                            val isSelected = selectedItem?.id == item.id
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedItem = item
                                        agreedPriceText = item.referencePrice.toInt().toString()
                                    }
                                    .background(if (isSelected) GoldAccent.copy(alpha = 0.2f) else Color.Transparent)
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.name, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text(item.line, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = GoldDark, modifier = Modifier.size(16.dp))
                                }
                            }
                            HorizontalDivider()
                        }
                    }

                    OutlinedTextField(
                        value = agreedPriceText,
                        onValueChange = { agreedPriceText = it },
                        label = { Text("Valor acordado (COP)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("TRANSFERENCIA", "EFECTIVO", "BANCOLOMBIA", "DAVIPLATA").forEach { method ->
                            FilterChip(
                                selected = paymentMethod == method,
                                onClick = { paymentMethod = method },
                                label = { Text(method, fontSize = 10.sp) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val price = agreedPriceText.toDoubleOrNull() ?: 0.0
                        val item = selectedItem ?: return@Button
                        viewModel.addServiceForClient(
                            clientId = cli.id,
                            catalogItemId = item.id,
                            catalogLine = item.line,
                            serviceType = item.name,
                            agreedValue = price,
                            paymentMethod = paymentMethod,
                            onSuccess = { showAddServiceDialog = false }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyDark)
                ) {
                    Text("Contratar y Generar Obligación")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddServiceDialog = false }) { Text("Cancelar") }
            }
        )
    }

    // 2. Radicar Caso Dialog
    if (showAddCaseDialog && state.client != null) {
        val cli = state.client!!
        var matter by remember { mutableStateOf("DERECHO_PETICION") }
        var targetEntity by remember { mutableStateOf("") }
        var radicado by remember { mutableStateOf("") }
        var termDaysText by remember { mutableStateOf("15") }
        var risk by remember { mutableStateOf("MEDIO") }
        var nextAction by remember { mutableStateOf("Esperar respuesta dentro de términos legales") }
        val services = state.services
        var selectedServiceId by remember { mutableStateOf(services.firstOrNull()?.id ?: "SIN_SERVICIO") }

        AlertDialog(
            onDismissRequest = { showAddCaseDialog = false },
            title = { Text("Radicar Caso Jurídico para ${cli.id}", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Asociar a Servicio contratado:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        item {
                            FilterChip(
                                selected = selectedServiceId == "SIN_SERVICIO",
                                onClick = { selectedServiceId = "SIN_SERVICIO" },
                                label = { Text("Sin servicio", fontSize = 11.sp) }
                            )
                        }
                        items(services) { s ->
                            FilterChip(
                                selected = selectedServiceId == s.id,
                                onClick = { selectedServiceId = s.id },
                                label = { Text(s.id, fontSize = 11.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = targetEntity,
                        onValueChange = { targetEntity = it },
                        label = { Text("Entidad Contraparte (Banco, Claro, FGA...)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = radicado,
                        onValueChange = { radicado = it },
                        label = { Text("Radicado / Token") },
                        placeholder = { Text("Ej. 2026-DP-09823") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("DERECHO_PETICION", "HABEAS_DATA", "TUTELA", "TRANSITO").forEach { m ->
                            FilterChip(
                                selected = matter == m,
                                onClick = { matter = m },
                                label = { Text(m.replace("_", " "), fontSize = 10.sp) }
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
                        val days = termDaysText.toIntOrNull() ?: 15
                        viewModel.createLegalCaseStandalone(
                            clientId = cli.id,
                            serviceId = selectedServiceId,
                            matter = matter,
                            targetEntity = targetEntity.ifBlank { "Entidad No Especificada" },
                            radicado = radicado.ifBlank { "BORRADOR-${System.currentTimeMillis() % 10000}" },
                            termType = "HABILES_$days",
                            termDays = days,
                            dueDate = "",
                            risk = risk,
                            nextAction = nextAction,
                            notes = "Radicado desde Expediente 360",
                            onSuccess = { showAddCaseDialog = false }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyDark)
                ) {
                    Text("Radicar Caso")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCaseDialog = false }) { Text("Cancelar") }
            }
        )
    }

    // 3. Registrar Abono a Obligación Dialog
    if (showPaymentDialog && state.client != null) {
        val obligations = state.obligations.filter { it.balance > 0 }
        var selectedObligation by remember { mutableStateOf(obligations.firstOrNull()) }
        var amountText by remember { mutableStateOf(selectedObligation?.balance?.toInt()?.toString() ?: "150000") }
        var paymentMethod by remember { mutableStateOf("TRANSFERENCIA") }
        var receiptRef by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showPaymentDialog = false },
            title = { Text("Registrar Abono / Pago", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (obligations.isEmpty()) {
                        Text("El cliente no registra obligaciones pendientes de pago. ¡Está al día!", color = SuccessGreen, fontWeight = FontWeight.Bold)
                    } else {
                        Text("Seleccione la obligación a abonar:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        obligations.forEach { obl ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedObligation = obl
                                        amountText = obl.balance.toInt().toString()
                                    }
                                    .background(if (selectedObligation?.id == obl.id) GoldAccent.copy(alpha = 0.2f) else Color.Transparent)
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(obl.id, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text(obl.concept, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text("Saldo: ${currencyFormat.format(obl.balance)}", fontWeight = FontWeight.Bold, color = DangerRed, fontSize = 12.sp)
                            }
                            HorizontalDivider()
                        }

                        OutlinedTextField(
                            value = amountText,
                            onValueChange = { amountText = it },
                            label = { Text("Monto a abonar (COP)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = receiptRef,
                            onValueChange = { receiptRef = it },
                            label = { Text("Referencia o Nro. Comprobante") },
                            placeholder = { Text("Ej. TRANSF-908231") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("TRANSFERENCIA", "EFECTIVO", "BANCOLOMBIA", "DAVIPLATA").forEach { m ->
                                FilterChip(
                                    selected = paymentMethod == m,
                                    onClick = { paymentMethod = m },
                                    label = { Text(m, fontSize = 10.sp) }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                if (obligations.isNotEmpty()) {
                    Button(
                        onClick = {
                            val obl = selectedObligation ?: return@Button
                            val amt = amountText.toDoubleOrNull() ?: 0.0
                            viewModel.registerPaymentForObligation(
                                obligationId = obl.id,
                                amount = amt,
                                paymentMethod = paymentMethod,
                                receiptRef = receiptRef,
                                onSuccess = { showPaymentDialog = false }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                    ) {
                        Text("Confirmar Abono", color = Color.White)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showPaymentDialog = false }) { Text("Cerrar") }
            }
        )
    }

    // 4. Agendar Tarea Dialog
    if (showAddActivityDialog && state.client != null) {
        val cli = state.client!!
        var title by remember { mutableStateOf("") }
        var blockType by remember { mutableStateOf("SEGUIMIENTO") }
        var dateText by remember { mutableStateOf("2026-10-20") }
        var syncCalendar by remember { mutableStateOf(true) }

        AlertDialog(
            onDismissRequest = { showAddActivityDialog = false },
            title = { Text("Agendar Tarea para ${cli.id}", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Título de la tarea") },
                        placeholder = { Text("Ej. Llamada de seguimiento perfil crediticio") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = dateText,
                        onValueChange = { dateText = it },
                        label = { Text("Fecha (AAAA-MM-DD)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("SEGUIMIENTO", "ADMINISTRATIVO", "REUNION", "FOCO").forEach { b ->
                            FilterChip(
                                selected = blockType == b,
                                onClick = { blockType = b },
                                label = { Text(b, fontSize = 10.sp) }
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = syncCalendar, onCheckedChange = { syncCalendar = it })
                        Text("Sincronizar con Google Calendar (AUT-007)", fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            viewModel.addActivityForClient(
                                clientId = cli.id,
                                title = title,
                                module = "CRM",
                                blockType = blockType,
                                date = dateText,
                                startTime = "09:00",
                                endTime = "10:00",
                                priority = "ALTA",
                                syncCalendar = syncCalendar,
                                onSuccess = { showAddActivityDialog = false }
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyDark)
                ) {
                    Text("Agendar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddActivityDialog = false }) { Text("Cancelar") }
            }
        )
    }

    // 5. Adjuntar Documento Dialog
    if (showAddDocumentDialog && state.client != null) {
        val cli = state.client!!
        var title by remember { mutableStateOf("") }
        var docType by remember { mutableStateOf("CONTRATO") }
        var driveUrl by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDocumentDialog = false },
            title = { Text("Adjuntar Documento", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Título del documento") },
                        placeholder = { Text("Ej. Poder suscrito autenticado") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = driveUrl,
                        onValueChange = { driveUrl = it },
                        label = { Text("Enlace de Google Drive (opcional)") },
                        placeholder = { Text("https://drive.google.com/...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("CONTRATO", "PETICION", "SOPORTE_PAGO", "FACTURA").forEach { dt ->
                            FilterChip(
                                selected = docType == dt,
                                onClick = { docType = dt },
                                label = { Text(dt, fontSize = 10.sp) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            viewModel.addDocumentForClient(
                                clientId = cli.id,
                                title = title,
                                docType = docType,
                                driveUrl = driveUrl,
                                onSuccess = { showAddDocumentDialog = false }
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyDark)
                ) {
                    Text("Vincular")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDocumentDialog = false }) { Text("Cancelar") }
            }
        )
    }
}
