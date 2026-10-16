package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "persons")
data class PersonEntity(
    @PrimaryKey val id: String, // e.g. PER-000109
    val documentNumber: String,
    val documentType: String = "CC",
    val fullName: String,
    val email: String,
    val phone: String,
    val identityState: String = "VERIFICADA", // VERIFICADA, PARCIAL, PENDIENTE
    val notes: String = ""
)

@Entity(tableName = "clients")
data class ClientEntity(
    @PrimaryKey val id: String, // e.g. CLI-000109
    val personId: String,
    val fullName: String,
    val documentNumber: String,
    val phone: String,
    val email: String,
    val segment: String = "LEGACY_CRM", // LEGACY_CRM, COMERCIAL, VIP
    val state: String = "ACTIVO", // ACTIVO, PENDIENTE, ARCHIVADO
    val totalServicesValue: Double = 0.0,
    val lastContactDate: String = "",
    val notes: String = ""
)

@Entity(tableName = "prospects")
data class ProspectEntity(
    @PrimaryKey val id: String, // e.g. PROS-000001
    val personId: String,
    val fullName: String,
    val phone: String,
    val email: String,
    val stage: String = "NUEVO", // NUEVO, CONTACTADO, CARACTERIZADO, PAGO_VALIDADO, CONVERTIDO, PAUSADO
    val serviceRequested: String = "PERFIL_CREDITICIO",
    val estimatedValue: Double = 0.0,
    val probability: Int = 50,
    val appointmentDate: String = "",
    val createdAt: String = "",
    val tokenUsed: String = "NO",
    val notes: String = ""
)

@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey val id: String, // e.g. SER-000033
    val clientId: String,
    val catalogItemId: String = "",
    val catalogLine: String = "Historial crediticio y hábeas data",
    val serviceType: String, // SANEAMIENTO_HISTORIAL, DERECHO_PETICION, etc.
    val agreedValue: Double,
    val paidValue: Double = 0.0,
    val balance: Double = 0.0,
    val paymentMethod: String = "TRANSFERENCIA",
    val state: String = "ACTIVO", // ACTIVO, COMPLETADO, ARCHIVADO
    val createdAt: String = "",
    val notes: String = ""
)

@Entity(tableName = "catalog_items")
data class CatalogItemEntity(
    @PrimaryKey val id: String, // e.g. CAT-000001
    val line: String, // One of the 8 lines of service
    val name: String,
    val description: String,
    val responsible: String = "GESTION_JURIDICA",
    val referencePrice: Double = 0.0,
    val scope: String = "Alcance definido según contratación individual",
    val isActive: Boolean = true
)

@Entity(tableName = "payment_obligations")
data class PaymentObligationEntity(
    @PrimaryKey val id: String, // e.g. OBL-000001
    val serviceId: String,
    val clientId: String,
    val concept: String,
    val totalAmount: Double,
    val paidAmount: Double = 0.0,
    val balance: Double = 0.0,
    val dueDate: String,
    val status: String = "PENDIENTE", // PENDIENTE, PAGADA_PARCIAL, PAGADA_TOTAL
    val createdAt: String
)

@Entity(tableName = "payment_records")
data class PaymentRecordEntity(
    @PrimaryKey val id: String, // e.g. PAG-000001
    val obligationId: String,
    val serviceId: String,
    val clientId: String,
    val amount: Double,
    val paymentMethod: String = "TRANSFERENCIA", // EFECTIVO, BANCOLOMBIA, DAVIPLATA, TRANSFERENCIA
    val date: String,
    val receiptRef: String = "",
    val supportDocId: String = "",
    val supportType: String = "DOC",
    val verified: Boolean = true,
    val notes: String = ""
)

@Entity(tableName = "commercial_requests")
data class CommercialRequestEntity(
    @PrimaryKey val id: String, // e.g. SOL-000001
    val prospectId: String = "",
    val fullName: String,
    val documentNumber: String,
    val phone: String,
    val email: String,
    val catalogItemId: String,
    val catalogItemName: String,
    val catalogLine: String,
    val quotedAmount: Double,
    val stage: String = "COTIZACION", // COTIZACION, EN_NEGOCIACION, APROBADA, CONTRATADA, RECHAZADA
    val createdAt: String,
    val notes: String = ""
)

@Entity(tableName = "legal_cases")
data class LegalCaseEntity(
    @PrimaryKey val id: String, // e.g. CAS-000028
    val clientId: String,
    val serviceId: String,
    val matter: String, // DERECHO_PETICION, TUTELA, SUPLANTACION_IDENTIDAD, HABEAS_DATA
    val targetEntity: String, // e.g. Banco de Bogotá, Claro, Movistar, Tigo, FGA
    val radicado: String,
    val filingDate: String,
    val termType: String = "HABILES_15", // HABILES_15, HABILES_10, CALENDARIO_30
    val termDays: Int = 15,
    val dueDate: String,
    val termState: String = "EN_TERMINO", // EN_TERMINO, VENCE_HOY, VENCIDO, RESPONDIDO, SIN_FECHA
    val risk: String = "MEDIO", // BAJO, MEDIO, ALTO, CRITICO
    val nextAction: String = "",
    val nextActionDate: String = "",
    val state: String = "EN_PROCESO", // ABIERTO, EN_PROCESO, ARCHIVADO, CERRADO
    val notes: String = ""
)

