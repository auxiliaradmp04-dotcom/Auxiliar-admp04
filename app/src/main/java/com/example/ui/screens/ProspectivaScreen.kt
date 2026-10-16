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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.RoadmapMilestoneEntity
import com.example.data.local.entity.ScenarioEntity
import com.example.data.local.entity.TrendEntity
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.MasterOperationsViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ProspectivaScreen(
    viewModel: MasterOperationsViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Radar, 1: Simulador, 2: Hoja de Ruta, 3: Indicadores

    val trends by viewModel.trends.collectAsState()
    val scenarios by viewModel.scenarios.collectAsState()
    val milestones by viewModel.roadmapMilestones.collectAsState()
    val simOutput by viewModel.simulationOutput.collectAsState()

    var showAddTrendDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = { showAddTrendDialog = true },
                    containerColor = GoldAccent,
                    contentColor = NavyDark,
                    modifier = Modifier.testTag("fab_add_trend")
                ) {
                    Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Radar, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Nueva Señal / Tendencia", fontWeight = FontWeight.Bold)
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
            // Tab Selector
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
                    text = { Text("Radar de Tendencias", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Simulador What-If", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Hoja de Ruta", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("Indicadores Adelantados", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) }
                )
            }

            when (selectedTab) {
                0 -> RadarTabContent(trends = trends)
                1 -> SimulatorTabContent(
                    viewModel = viewModel,
                    simOutput = simOutput,
                    scenarios = scenarios
                )
                2 -> RoadmapTabContent(
                    milestones = milestones,
                    onUpdateStatus = { m, s -> viewModel.updateRoadmapMilestoneStatus(m, s) }
                )
                3 -> LeadingIndicatorsTabContent()
            }
        }

        if (showAddTrendDialog) {
            AddTrendDialog(
                viewModel = viewModel,
                onDismiss = { showAddTrendDialog = false }
            )
        }
    }
}

@Composable
fun RadarTabContent(trends: List<TrendEntity>) {
    var selectedCategory by remember { mutableStateOf("TODAS") }

    val filtered = remember(trends, selectedCategory) {
        if (selectedCategory == "TODAS") trends
        else trends.filter { it.category == selectedCategory }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("radar_header_banner"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDark)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.linearGradient(listOf(NavyDark, NavyCard)))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("RADAR ESTRATÉGICO", color = GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("Índice de Resiliencia: 94/100", color = SuccessGreenLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            text = "Monitoreo de Señales Tempranas y Megatendencias",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Vigilancia prospectiva para anticipar disrupciones tecnológicas, regulatorias y de mercado antes de que impacten la operación.",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val categories = listOf("TODAS", "TECNOLOGICA", "REGULATORIA", "MERCADO", "LABORAL")
                items(categories) { c ->
                    FilterChip(
                        selected = selectedCategory == c,
                        onClick = { selectedCategory = c },
                        label = { Text(c.replace("_", " "), fontSize = 11.sp) }
                    )
                }
            }
        }

        items(filtered, key = { it.id }) { trend ->
            TrendCard(trend = trend)
        }
    }
}

