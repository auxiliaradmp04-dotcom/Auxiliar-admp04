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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.FinancialMovementEntity
import com.example.data.local.entity.PaymentObligationEntity
import com.example.ui.components.Sm6ConfirmCommitDialog
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.MasterOperationsViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceScreen(
    viewModel: MasterOperationsViewModel,
    modifier: Modifier = Modifier
) {
    val movements by viewModel.financialMovements.collectAsState()
    val obligations by viewModel.paymentObligations.collectAsState()
    val summary by viewModel.dashboardSummary.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Movimientos, 1: Cartera & Obligaciones, 2: Cierre Mensual & Conciliación
    var selectedScope by remember { mutableStateOf("TODOS") }
    var showAddMovementDialog by remember { mutableStateOf(false) }
    var showAddPaymentDialog by remember { mutableStateOf(false) }
    var selectedObligationForPayment by remember { mutableStateOf<PaymentObligationEntity?>(null) }

    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply {
        maximumFractionDigits = 0
    }

    val filteredMovements = remember(movements, selectedScope) {
        if (selectedScope == "TODOS") movements
        else movements.filter { it.scope == selectedScope }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            when (selectedTab) {
                0 -> {
                    FloatingActionButton(
                        onClick = { showAddMovementDialog = true },
                        containerColor = GoldAccent,
                        contentColor = NavyDark,
                        modifier = Modifier.testTag("fab_add_finance")
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Nuevo Movimiento", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                1 -> {
                    FloatingActionButton(
                        onClick = {
                            selectedObligationForPayment = obligations.firstOrNull { it.balance > 0 }
                            showAddPaymentDialog = true
                        },
                        containerColor = SuccessGreen,
                        contentColor = Color.White,
                        modifier = Modifier.testTag("fab_abono_obligacion")
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Payment, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Registrar Abono", fontWeight = FontWeight.Bold, color = Color.White)
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
            // Balances summary card (Live Treasury)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Saldos de Tesorería (Liquidez Real)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(currencyFormat.format(summary.availableCash), fontWeight = FontWeight.Bold, color = SuccessGreen, fontSize = 15.sp)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Caja Menor: $55.550", fontSize = 11.sp)
                        Text("Efectivo: $81.000", fontSize = 11.sp)
                        Text("Bancolombia: $31.142", fontSize = 11.sp)
                        Text("Daviplata: $81.410", fontSize = 11.sp)
                    }
                }
            }

            // Tabs: Movimientos | Cartera & Obligaciones | Cierre Mensual
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Movimientos (${movements.size})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Cartera & Obligaciones (${obligations.size})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Cierre Mensual", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                )
            }

            when (selectedTab) {
                0 -> {
                    // Scope filter chips
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val scopes = listOf("TODOS", "PERSONAL", "PROSPECTIVA", "PROYECTO", "DEPENDIENTE")
                        items(scopes) { s ->
                            FilterChip(
                                selected = selectedScope == s,
                                onClick = { selectedScope = s },
                                label = { Text(s, fontSize = 11.sp) }
                            )
                        }
                    }

                    Text(
                        text = "Mostrando ${filteredMovements.size} registros de caja",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    // Movements list
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(filteredMovements, key = { it.id }) { mov ->
                            MovementItemCard(movement = mov, currencyFormat = currencyFormat)
                        }
                    }
                }
                1 -> {
                    // Cartera & Obligaciones de Clientes
                    val pendingObligations = obligations.filter { it.balance > 0 }
                    val totalPendingBalance = obligations.sumOf { it.balance }
                    val totalAgreedAmount = obligations.sumOf { it.totalAmount }
                    val totalPaidAmount = obligations.sumOf { it.paidAmount }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(top = 6.dp, bottom = 80.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = NavyDark)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Cartera Total Exigible", color = GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text(currencyFormat.format(totalPendingBalance), color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                        Text("${pendingObligations.size} obligaciones pendientes de recaudo", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Recaudado: ${currencyFormat.format(totalPaidAmount)}", color = SuccessGreen, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                        Text("Contratado: ${currencyFormat.format(totalAgreedAmount)}", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                                    }
                                }
                            }
                        }

                        items(obligations, key = { it.id }) { obl ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(obl.id, fontWeight = FontWeight.Bold, color = GoldDark, fontSize = 13.sp)
                                        StatusBadge(obl.status)
                                    }

                                    Text(obl.concept, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Cliente: ${obl.clientId} | Vence: ${obl.dueDate}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("Total: ${currencyFormat.format(obl.totalAmount)}", fontSize = 11.sp)
                                            Text("Pagado: ${currencyFormat.format(obl.paidAmount)}", fontSize = 11.sp, color = SuccessGreen)
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("Saldo: ${currencyFormat.format(obl.balance)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (obl.balance > 0) DangerRed else SuccessGreen)
                                            if (obl.balance > 0) {
                                                TextButton(
                                                    onClick = {
                                                        selectedObligationForPayment = obl
                                                        showAddPaymentDialog = true
                                                    },
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                                ) {
                                                    Icon(Icons.Default.AddCard, contentDescription = null, modifier = Modifier.size(14.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Abonar", fontSize = 11.sp)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // Cierre Mensual & Conciliación con Informe Reproducible
                    MonthlyClosureReportView(
                        movements = movements,
                        obligations = obligations,
                        summary = summary,
                        currencyFormat = currencyFormat
                    )
                }
            }
        }
    }

    // Modal: Nuevo Movimiento Financiero
    if (showAddMovementDialog) {
        AddFinancialMovementDialog(
            viewModel = viewModel,
            onDismiss = { showAddMovementDialog = false }
        )
    }

    // Modal: Registrar Abono a Obligación
    if (showAddPaymentDialog) {
        val obl = selectedObligationForPayment
        var amountText by remember { mutableStateOf(obl?.balance?.toInt()?.toString() ?: "150000") }
        var paymentMethod by remember { mutableStateOf("TRANSFERENCIA") }
        var receiptRef by remember { mutableStateOf("") }
        var notes by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddPaymentDialog = false },
            title = { Text("Registrar Abono a Obligación", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (obl != null) {
                        Text("Obligación: ${obl.id} (${obl.clientId})", fontSize = 12.sp, color = GoldDark, fontWeight = FontWeight.Bold)
                        Text(obl.concept, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Saldo actual: ${currencyFormat.format(obl.balance)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DangerRed)
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
                        label = { Text("Referencia o Nro. Soporte") },
                        placeholder = { Text("Ej. COMP-098213") },
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

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notas de conciliación") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val obligationId = obl?.id ?: return@Button
                        val amount = amountText.toDoubleOrNull() ?: 0.0
                        viewModel.registerPaymentForObligation(
                            obligationId = obligationId,
                            amount = amount,
                            paymentMethod = paymentMethod,
                            receiptRef = receiptRef,
                            notes = notes,
                            onSuccess = { showAddPaymentDialog = false }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                ) {
                    Text("Confirmar Abono", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddPaymentDialog = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
fun MonthlyClosureReportView(
    movements: List<FinancialMovementEntity>,
    obligations: List<PaymentObligationEntity>,
    summary: com.example.ui.viewmodel.DashboardSummary,
    currencyFormat: NumberFormat
) {
    val totalIncomes = movements.filter { it.type == "INGRESO" }.sumOf { it.amount }
    val totalExpenses = movements.filter { it.type == "GASTO" && it.state == "CONFIRMADO" }.sumOf { it.amount }
    val totalCartera = obligations.sumOf { it.balance }
    val totalCuentasPagar = summary.accountsPayable

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 40.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("monthly_closure_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDark)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Informe Reproducible de Cierre Mensual",
                            color = GoldAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        StatusBadge("CONCILIADO")
                    }

                    Text(
                        text = "Prospectiva 0.5 · S.A.S. — Estado Financiero y Cartera",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Text(
                        text = "Corte contable con trazabilidad completa de obligaciones, recaudos y cuentas de liquidez.",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("1. Resumen de Flujo Efectivo y Cartera", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyDark)
                    HorizontalDivider()

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Ingresos Efectivos Confirmados:", fontSize = 12.sp)
                        Text(currencyFormat.format(totalIncomes), fontWeight = FontWeight.Bold, color = SuccessGreen, fontSize = 12.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Egresos y Gastos Operacionales:", fontSize = 12.sp)
                        Text(currencyFormat.format(totalExpenses), fontWeight = FontWeight.Bold, color = DangerRed, fontSize = 12.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Cartera por Cobrar Exigible (OBL):", fontSize = 12.sp)
                        Text(currencyFormat.format(totalCartera), fontWeight = FontWeight.Bold, color = WarningAmber, fontSize = 12.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Cuentas por Pagar a Proveedores:", fontSize = 12.sp)
                        Text(currencyFormat.format(totalCuentasPagar), fontWeight = FontWeight.Bold, color = DangerRed, fontSize = 12.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Pendiente RAP Pacífico (Convenio):", fontSize = 12.sp)
                        Text(currencyFormat.format(summary.rapPacificoPending), fontWeight = FontWeight.Bold, color = CyanAccent, fontSize = 12.sp)
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("2. Arqueo y Conciliación de Cuentas", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyDark)
                    HorizontalDivider()

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Caja Menor:", fontSize = 12.sp)
                        Text("$55.550 COP", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Efectivo en Oficina:", fontSize = 12.sp)
                        Text("$81.000 COP", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Cuenta Bancolombia:", fontSize = 12.sp)
                        Text("$31.142 COP", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Cuenta Daviplata:", fontSize = 12.sp)
                        Text("$81.410 COP", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                    HorizontalDivider()
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Saldo Neto de Liquidez Inmediata:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyDark)
                        Text(currencyFormat.format(summary.availableCash), fontWeight = FontWeight.Bold, color = SuccessGreen, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun MovementItemCard(
    movement: FinancialMovementEntity,
    currencyFormat: NumberFormat
) {
    val isIncome = movement.type == "INGRESO"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("movement_${movement.id}"),
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
                    Text(
                        text = movement.id,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )
                    StatusBadge(movement.scope)
                    StatusBadge(movement.category)
                }
                Text(
                    text = "${if (isIncome) "+" else "-"} ${currencyFormat.format(movement.amount)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = if (isIncome) SuccessGreen else DangerRed
                )
            }

            Text(
                text = movement.concept,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Tercero: ${movement.thirdParty} (${movement.paymentMethod})",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                StatusBadge(movement.supportType)
            }

            if (movement.dueDate.isNotBlank()) {
                Text(
                    text = "Vencimiento / Control: ${movement.dueDate}",
                    fontSize = 11.sp,
                    color = WarningAmber,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun AddFinancialMovementDialog(
    viewModel: MasterOperationsViewModel,
    onDismiss: () -> Unit
) {
    var type by remember { mutableStateOf("GASTO") }
    var scope by remember { mutableStateOf("PERSONAL") }
    var category by remember { mutableStateOf("SERVICIOS_PUBLICOS") }
    var concept by remember { mutableStateOf("") }
    var thirdParty by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("EFECTIVO") }
    var supportType by remember { mutableStateOf("CONSTANCIA_INTERNA") }
    var dueDate by remember { mutableStateOf("2026-09-10") }
    var notes by remember { mutableStateOf("") }

    var showSm6Gate by remember { mutableStateOf(false) }

    if (showSm6Gate) {
        val amount = amountStr.toDoubleOrNull() ?: 0.0
        Sm6ConfirmCommitDialog(
            actionTitle = "Registro de Movimiento Financiero (FIN)",
            operationDescription = "Se registrará $type por $amount COP en ámbito $scope ($category).",
            affectedEntity = "$concept - $thirdParty",
            onConfirm = {
                viewModel.addFinancialMovement(
                    type = type,
                    scope = scope,
                    category = category,
                    concept = concept,
                    thirdParty = thirdParty,
                    amount = amount,
                    paymentMethod = paymentMethod,
                    supportType = supportType,
                    dueDate = dueDate,
                    notes = notes,
                    onSuccess = onDismiss
                )
                showSm6Gate = false
            },
            onDismiss = { showSm6Gate = false }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar Movimiento Financiero", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = type,
                            onValueChange = { type = it },
                            label = { Text("Tipo (INGRESO / GASTO)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = scope,
                            onValueChange = { scope = it },
                            label = { Text("Ámbito (PERSONAL, PROSPECTIVA)") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Categoría (SERVICIOS, HONORARIOS, FAMILIA)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = concept,
                        onValueChange = { concept = it },
                        label = { Text("Concepto") },
                        modifier = Modifier.fillMaxWidth().testTag("input_fin_concept")
                    )
                }
                item {
                    OutlinedTextField(
                        value = thirdParty,
                        onValueChange = { thirdParty = it },
                        label = { Text("Tercero / Proveedor / Beneficiario") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = amountStr,
                        onValueChange = { amountStr = it },
                        label = { Text("Monto (COP)") },
                        modifier = Modifier.fillMaxWidth().testTag("input_fin_amount")
                    )
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = paymentMethod,
                            onValueChange = { paymentMethod = it },
                            label = { Text("Medio Pago") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = supportType,
                            onValueChange = { supportType = it },
                            label = { Text("Soporte (DOC, CONSTANCIA)") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { showSm6Gate = true },
                enabled = concept.isNotBlank() && (amountStr.toDoubleOrNull() ?: 0.0) > 0,
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
            ) {
                Text("Validar Commit", color = NavyDark, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