@Entity(tableName = "legal_actions")
data class LegalActionEntity(
    @PrimaryKey val id: String, // e.g. JUR-000001
    val caseId: String,
    val actionType: String, // PETICION_INICIAL, RECURSO_REPOSICION, INSISTENCIA, TUTELA, RESPUESTA
    val date: String,
    val description: String,
    val radicadoOrToken: String = "",
    val responsible: String = "GESTION_JURIDICA"
)

@Entity(tableName = "financial_movements")
data class FinancialMovementEntity(
    @PrimaryKey val id: String, // e.g. FIN-000077
    val date: String,
    val type: String, // INGRESO, GASTO, VENTA_HISTORICA, TRANSFERENCIA_INTERNA
    val scope: String, // PERSONAL, PROSPECTIVA, PROYECTO, DEPENDIENTE
    val category: String, // SERVICIOS_PUBLICOS, ARRENDAMIENTO, TRANSPORTE, ALIMENTACION, HONORARIOS, FAMILIA, SEGURIDAD_SOCIAL
    val concept: String,
    val thirdParty: String,
    val amount: Double,
    val paymentMethod: String = "EFECTIVO", // EFECTIVO, BANCOLOMBIA, DAVIPLATA
    val supportType: String = "DOC", // DOC, CONSTANCIA_INTERNA, PENDIENTE_SOPORTE, SIN_SOPORTE_DECLARADO
    val supportDocId: String = "",
    val dueDate: String = "",
    val isReconciled: Boolean = false,
    val state: String = "CONFIRMADO", // CONFIRMADO, ARCHIVADO, PENDIENTE
    val notes: String = ""
)

@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey val id: String, // e.g. ACT-000009
    val title: String,
    val module: String, // CRM, JURIDICO, FINANZAS, EMPLEO, OPERACION, DOCENCIA
    val blockType: String, // FOCO, ADMINISTRATIVO, REUNION, SEGUIMIENTO, PERSONAL, VIAJE, DOCENCIA
    val date: String,
    val startTime: String = "",
    val endTime: String = "",
    val priority: String = "MEDIA", // BAJA, MEDIA, ALTA, CRITICA
    val responsible: String = "OPERACION_ADMINISTRATIVA",
    val termState: String = "PROGRAMADA", // PROGRAMADA, HOY, VENCIDA, COMPLETADA, CANCELADA
    val syncCalendar: Boolean = false,
    val calendarEventId: String = "",
    val relatedEntityId: String = "",
    val notes: String = ""
)

@Entity(tableName = "employment_processes")
data class EmploymentProcessEntity(
    @PrimaryKey val id: String, // e.g. EMP-000004
    val organization: String,
    val position: String,
    val stage: String = "IDENTIFICADA", // IDENTIFICADA, EN_PREPARACION, POSTULADA, ENTREVISTA, CERRADA
    val submissionDate: String = "",
    val deadline: String = "",
    val priority: String = "ALTA", // BAJA, MEDIA, ALTA, CRITICA
    val nextAction: String = "",
    val nextActionDate: String = "",
    val supportDocs: String = "",
    val notes: String = ""
)

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey val id: String, // e.g. DOC-000001
    val title: String,
    val docType: String, // CONTRATO, FACTURA, PETICION, HOJA_VIDA, SOPORTE_PAGO, EVIDENCIA_ACTA
    val relatedEntityId: String = "",
    val driveUrl: String = "",
    val date: String = "",
    val version: String = "V1",
    val hashSha256: String = ""
)

@Entity(tableName = "system_counters")
data class SystemCounterEntity(
    @PrimaryKey val prefix: String, // CLI, PROS, CAS, JUR, SER, FIN, ACT, DOC, EMP, PER, REG
    val lastNumber: Int
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true) val logId: Long = 0,
    val timestamp: String,
    val batchId: String,
    val action: String,
    val affectedId: String,
    val status: String,
    val details: String
)

@Entity(tableName = "trends")
data class TrendEntity(
    @PrimaryKey val id: String, // e.g. TND-000001
    val title: String,
    val category: String, // TECNOLOGICA, REGULATORIA, MERCADO, LABORAL
    val horizon: String, // CORTO_PLAZO, MEDIANO_PLAZO, LARGO_PLAZO
    val maturity: String, // SENAL_DEBIL, TENDENCIA_EMERGENTE, MEGATENDENCIA
    val impactScore: Int, // 1 to 5
    val probabilityScore: Int, // 1 to 5
    val description: String,
    val triggerFactor: String,
    val status: String = "ACTIVA" // ACTIVA, EN_MONITOREO, MATERIALIZADA
)

@Entity(tableName = "scenarios")
data class ScenarioEntity(
    @PrimaryKey val id: String, // e.g. SCN-000001
    val title: String,
    val scenarioType: String, // OPTIMISTA, BASE_TENDENCIAL, DESAFIANTE, DISRUPTIVO
    val probability: Int, // percentage 0-100
    val description: String,
    val keyTriggers: String,
    val actionPlan: String,
    val projectedGrowthPct: Int // e.g. +35% or -15%
)

@Entity(tableName = "roadmap_milestones")
data class RoadmapMilestoneEntity(
    @PrimaryKey val id: String, // e.g. MLS-000001
    val title: String,
    val quarter: String, // Q4 2026, Q1 2027, Q2 2027, etc.
    val targetDate: String,
    val impactLevel: String, // ALTO, MEDIO, CRITICO
    val uncertaintyLevel: String, // ALTA, MEDIA, BAJA
    val status: String = "PLANIFICADO", // PLANIFICADO, EN_CURSO, COMPLETADO, EN_RIESGO
    val associatedModule: String = "OPERACION",
    val strategicGoal: String
)

