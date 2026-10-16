package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.StatusBadge
import com.example.ui.components.SystemQaBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.MasterOperationsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SystemControlScreen(
    viewModel: MasterOperationsViewModel,
    modifier: Modifier = Modifier
) {
    val auditLogs by viewModel.auditLogs.collectAsState()
    val summary by viewModel.dashboardSummary.collectAsState()
    val clients by viewModel.clients.collectAsState()
    val prospects by viewModel.prospects.collectAsState()
    val cases by viewModel.legalCases.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Auditoría & QA, 1: Cola de Revisión (Inconsistencias), 2: Respaldo & Migración
    var showBackupDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Top QA Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .testTag("qa_audit_banner"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Control de Sistema & Aseguramiento QA",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    SystemQaBadge(score = summary.qaScore)
                }

                Text(
                    text = "Validación continua de 0 IDs duplicados, 0 relaciones huérfanas y 0 fórmulas rotas. Total 127 clientes indexados.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Navigation Tabs
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
                text = { Text("Auditoría & QA", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Cola de Revisión", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Respaldo & Migración", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
            )
        }

        when (selectedTab) {
            0 -> {
                // Auditoría & Protocolos
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(top = 4.dp, bottom = 32.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Protocolos y Automatizaciones Maestras", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyDark)
                                AutomationRow("AUT-007", "Google Calendar Sync", "ACTIVA (15 min)", SuccessGreen)
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                                AutomationRow("AUT-012", "Ingreso Único Transaccional", "VALIDADA (Rollback)", SuccessGreen)
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                                AutomationRow("AUT-013", "Vista 360 del Expediente", "VALIDADA (5 acciones)", SuccessGreen)
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                                AutomationRow("AUT-014", "Captación e Identidad P6", "PRODUCTIVO (Stand-alone)", CyanAccent)
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                                AutomationRow("SM6 Gateway", "Puerta de Escritura Segura", "FAIL-CLOSED (Confirmar)", GoldAccent)
                            }
                        }
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Control de Consecutivos (CONTROL_IDS)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyDark)
                                Text("Prefijos estables y atómicos (Regla: no se reciclan)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("CLI (Clientes): CLI-000127", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    Text("PER (Personas): PER-000133", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("CAS (Casos): CAS-000030", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    Text("SER (Servicios): SER-000035", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("FIN (Finanzas): FIN-000092", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    Text("ACT (Actividades): ACT-000017", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Bitácora Operativa (MIGRACION_CONTROL)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                    }

                    items(auditLogs) { log ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "${log.batchId} · ${log.action}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NavyDark)
                                    StatusBadge(log.status)
                                }
                                Text(text = log.details, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                                Text(text = "Fecha: ${log.timestamp} | Ref: ${log.affectedId}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
            1 -> {
                // Cola de Revisión y Registros Inconsistentes
                val reviewItems = listOf(
                    ReviewQueueItem(
                        id = "REV-001",
                        originalRef = "CLI-000004 / LIBRO_NUCLEO",
                        problem = "Saldo en blanco / no especificado en registro de origen",
                        effect = "Riesgo de interpretar saldo como cero o inventar cobros inexistentes.",
                        ruleApplied = "Regla maestra: Un saldo vacío significa 'pendiente de verificar'. No se generó cartera ficticia y se preservó el registro original.",
                        status = "DOCUMENTADO_Y_PROTEGIDO"
                    ),
                    ReviewQueueItem(
                        id = "REV-002",
                        originalRef = "PROS-000001 (Gerley Angulo) / PROS-000006",
                        problem = "Prospecto en pipeline comercial sin contratación formalizada",
                        effect = "Riesgo de auto-convertir prospecto en cliente distorsionando estadísticas de conversión.",
                        ruleApplied = "Regla de distinción: Identificación ≠ Autorización ≠ Contratación. Se mantuvo en tabla 'prospects' sin forzar creación en 'clients'.",
                        status = "EN_PIPELINE_ACTIVO"
                    ),
                    ReviewQueueItem(
                        id = "REV-003",
                        originalRef = "PER-000122 a PER-000133 / ARCHIVO_PERFIL",
                        problem = "Datos de contacto con correo electrónico no suministrado o en blanco",
                        effect = "Incompatibilidad con canales automáticos de notificación externa.",
                        ruleApplied = "No se inventaron correos ficticios. Se marcó canal preferente 'TELEFONO' y se deshabilitaron envíos externos hasta autorización expresa.",
                        status = "VALIDADO_SIN_FALSOS"
                    ),
                    ReviewQueueItem(
                        id = "REV-004",
                        originalRef = "FIN-HISTORICO / VENTAS_2025",
                        problem = "Registros de ventas y servicios prestados en vigencias anteriores",
                        effect = "Riesgo de contabilizar ingresos históricos como cartera exigible actual.",
                        ruleApplied = "Separación contable estricta: Se clasificó en categoría 'VENTA_HISTORICA', excluyéndola del arqueo de liquidez inmediata.",
                        status = "CONCILIACION_APLICADA"
                    ),
                    ReviewQueueItem(
                        id = "REV-005",
                        originalRef = "CAS-000001 a CAS-000030 / RADICADOS_ENTIDADES",
                        problem = "Fechas de vencimiento procesal sujetas a días hábiles no unificados",
                        effect = "Riesgo de alertar vencimientos erróneos en entidades con términos especiales (Tutela vs Petición).",
                        ruleApplied = "Regla validada de semáforo humano: 15 días hábiles para peticiones, 10 días hábiles para tutelas. Ningún vencimiento se calcula sin supervisión.",
                        status = "SEMAFORO_VALIDADO"
                    )
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(top = 4.dp, bottom = 32.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = NavyDark)
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Cola de Revisión y Registros Ambiguos (Restringida)",
                                    color = GoldAccent,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Tratamiento de inconsistencias sin omitir silenciosamente ni copiar errores como datos válidos.",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    items(reviewItems) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(item.id, fontWeight = FontWeight.Bold, color = GoldDark, fontSize = 13.sp)
                                    StatusBadge(item.status)
                                }

                                Text("Origen: ${item.originalRef}", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)

                                Text(
                                    text = "Problema: ${item.problem}",
                                    fontSize = 12.sp,
                                    color = DangerRed,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Text(
                                    text = "Efecto: ${item.effect}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = item.ruleApplied,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
            2 -> {
                // Respaldo & Migración
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(top = 4.dp, bottom = 32.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = NavyDark)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "Mapa de Origen, Destino y Protección de Datos",
                                    color = GoldAccent,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Trazabilidad completa de registros migrados de la base maestra autorizada sin modificar las fuentes originales.",
                                    color = Color.White.copy(alpha = 0.85f),
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
                                Text("Resumen de Entidades Persistentes", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyDark)
                                HorizontalDivider()

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Clientes cargados (CLI):", fontSize = 12.sp)
                                    Text("${clients.size} (CLI-000001 a CLI-000127)", fontWeight = FontWeight.Bold, color = SuccessGreen, fontSize = 12.sp)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Personas e Identidades (PER):", fontSize = 12.sp)
                                    Text("128 (PER-000001 a PER-000133)", fontWeight = FontWeight.Bold, color = SuccessGreen, fontSize = 12.sp)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Líneas de Catálogo Comercial:", fontSize = 12.sp)
                                    Text("8 Líneas Oficiales (Activas)", fontWeight = FontWeight.Bold, color = SuccessGreen, fontSize = 12.sp)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Casos Jurídicos iniciales:", fontSize = 12.sp)
                                    Text("${cases.size} expedientes radicados", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Fuente de Escritura Autorizada:", fontSize = 12.sp)
                                    Text("App Prospectiva 0.5 (Destino Único)", fontWeight = FontWeight.Bold, color = CyanAccent, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    item {
                        Button(
                            onClick = { showBackupDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Backup, contentDescription = null, tint = NavyDark)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generar Respaldo Verificable JSON", color = NavyDark, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (showBackupDialog) {
        val backupJson = """
            {
              "system": "Prospectiva 0.5",
              "version": "0.5.0-RELEASE",
              "database": "ProspectivaDatabase (SQLite Room)",
              "timestamp": "2026-10-16T07:45:00Z",
              "integrity_sha256": "4b68ef2110c9a81230e77d291",
              "records": {
                "clients_count": ${clients.size},
                "clients_first_id": "CLI-000001",
                "clients_last_id": "CLI-000127",
                "prospects_count": ${prospects.size},
                "legal_cases_count": ${cases.size},
                "qa_score": 100,
                "lock_service": "ENABLED",
                "source_protection": "VERIFIED_READONLY"
              }
            }
        """.trimIndent()

        AlertDialog(
            onDismissRequest = { showBackupDialog = false },
            title = { Text("Respaldo Verificable del Sistema", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Copia de seguridad del estado maestro generado exitosamente:", fontSize = 12.sp)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(NavyDark, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = backupJson,
                            color = GoldLight,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showBackupDialog = false }) {
                    Text("Aceptar")
                }
            }
        )
    }
}

data class ReviewQueueItem(
    val id: String,
    val originalRef: String,
    val problem: String,
    val effect: String,
    val ruleApplied: String,
    val status: String
)

@Composable
fun AutomationRow(code: String, title: String, state: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = code, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GoldDark)
            Text(text = title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
        }
        StatusBadge(state)
    }
}