@Composable
fun TrendCard(trend: TrendEntity) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("trend_card_${trend.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(trend.id, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                    StatusBadge(trend.category)
                }
                StatusBadge(trend.maturity)
            }

            Text(
                text = trend.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = trend.description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Detonante: ${trend.triggerFactor}",
                    fontSize = 11.sp,
                    color = CyanAccent,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "Impacto: ${"★".repeat(trend.impactScore)}",
                    fontSize = 12.sp,
                    color = GoldDark,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun SimulatorTabContent(
    viewModel: MasterOperationsViewModel,
    simOutput: com.example.ui.viewmodel.SimulationResult,
    scenarios: List<ScenarioEntity>
) {
    val growth by viewModel.simGrowthPct.collectAsState()
    val conversion by viewModel.simCrmConversionPct.collectAsState()
    val lagDays by viewModel.simCollectionLagDays.collectAsState()
    val risk by viewModel.simRegulatoryRisk.collectAsState()

    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply { maximumFractionDigits = 0 }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
    ) {
        // Output Summary Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("sim_output_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Proyección Semestral Simulada", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        val stressColor = when {
                            simOutput.liquidityStressScore > 75 -> SuccessGreen
                            simOutput.liquidityStressScore in 50..75 -> WarningAmber
                            else -> DangerRed
                        }
                        Box(
                            modifier = Modifier
                                .background(stressColor.copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Salud: ${simOutput.liquidityStressScore}/100",
                                color = stressColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Ingresos Proyectados (6M)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                currencyFormat.format(simOutput.projectedSixMonthRevenue),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyDark
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Capacidad Casos", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                "${simOutput.projectedCaseVolume} expedientes",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanAccent
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White.copy(alpha = 0.6f), shape = RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                            Text(
                                text = simOutput.strategicRecommendation,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = NavyDark
                            )
                        }
                    }
                }
            }
        }

        // Interactive Sliders Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Ajuste de Variables de Escenario (What-If)", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                    // Slider 1: Growth
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Variación en Demanda / Nuevos Clientes:", fontSize = 12.sp)
                            Text("${if (growth >= 0) "+" else ""}$growth%", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (growth >= 0) SuccessGreen else DangerRed)
                        }
                        Slider(
                            value = growth.toFloat(),
                            onValueChange = { viewModel.updateSimulationGrowth(it.toInt()) },
                            valueRange = -30f..50f,
                            steps = 15
                        )
                    }

                    // Slider 2: CRM Conversion
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Tasa de Conversión CRM Prospectiva:", fontSize = 12.sp)
                            Text("$conversion%", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CyanAccent)
                        }
                        Slider(
                            value = conversion.toFloat(),
                            onValueChange = { viewModel.updateSimulationConversion(it.toInt()) },
                            valueRange = 10f..90f,
                            steps = 15
                        )
                    }

                    // Slider 3: Collection lag
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Plazo Medio de Cobro / Desembolso:", fontSize = 12.sp)
                            Text("$lagDays días", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (lagDays > 45) DangerRed else NavyDark)
                        }
                        Slider(
                            value = lagDays.toFloat(),
                            onValueChange = { viewModel.updateSimulationLag(it.toInt()) },
                            valueRange = 10f..90f,
                            steps = 15
                        )
                    }

                    // Segmented Risk
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Riesgo Regulatorio / Litigioso:", fontSize = 12.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf("BAJO", "MEDIO", "ALTO").forEach { r ->
                                FilterChip(
                                    selected = risk == r,
                                    onClick = { viewModel.updateSimulationRisk(r) },
                                    label = { Text(r, fontSize = 10.sp) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Prebuilt Scenarios
        item {
            Text("Modelos de Escenarios Configurados", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        items(scenarios) { scn ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(scn.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        StatusBadge(scn.scenarioType)
                    }
                    Text(scn.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Detonantes Clave: ${scn.keyTriggers}", fontSize = 11.sp, color = GoldDark)
                    Text("Plan de Acción: ${scn.actionPlan}", fontSize = 11.sp, color = CyanAccent)
                }
            }
        }
    }
}

@Composable
fun RoadmapTabContent(
    milestones: List<RoadmapMilestoneEntity>,
    onUpdateStatus: (RoadmapMilestoneEntity, String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Hoja de Ruta Estratégica & Backcasting", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(
                        "Conexión entre la visión futura de Prospectiva 0.5 y las metas operativas actuales. Los hitos sincronizan prioridades y recursos.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        items(milestones, key = { it.id }) { m ->
            Card(
                modifier = Modifier.fillMaxWidth().testTag("milestone_${m.id}"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(m.quarter, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = GoldAccent)
                            StatusBadge(m.impactLevel)
                        }
                        StatusBadge(m.status)
                    }

                    Text(m.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(m.strategicGoal, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Fecha Objetivo: ${m.targetDate}", fontSize = 11.sp, color = NavyDark, fontWeight = FontWeight.SemiBold)

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf("PLANIFICADO", "EN_CURSO", "COMPLETADO").forEach { st ->
                                if (m.status != st) {
                                    OutlinedButton(
                                        onClick = { onUpdateStatus(m, st) },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text(st.replace("_", " "), fontSize = 9.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LeadingIndicatorsTabContent() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Indicadores Líderes vs Rezagados", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(
                        "Métricas de anticipación diseñadas para alertar desvíos antes de que ocurran los vencimientos o tensiones de tesorería.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            LeadingMetricRow(
                title = "Flujo de Caja Adelantado (30 Días)",
                status = "HOLGADO",
                description = "Disponible actual ($249k) + radicación RAP ($3.8M) cubre obligaciones de los 10 días ($2.0M).",
                color = SuccessGreen
            )
        }

        item {
            LeadingMetricRow(
                title = "Ventana Regulatoria de Respuestas (15 Días)",
                status = "EN TERMINO",
                description = "0 peticiones con vencimiento en menos de 48 horas. Próxima revisión CAS-000024 el 31/08.",
                color = CyanAccent
            )
        }

        item {
            LeadingMetricRow(
                title = "Pipeline de Conversión a Cliente (60 Días)",
                status = "ACTIVO",
                description = "PROS-000001 (Gerley Angulo) y PROS-000006 (Laura Valentina) en validación para conversión AUT-012.",
                color = GoldDark
            )
        }

        item {
            LeadingMetricRow(
                title = "Capacidad Operativa de Consultorías (90 Días)",
                status = "DISPONIBLE",
                description = "Postulación OIM CONS 111 enviada con soporte. Capacidad lista para asignación de horas docentes y proyectos.",
                color = InfoBlue
            )
        }
    }
}

@Composable
fun LeadingMetricRow(title: String, status: String, description: String, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                StatusBadge(status)
            }
            Text(description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun AddTrendDialog(
    viewModel: MasterOperationsViewModel,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("TECNOLOGICA") }
    var horizon by remember { mutableStateOf("CORTO_PLAZO") }
    var maturity by remember { mutableStateOf("TENDENCIA_EMERGENTE") }
    var impactScore by remember { mutableIntStateOf(4) }
    var probabilityScore by remember { mutableIntStateOf(4) }
    var description by remember { mutableStateOf("") }
    var triggerFactor by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar Señal / Tendencia en Radar", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Título de la Tendencia / Señal") },
                        modifier = Modifier.fillMaxWidth().testTag("input_trend_title")
                    )
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = { category = it },
                            label = { Text("Categoría (TECNOLOGICA, REGULATORIA, MERCADO)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = horizon,
                            onValueChange = { horizon = it },
                            label = { Text("Horizonte (CORTO, MEDIANO)") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = maturity,
                        onValueChange = { maturity = it },
                        label = { Text("Madurez (SENAL_DEBIL, TENDENCIA, MEGATENDENCIA)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Descripción del cambio futuro") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = triggerFactor,
                        onValueChange = { triggerFactor = it },
                        label = { Text("Detonante o indicador temprano") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.addTrend(
                        title = title,
                        category = category,
                        horizon = horizon,
                        maturity = maturity,
                        impactScore = impactScore,
                        probabilityScore = probabilityScore,
                        description = description,
                        triggerFactor = triggerFactor,
                        onSuccess = onDismiss
                    )
                },
                enabled = title.isNotBlank() && description.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
            ) {
                Text("Registrar en Radar", color = NavyDark, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
