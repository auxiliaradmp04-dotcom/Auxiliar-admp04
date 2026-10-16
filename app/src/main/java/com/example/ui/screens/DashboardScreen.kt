package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MetricCard
import com.example.ui.components.StatusBadge
import com.example.ui.components.SystemQaBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.MasterOperationsViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: MasterOperationsViewModel,
    onNavigateToProspectiva: () -> Unit,
    onNavigateToCrm: () -> Unit,
    onNavigateToLegal: () -> Unit,
    onNavigateToFinance: () -> Unit,
    onNavigateToAgenda: () -> Unit,
    onNavigateToEmployment: () -> Unit,
    onNavigateTo360: () -> Unit,
    modifier: Modifier = Modifier
) {
    val summary by viewModel.dashboardSummary.collectAsState()
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply {
        maximumFractionDigits = 0
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
    ) {
        // Executive Header Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("executive_header_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDark)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(NavyDark, NavyCard)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "PROSPECTIVA 0.5",
                                    color = GoldAccent,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "Tablero Maestro Ejecutivo",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            SystemQaBadge(score = summary.qaScore)
                        }

                        Text(
                            text = "Sistema consolidado de operaciones: CRM, jurídico, finanzas y agenda integrados.",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 13.sp
                        )

                        Divider(color = Color.White.copy(alpha = 0.15f), thickness = 1.dp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.CloudDone, contentDescription = null, tint = CyanLight, modifier = Modifier.size(16.dp))
                                Text("AUT-007 Calendar Activo", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                                Text("Puerta SM6 Fail-Closed", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Financial Overview Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("financial_overview_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.AccountBalance, contentDescription = null, tint = CyanAccent)
                            Text(
                                text = "Posición Financiera Consolidada",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        TextButton(onClick = onNavigateToFinance) {
                            Text("Ver Detalle")
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Liquidity box
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = SuccessGreenLight)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Disponible Caja/Bancos", fontSize = 11.sp, color = Color(0xFF065F46), fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = currencyFormat.format(summary.availableCash),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF064E3B)
                                )
                                Text("Caja, Bancolombia, DaviPlata", fontSize = 10.sp, color = Color(0xFF065F46).copy(alpha = 0.8f))
                            }
                        }

                        // RAP Pacifico box
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = CyanGlow)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("RAP Pacífico Pendiente", fontSize = 11.sp, color = Color(0xFF075985), fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = currencyFormat.format(summary.rapPacificoPending),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0C4A6E)
                                )
                                Text("Por presentar cuenta cobro", fontSize = 10.sp, color = Color(0xFF075985).copy(alpha = 0.8f))
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Obligaciones 10 días (CxP):", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                currencyFormat.format(summary.accountsPayable),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DangerRed
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Saldo Proyectado:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                currencyFormat.format(summary.projectedBalance),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SuccessGreen
                            )
                        }
                    }
                }
            }
        }

        // Strategic Foresight & Trends Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("strategic_foresight_banner"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(GoldAccent.copy(alpha = 0.15f), shape = RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Radar, contentDescription = null, tint = GoldDark, modifier = Modifier.size(20.dp))
                            }
                            Column {
                                Text(
                                    text = "Prospectiva Estratégica & Futuro",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Análisis de Tendencias y Modelado de Escenarios",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        StatusBadge("ACTIVO")
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Radar de Señales", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("5 Tendencias", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyDark)
                            }
                        }
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Escenario Base", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("+18% Sostenible", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SuccessGreen)
                            }
                        }
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Resiliencia", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("94/100", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GoldDark)
                            }
                        }
                    }

                    Button(
                        onClick = onNavigateToProspectiva,
                        modifier = Modifier.fillMaxWidth().testTag("btn_open_prospectiva"),
                        colors = ButtonDefaults.buttonColors(containerColor = NavyDark)
                    ) {
                        Icon(Icons.Default.TrendingUp, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Explorar Radar y Simular Escenarios (What-If)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Operational Metrics Grid
        item {
            Text(
                text = "Módulos Operativos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Clientes Activos",
                    value = "${summary.activeClientsCount}",
                    subtitle = "${summary.prospectsCount} en prospectiva",
                    icon = Icons.Default.People,
                    accentColor = GoldAccent,
                    modifier = Modifier.weight(1f).clickable { onNavigateToCrm() }
                )

                MetricCard(
                    title = "Casos Jurídicos",
                    value = "${summary.openCasesCount}",
                    subtitle = if (summary.termsDueUrgent > 0) "${summary.termsDueUrgent} término(s) crítico(s)" else "0 vencidos",
                    icon = Icons.Default.Gavel,
                    accentColor = if (summary.termsDueUrgent > 0) DangerRed else CyanAccent,
                    modifier = Modifier.weight(1f).clickable { onNavigateToLegal() }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Agenda de Hoy / Foco",
                    value = "${summary.openActivitiesCount}",
                    subtitle = "Sincronizado Calendar",
                    icon = Icons.Default.CalendarToday,
                    accentColor = CyanAccent,
                    modifier = Modifier.weight(1f).clickable { onNavigateToAgenda() }
                )

                MetricCard(
                    title = "Convocatorias Empleo",
                    value = "${summary.activeEmploymentCount}",
                    subtitle = "OIM CONS 111 POSTULADA",
                    icon = Icons.Default.Work,
                    accentColor = GoldAccent,
                    modifier = Modifier.weight(1f).clickable { onNavigateToEmployment() }
                )
            }
        }

        // Urgent Operational Semaphores
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("urgent_alerts_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Semáforo y Alertas Operativas",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    // Alert 1: Legal case draft
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("CAS-000024: Gennials BPO", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Revisión borrador Gmail antes de envío", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        StatusBadge("ALTA")
                    }

                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                    // Alert 2: Prospect appointment
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("ACT-000009: Cita Gerley Angulo", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("10/09/2026 08:00 - Perfil Crediticio", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        StatusBadge("PROGRAMADA")
                    }

                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                    // Alert 3: Employment follow up
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("EMP-000004: OIM CONS 111 2026", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Seguimiento 07/09/2026 (Ref 22905)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        StatusBadge("POSTULADA")
                    }
                }
            }
        }

        // Quick Navigation Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onNavigateToCrm,
                    modifier = Modifier.weight(1f).testTag("quick_crm_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyDark)
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Ingreso Único", fontSize = 12.sp)
                }

                Button(
                    onClick = onNavigateTo360,
                    modifier = Modifier.weight(1f).testTag("quick_360_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = NavyDark, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Vista 360", color = NavyDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
