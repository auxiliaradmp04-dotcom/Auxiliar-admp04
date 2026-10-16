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
import com.example.data.local.entity.CatalogItemEntity
import com.example.data.local.entity.ClientEntity
import com.example.data.local.entity.CommercialRequestEntity
import com.example.data.local.entity.ProspectEntity
import com.example.ui.components.Sm6ConfirmCommitDialog
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.MasterOperationsViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrmScreen(
    viewModel: MasterOperationsViewModel,
    onClientSelected: (ClientEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Clientes, 1: Prospectos, 2: Solicitudes/Cotizaciones, 3: Catálogo Comercial
    var showIngresoUnicoDialog by remember { mutableStateOf(false) }
    var showAddCatalogItemDialog by remember { mutableStateOf(false) }
    var showNewQuotationDialog by remember { mutableStateOf(false) }

    val clients by viewModel.clients.collectAsState()
    val prospects by viewModel.prospects.collectAsState()
    val requests by viewModel.commercialRequests.collectAsState()
    val catalogItems by viewModel.catalogItems.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            when (selectedTab) {
                0, 1 -> {
                    FloatingActionButton(
                        onClick = { showIngresoUnicoDialog = true },
                        containerColor = GoldAccent,
                        contentColor = NavyDark,
                        modifier = Modifier.testTag("fab_ingreso_unico")
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PersonAdd, contentDescription = "Ingreso Único")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Ingreso Único", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                2 -> {
                    FloatingActionButton(
                        onClick = { showNewQuotationDialog = true },
                        containerColor = GoldAccent,
                        contentColor = NavyDark,
                        modifier = Modifier.testTag("fab_nueva_cotizacion")
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PostAdd, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Nueva Cotización", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                3 -> {
                    FloatingActionButton(
                        onClick = { showAddCatalogItemDialog = true },
                        containerColor = GoldAccent,
                        contentColor = NavyDark,
                        modifier = Modifier.testTag("fab_nuevo_servicio_catalogo")
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AddBusiness, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Nuevo Servicio", fontWeight = FontWeight.Bold)
                        }
                    }
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
            // Tabs: Clientes, Prospectos, Cotizaciones, Catálogo
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                edgePadding = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Clientes (${clients.size})", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Prospectos (${prospects.size})", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Cotizaciones (${requests.size})", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("Catálogo (${catalogItems.size})", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) }
                )
            }

            when (selectedTab) {
                0 -> {
                    // Clients List with Real-time Search and Status Filter
                    var clientSearchQuery by remember { mutableStateOf("") }
                    var statusFilter by remember { mutableStateOf("TODOS") }

                    val filteredClients = remember(clients, clientSearchQuery, statusFilter) {
                        clients.filter { client ->
                            val matchesQuery = clientSearchQuery.isBlank() ||
                                client.fullName.contains(clientSearchQuery, ignoreCase = true) ||
                                client.documentNumber.contains(clientSearchQuery) ||
                                client.phone.contains(clientSearchQuery) ||
                                client.id.contains(clientSearchQuery, ignoreCase = true)
                            val matchesStatus = statusFilter == "TODOS" || client.state.equals(statusFilter, ignoreCase = true)
                            matchesQuery && matchesStatus
                        }
                    }

                    Column(modifier = Modifier.fillMaxSize()) {
                        OutlinedTextField(
                            value = clientSearchQuery,
                            onValueChange = { clientSearchQuery = it },
                            placeholder = { Text("Buscar entre 127 clientes (nombre, CC, CLI-...)") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            trailingIcon = {
                                if (clientSearchQuery.isNotBlank()) {
                                    IconButton(onClick = { clientSearchQuery = "" }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(bottom = 6.dp)
                        ) {
                            val filters = listOf("TODOS", "ACTIVO", "PENDIENTE", "EN_PROCESO", "ARCHIVADO")
                            items(filters) { f ->
                                FilterChip(
                                    selected = statusFilter == f,
                                    onClick = { statusFilter = f },
                                    label = { Text(f, fontSize = 11.sp) }
                                )
                            }
                        }

                        Text(
                            text = "Mostrando ${filteredClients.size} de ${clients.size} clientes cargados",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(top = 4.dp, bottom = 80.dp)
                        ) {
                            items(filteredClients, key = { it.id }) { client ->
                                ClientItemCard(client = client, onClick = { onClientSelected(client) })
                            }
                        }
                    }
                }
                1 -> {
                    // Prospects Pipeline
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(top = 4.dp, bottom = 80.dp)
                    ) {
                        items(prospects, key = { it.id }) { prospect ->
                            ProspectItemCard(
                                prospect = prospect,
                                onStageChange = { newStage -> viewModel.updateProspectStage(prospect, newStage) }
                            )
                        }
                    }
                }
                2 -> {
                    // Commercial Requests & Quotes Pipeline
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(top = 4.dp, bottom = 80.dp)
                    ) {
                        items(requests, key = { it.id }) { req ->
                            CommercialRequestCard(
                                request = req,
                                onContract = {
                                    viewModel.contractCommercialRequest(
                                        request = req,
                                        agreedAmount = req.quotedAmount,
                                        paymentMethod = "TRANSFERENCIA",
                                        createCase = true,
                                        targetEntity = "Entidad Reclamada",
                                        radicado = "BORRADOR_INICIAL",
                                        risk = "MEDIO",
                                        onSuccess = {}
                                    )
                                }
                            )
                        }
                    }
                }
                3 -> {
                    // Catalog Browser (Organized in 8 Lines)
                    CatalogBrowserContent(catalogItems = catalogItems)
                }
            }
        }

        if (showIngresoUnicoDialog) {
            IngresoUnicoDialog(
                viewModel = viewModel,
                catalogItems = catalogItems,
                onDismiss = { showIngresoUnicoDialog = false }
            )
        }

        if (showAddCatalogItemDialog) {
            AddCatalogItemDialog(
                viewModel = viewModel,
                onDismiss = { showAddCatalogItemDialog = false }
            )
        }

        if (showNewQuotationDialog) {
            NewQuotationDialog(
                viewModel = viewModel,
                catalogItems = catalogItems,
                onDismiss = { showNewQuotationDialog = false }
            )
        }
    }
}

@Composable
fun ClientItemCard(
    client: ClientEntity,
    onClick: () -> Unit
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply { maximumFractionDigits = 0 }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("client_item_${client.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = client.id,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )
                    StatusBadge(client.segment)
                }
                StatusBadge(client.state)
            }

            Text(
                text = client.fullName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "CC: ${client.documentNumber} | Tel: ${client.phone}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = currencyFormat.format(client.totalServicesValue),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )
            }

            if (client.notes.isNotBlank()) {
                Text(
                    text = client.notes,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun ProspectItemCard(
    prospect: ProspectEntity,
    onStageChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("prospect_item_${prospect.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${prospect.id} · ${prospect.serviceRequested.replace("_", " ")}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = CyanAccent
                )
                StatusBadge(prospect.stage)
            }

            Text(
                text = prospect.fullName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Tel: ${prospect.phone} | Correo: ${prospect.email}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (prospect.appointmentDate.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.Event, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(14.dp))
                    Text(
                        text = "Cita: ${prospect.appointmentDate}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = WarningAmber
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Probabilidad: ${prospect.probability}%",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Box {
                    OutlinedButton(
                        onClick = { expanded = true },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Cambiar Etapa", fontSize = 11.sp)
                    }

                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        listOf("NUEVO", "CONTACTADO", "CARACTERIZADO", "PAGO_VALIDADO", "CONVERTIDO", "PAUSADO").forEach { stage ->
                            DropdownMenuItem(
                                text = { Text(stage) },
                                onClick = {
                                    expanded = false
                                    onStageChange(stage)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CommercialRequestCard(
    request: CommercialRequestEntity,
    onContract: () -> Unit
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply { maximumFractionDigits = 0 }
    val isContracted = request.stage == "CONTRATADA"

    Card(
        modifier = Modifier.fillMaxWidth().testTag("commercial_req_${request.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(request.id, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = GoldAccent)
                    StatusBadge(request.catalogLine)
                }
                StatusBadge(request.stage)
            }

            Text(
                text = "${request.fullName} · CC: ${request.documentNumber}",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            Text(
                text = "Servicio: ${request.catalogItemName}",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = NavyDark
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cotizado: ${currencyFormat.format(request.quotedAmount)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = GoldDark
                )

                if (!isContracted) {
                    Button(
                        onClick = onContract,
                        colors = ButtonDefaults.buttonColors(containerColor = NavyDark),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Contratar Servicio", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (request.notes.isNotBlank()) {
                Text(request.notes, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun CatalogBrowserContent(catalogItems: List<CatalogItemEntity>) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedLine by remember { mutableStateOf("TODAS") }
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply { maximumFractionDigits = 0 }

    val filtered = remember(catalogItems, searchQuery, selectedLine) {
        catalogItems.filter { item ->
            (selectedLine == "TODAS" || item.line == selectedLine) &&
                    (searchQuery.isBlank() || item.name.contains(searchQuery, ignoreCase = true) || item.description.contains(searchQuery, ignoreCase = true))
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(top = 4.dp, bottom = 80.dp)
    ) {
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar servicio en el catálogo...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("search_catalog_input")
            )
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                val lines = listOf("TODAS") + catalogItems.map { it.line }.distinct()
                items(lines) { l ->
                    FilterChip(
                        selected = selectedLine == l,
                        onClick = { selectedLine = l },
                        label = { Text(l, fontSize = 11.sp) }
                    )
                }
            }
        }

        items(filtered, key = { it.id }) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(item.line, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldDark)
                        Text(currencyFormat.format(item.referencePrice), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                    }
                    Text(item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(item.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Responsable: ${item.responsible} | ${item.scope}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f))
                }
            }
        }
    }
}

@Composable
fun IngresoUnicoDialog(
    viewModel: MasterOperationsViewModel,
    catalogItems: List<CatalogItemEntity>,
    onDismiss: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var documentNumber by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var selectedCatalogItem by remember { mutableStateOf(catalogItems.firstOrNull()) }
    var agreedValueStr by remember { mutableStateOf(selectedCatalogItem?.referencePrice?.toInt()?.toString() ?: "250000") }
    var paymentMethod by remember { mutableStateOf("TRANSFERENCIA") }
    var createCase by remember { mutableStateOf(true) }
    var targetEntity by remember { mutableStateOf("Banco de Bogotá") }
    var caseMatter by remember { mutableStateOf("DERECHO_PETICION") }
    var radicado by remember { mutableStateOf("") }
    var risk by remember { mutableStateOf("MEDIO") }

    var showSm6Gate by remember { mutableStateOf(false) }

    if (showSm6Gate) {
        Sm6ConfirmCommitDialog(
            actionTitle = "Alta Atómica de Ingreso Único (AUT-012)",
            operationDescription = "Se emitirá Cliente, Servicio (${selectedCatalogItem?.name ?: "Personalizado"}), Obligación OBL y ${if (createCase) "Caso Jurídico ($targetEntity)" else "sin caso"}.",
            affectedEntity = "$fullName (CC: $documentNumber)",
            onConfirm = {
                val value = agreedValueStr.toDoubleOrNull() ?: 0.0
                viewModel.executeIngresoUnico(
                    fullName = fullName,
                    documentNumber = documentNumber,
                    phone = phone,
                    email = email,
                    serviceType = selectedCatalogItem?.name ?: "SANEAMIENTO_HISTORIAL",
                    agreedValue = value,
                    paymentMethod = paymentMethod,
                    createCase = createCase,
                    caseMatter = caseMatter,
                    targetEntity = targetEntity,
                    radicado = radicado,
                    risk = risk,
                    onSuccess = onDismiss
                )
                showSm6Gate = false
            },
            onDismiss = { showSm6Gate = false }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Ingreso Único (AUT-012/AUT-014)", fontWeight = FontWeight.Bold)
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text("1. Datos del Cliente / Titular", fontWeight = FontWeight.Bold, color = GoldAccent, fontSize = 13.sp)
                }
                item {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Nombre Completo") },
                        modifier = Modifier.fillMaxWidth().testTag("input_full_name")
                    )
                }
                item {
                    OutlinedTextField(
                        value = documentNumber,
                        onValueChange = { documentNumber = it },
                        label = { Text("Cédula / Documento (Sin puntos)") },
                        modifier = Modifier.fillMaxWidth().testTag("input_document")
                    )
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Teléfono") },
                            modifier = Modifier.weight(1f).testTag("input_phone")
                        )
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Correo") },
                            modifier = Modifier.weight(1f).testTag("input_email")
                        )
                    }
                }
                item {
                    Divider()
                    Text("2. Servicio Contratado del Catálogo Oficial", fontWeight = FontWeight.Bold, color = GoldAccent, fontSize = 13.sp)
                }
                item {
                    var expandedDropdown by remember { mutableStateOf(false) }
                    Box {
                        OutlinedButton(
                            onClick = { expandedDropdown = true },
                            modifier = Modifier.fillMaxWidth().testTag("select_service_button")
                        ) {
                            Text(
                                text = selectedCatalogItem?.name ?: "Seleccione Servicio del Catálogo",
                                maxLines = 1,
                                fontSize = 12.sp
                            )
                        }

                        DropdownMenu(
                            expanded = expandedDropdown,
                            onDismissRequest = { expandedDropdown = false },
                            modifier = Modifier.heightIn(max = 300.dp)
                        ) {
                            catalogItems.forEach { cat ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(cat.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text(cat.line, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    },
                                    onClick = {
                                        selectedCatalogItem = cat
                                        agreedValueStr = cat.referencePrice.toInt().toString()
                                        expandedDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = agreedValueStr,
                            onValueChange = { agreedValueStr = it },
                            label = { Text("Valor Pactado (COP)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = paymentMethod,
                            onValueChange = { paymentMethod = it },
                            label = { Text("Medio Pago") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    Divider()
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = createCase, onCheckedChange = { createCase = it })
                        Text("3. Vincular Caso / Trámite Jurídico (CAS)", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }
                if (createCase) {
                    item {
                        OutlinedTextField(
                            value = targetEntity,
                            onValueChange = { targetEntity = it },
                            label = { Text("Entidad Destino (Claro, Tigo, Banco de Bogotá, etc.)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = radicado,
                            onValueChange = { radicado = it },
                            label = { Text("Radicado (Opcional si es inicial)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { showSm6Gate = true },
                enabled = fullName.isNotBlank() && documentNumber.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
            ) {
                Text("Validar y Procesar", color = NavyDark, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun AddCatalogItemDialog(
    viewModel: MasterOperationsViewModel,
    onDismiss: () -> Unit
) {
    val lines = listOf(
        "Diagnóstico crediticio",
        "Historial crediticio y hábeas data",
        "Peticiones y reclamaciones",
        "Gestión de obligaciones financieras",
        "Gestión jurídica de demandas",
        "Tránsito",
        "Seguridad social",
        "Consultoría y acompañamiento integral"
    )
    var selectedLine by remember { mutableStateOf(lines[0]) }
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var scope by remember { mutableStateOf("Alcance según contratación") }
    var responsible by remember { mutableStateOf("GESTION_JURIDICA") }
    var priceStr by remember { mutableStateOf("150000") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Servicio en Catálogo Comercial", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    Text("Línea de Servicio Oficial:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    var lineExpanded by remember { mutableStateOf(false) }
                    Box {
                        OutlinedButton(onClick = { lineExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                            Text(selectedLine, fontSize = 12.sp)
                        }
                        DropdownMenu(expanded = lineExpanded, onDismissRequest = { lineExpanded = false }) {
                            lines.forEach { l ->
                                DropdownMenuItem(text = { Text(l) }, onClick = { selectedLine = l; lineExpanded = false })
                            }
                        }
                    }
                }
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre del Servicio") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = priceStr,
                        onValueChange = { priceStr = it },
                        label = { Text("Precio de Referencia (COP)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = priceStr.toDoubleOrNull() ?: 0.0
                    viewModel.addCatalogItem(selectedLine, name, description, scope, responsible, price, onDismiss)
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
            ) {
                Text("Guardar Servicio", color = NavyDark, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
fun NewQuotationDialog(
    viewModel: MasterOperationsViewModel,
    catalogItems: List<CatalogItemEntity>,
    onDismiss: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var documentNumber by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var selectedItem by remember { mutableStateOf(catalogItems.firstOrNull()) }
    var quotedAmountStr by remember { mutableStateOf(selectedItem?.referencePrice?.toInt()?.toString() ?: "150000") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Cotización Comercial", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text("Nombre del Prospecto") }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    OutlinedTextField(value = documentNumber, onValueChange = { documentNumber = it }, label = { Text("Documento / Cédula") }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Teléfono") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Correo") }, modifier = Modifier.weight(1f))
                    }
                }
                item {
                    var exp by remember { mutableStateOf(false) }
                    Box {
                        OutlinedButton(onClick = { exp = true }, modifier = Modifier.fillMaxWidth()) {
                            Text(selectedItem?.name ?: "Seleccione servicio", fontSize = 12.sp)
                        }
                        DropdownMenu(expanded = exp, onDismissRequest = { exp = false }, modifier = Modifier.heightIn(max = 250.dp)) {
                            catalogItems.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat.name) },
                                    onClick = {
                                        selectedItem = cat
                                        quotedAmountStr = cat.referencePrice.toInt().toString()
                                        exp = false
                                    }
                                )
                            }
                        }
                    }
                }
                item {
                    OutlinedTextField(value = quotedAmountStr, onValueChange = { quotedAmountStr = it }, label = { Text("Valor Cotizado (COP)") }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Condiciones / Notas") }, modifier = Modifier.fillMaxWidth())
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = quotedAmountStr.toDoubleOrNull() ?: 0.0
                    viewModel.createCommercialRequest(
                        fullName = fullName,
                        documentNumber = documentNumber,
                        phone = phone,
                        email = email,
                        catalogItemId = selectedItem?.id ?: "",
                        catalogItemName = selectedItem?.name ?: "Servicio",
                        catalogLine = selectedItem?.line ?: "General",
                        quotedAmount = amount,
                        notes = notes,
                        onSuccess = onDismiss
                    )
                },
                enabled = fullName.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
            ) {
                Text("Registrar Cotización", color = NavyDark, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
