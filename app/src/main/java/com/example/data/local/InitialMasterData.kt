package com.example.data.local

import com.example.data.local.entity.*

object InitialMasterData {

    fun getDefaultCounters(): List<SystemCounterEntity> = listOf(
        SystemCounterEntity("CLI", 127),
        SystemCounterEntity("PROS", 9),
        SystemCounterEntity("CAS", 42),
        SystemCounterEntity("JUR", 21),
        SystemCounterEntity("SER", 46),
        SystemCounterEntity("FIN", 136),
        SystemCounterEntity("ACT", 94),
        SystemCounterEntity("DOC", 106),
        SystemCounterEntity("EMP", 4),
        SystemCounterEntity("PER", 133),
        SystemCounterEntity("REG", 12),
        SystemCounterEntity("VIV", 1),
        SystemCounterEntity("OBL", 6),
        SystemCounterEntity("PAG", 6),
        SystemCounterEntity("CAT", 21),
        SystemCounterEntity("SOL", 3)
    )

    fun getDefaultPersons(): List<PersonEntity> = InitialClientsData.getInitialPersons()

    fun getDefaultClients(): List<ClientEntity> = InitialClientsData.getInitialClients()

    fun getDefaultProspects(): List<ProspectEntity> = listOf(
        ProspectEntity("PROS-000001", "PER-000122", "Gerley Angulo", "3238374358", "gerleyangulo3@gmail.com", "CARACTERIZADO", "PERFIL_CREDITICIO", 150000.0, 75, "2026-09-10 08:00", "2026-08-28", "NO", "Cita programada ACT-000009. Requiere documento físico."),
        ProspectEntity("PROS-000006", "PER-000127", "Laura Valentina Herrera Cardozo", "3169201847", "valentinahcardozo@gmail.com", "NUEVO", "SANEAMIENTO_HISTORIAL", 250000.0, 40, "", "2026-09-09", "NO", "Token REG-000012 enviado para validación.")
    )

    fun getDefaultServices(): List<ServiceEntity> = listOf(
        ServiceEntity(id = "SER-000003", clientId = "CLI-000061", serviceType = "DERECHO_PETICION", agreedValue = 350000.0, paidValue = 150000.0, balance = 200000.0, paymentMethod = "TRANSFERENCIA", state = "ARCHIVADO", createdAt = "2026-08-20"),
        ServiceEntity(id = "SER-000021", clientId = "CLI-000079", serviceType = "INFORME_JURIDICO", agreedValue = 100000.0, paidValue = 100000.0, balance = 0.0, paymentMethod = "TRANSFERENCIA", state = "COMPLETADO", createdAt = "2026-08-20"),
        ServiceEntity(id = "SER-000031", clientId = "CLI-000103", serviceType = "DERECHO_PETICION", agreedValue = 250000.0, paidValue = 250000.0, balance = 0.0, paymentMethod = "DAVIPLATA", state = "ACTIVO", createdAt = "2026-08-20"),
        ServiceEntity(id = "SER-000033", clientId = "CLI-000109", serviceType = "SANEAMIENTO_HISTORIAL", agreedValue = 350000.0, paidValue = 350000.0, balance = 0.0, paymentMethod = "TRANSFERENCIA", state = "ACTIVO", createdAt = "2026-08-20"),
        ServiceEntity(id = "SER-000034", clientId = "CLI-000110", serviceType = "DERECHO_PETICION", agreedValue = 300000.0, paidValue = 300000.0, balance = 0.0, paymentMethod = "BANCOLOMBIA", state = "ACTIVO", createdAt = "2026-08-20"),
        ServiceEntity(id = "SER-000035", clientId = "CLI-000121", serviceType = "SANEAMIENTO_HISTORIAL", agreedValue = 250000.0, paidValue = 250000.0, balance = 0.0, paymentMethod = "EFECTIVO", state = "ARCHIVADO", createdAt = "2026-08-21")
    )

    fun getDefaultLegalCases(): List<LegalCaseEntity> = listOf(
        LegalCaseEntity(
            id = "CAS-000024",
            clientId = "CLI-000069",
            serviceId = "SER-000031",
            matter = "HABEAS_DATA",
            targetEntity = "Gennials BPO",
            radicado = "BORRADOR_GMAIL",
            filingDate = "2026-08-25",
            termType = "HABILES_15",
            termDays = 15,
            dueDate = "2026-09-15",
            termState = "EN_TERMINO",
            risk = "ALTO",
            nextAction = "Revisión de borrador remitente y confirmación de envío",
            nextActionDate = "2026-08-31",
            state = "EN_PROCESO",
            notes = "Caso prioritario. Conserva borrador en Gmail marcado para revisión."
        ),
        LegalCaseEntity(
            id = "CAS-000028",
            clientId = "CLI-000109",
            serviceId = "SER-000033",
            matter = "DERECHO_PETICION",
            targetEntity = "Banco de Bogotá / FGA",
            radicado = "114984 / Req 18332963",
            filingDate = "2024-02-08",
            termType = "HABILES_15",
            termDays = 15,
            dueDate = "2024-03-01",
            termState = "RESPONDIDO",
            risk = "BAJO",
            nextAction = "Expediente consolidado con respuesta favorable",
            nextActionDate = "",
            state = "ARCHIVADO",
            notes = "Respuestas incorporadas: FGA 114984 y Banco Bogotá 18332963."
        ),
        LegalCaseEntity(
            id = "CAS-000026",
            clientId = "CLI-000103",
            serviceId = "SER-000031",
            matter = "DERECHO_PETICION",
            targetEntity = "Banco Unión",
            radicado = "PQR 990202",
            filingDate = "2024-02-12",
            termType = "HABILES_15",
            termDays = 15,
            dueDate = "2024-03-05",
            termState = "RESPONDIDO",
            risk = "MEDIO",
            nextAction = "Monitoreo de actualización en centrales de riesgo",
            nextActionDate = "",
            state = "ARCHIVADO",
            notes = "Atendido y verificado en ciclo de reconciliación."
        ),
        LegalCaseEntity(
            id = "CAS-000029",
            clientId = "CLI-000110",
            serviceId = "SER-000034",
            matter = "DERECHO_PETICION",
            targetEntity = "RCI-Mobilize",
            radicado = "Crédito 1004780449",
            filingDate = "2024-02-15",
            termType = "HABILES_15",
            termDays = 15,
            dueDate = "2024-03-08",
            termState = "RESPONDIDO",
            risk = "MEDIO",
            nextAction = "Trazabilidad cerrada",
            nextActionDate = "",
            state = "ARCHIVADO",
            notes = "6 hitos documentados y consolidados."
        ),
        LegalCaseEntity(
            id = "CAS-000030",
            clientId = "CLI-000121",
            serviceId = "SER-000035",
            matter = "DERECHO_PETICION",
            targetEntity = "CLARO Soluciones",
            radicado = "12025217918",
            filingDate = "2024-02-20",
            termType = "HABILES_15",
            termDays = 15,
            dueDate = "2024-03-12",
            termState = "RESPONDIDO",
            risk = "BAJO",
            nextAction = "Cierre formal de caso",
            nextActionDate = "",
            state = "ARCHIVADO",
            notes = "Alta histórica mediante Ingreso Único AUT-012."
        )
    )

    fun getDefaultLegalActions(): List<LegalActionEntity> = listOf(
        LegalActionEntity("JUR-000001", "CAS-000028", "PETICION_INICIAL", "2024-02-08", "Envío de derecho de petición saneamiento historial crediticio", "114984"),
        LegalActionEntity("JUR-000002", "CAS-000028", "RESPUESTA", "2024-02-15", "Recepción de respuesta y requerimiento Banco de Bogotá", "18332963"),
        LegalActionEntity("JUR-000003", "CAS-000028", "INSISTENCIA", "2024-03-04", "Escrito de aclaración e inconformidad final", "INC-01"),
        LegalActionEntity("JUR-000004", "CAS-000024", "PETICION_INICIAL", "2026-08-25", "Preparación de derecho de petición Habeas Data Gennials BPO", "DRAFT-01")
    )

    fun getDefaultFinancialMovements(): List<FinancialMovementEntity> = listOf(
        FinancialMovementEntity("FIN-000077", "2026-08-28", "GASTO", "PERSONAL", "SERVICIOS_PUBLICOS", "Factura Gas de Occidente Contrato 654929", "Gases de Occidente", 2235.0, "EFECTIVO", "DOC", "DOC-000010", "2026-09-01", true, "CONFIRMADO", "Vencimiento real 01/09/2026. Soportado."),
        FinancialMovementEntity("FIN-000084", "2026-08-31", "GASTO", "PERSONAL", "ARRENDAMIENTO", "Canon de arrendamiento mensual vivienda", "Alba Patricia Revelo", 250000.0, "TRANSFERENCIA", "CONSTANCIA_INTERNA", "", "2026-09-10", false, "PENDIENTE", "Fecha límite de control primeros 10 días."),
        FinancialMovementEntity("FIN-000085", "2026-08-31", "GASTO", "PROSPECTIVA", "SEGURIDAD_SOCIAL", "Aporte parafiscales y seguridad social independiente", "Seguridad Social ARL/EPS", 508000.0, "BANCOLOMBIA", "PENDIENTE_SOPORTE", "", "2026-09-10", false, "PENDIENTE", "Obligación de control primeros 10 días."),
        FinancialMovementEntity("FIN-000086", "2026-08-31", "GASTO", "PERSONAL", "SERVICIOS_PUBLICOS", "Factura Celsia Energía Eléctrica", "Celsia Energía", 111000.0, "DAVIPLATA", "PENDIENTE_SOPORTE", "", "2026-09-10", false, "PENDIENTE", "Pendiente adjuntar factura física."),
        FinancialMovementEntity("FIN-000087", "2026-08-31", "GASTO", "PERSONAL", "SERVICIOS_PUBLICOS", "Internet y telefonía fija", "Operador Telecom", 100000.0, "TRANSFERENCIA", "CONSTANCIA_INTERNA", "", "2026-09-10", false, "PENDIENTE", "Fecha límite control 10/09."),
        FinancialMovementEntity("FIN-000088", "2026-08-31", "GASTO", "PERSONAL", "SERVICIOS_PUBLICOS", "Telefonía móvil plan WOM", "WOM Colombia", 40000.0, "DAVIPLATA", "CONSTANCIA_INTERNA", "", "2026-09-10", false, "PENDIENTE", "Control 10 días."),
        FinancialMovementEntity("FIN-000089", "2026-08-31", "GASTO", "DEPENDIENTE", "FAMILIA", "Apoyo cuota familiar Sara", "Sara", 150000.0, "EFECTIVO", "CONSTANCIA_INTERNA", "", "2026-09-10", false, "PENDIENTE", "Obligación familiar mensual."),
        FinancialMovementEntity("FIN-000090", "2026-08-31", "GASTO", "DEPENDIENTE", "FAMILIA", "Apoyo cuota familiar Luisa", "Luisa", 150000.0, "EFECTIVO", "CONSTANCIA_INTERNA", "", "2026-09-10", false, "PENDIENTE", "Obligación familiar mensual."),
        FinancialMovementEntity("FIN-000091", "2026-08-31", "GASTO", "DEPENDIENTE", "FAMILIA", "Cuota sostenimiento Menelik", "Menelik", 600000.0, "TRANSFERENCIA", "CONSTANCIA_INTERNA", "", "2026-09-10", false, "PENDIENTE", "Obligación familiar."),
        FinancialMovementEntity("FIN-000092", "2026-08-31", "GASTO", "DEPENDIENTE", "FAMILIA", "Educación y útiles Menelik", "Colegio / Institución", 90000.0, "EFECTIVO", "CONSTANCIA_INTERNA", "", "2026-09-10", false, "PENDIENTE", "Gastos educativos.")
    )

    fun getDefaultActivities(): List<ActivityEntity> = listOf(
        ActivityEntity("ACT-000009", "Cita inicial y caracterización con Gerley Angulo", "CRM", "FOCO", "2026-09-10", "08:00", "08:30", "ALTA", "OPERACION_ADMINISTRATIVA", "PROGRAMADA", true, "gcal_evt_09", "PROS-000001", "Verificar documentos físicos en reunión."),
        ActivityEntity("ACT-000011", "Seguimiento postulación OIM CONS 111 2026", "EMPLEO", "SEGUIMIENTO", "2026-09-07", "09:00", "09:30", "CRITICA", "OPERACION_ADMINISTRATIVA", "PROGRAMADA", true, "7cpkfuj0nipii8gi8t2sr6u75s", "EMP-000004", "Monitoreo de estado en IOM e-Recruitment."),
        ActivityEntity("ACT-000012", "Sesión de inglés y desarrollo profesional continuo", "PERSONAL", "FOCO", "2026-08-30", "19:00", "20:00", "MEDIA", "DIRECCION", "COMPLETADA", true, "gcal_evt_12", "", "Sesión de recuperación ejecutada."),
        ActivityEntity("ACT-000016", "Revisión borrador CAS-000024 Gennials BPO", "JURIDICO", "ADMINISTRATIVO", "2026-08-31", "09:15", "09:45", "ALTA", "GESTION_JURIDICA", "HOY", true, "gcal_evt_16", "CAS-000024", "Confirmar remitente Gmail antes de envío definitivo."),
        ActivityEntity("ACT-000017", "Revisión posterior Fase 6 y monitoreo primer ciclo", "OPERACION", "SEGUIMIENTO", "2026-09-01", "10:00", "11:00", "BAJA", "OPERACION_ADMINISTRATIVA", "PROGRAMADA", false, "", "", "SM6 v1.1.0 Escritura controlada.")
    )

    fun getDefaultEmploymentProcesses(): List<EmploymentProcessEntity> = listOf(
        EmploymentProcessEntity(
            id = "EMP-000004",
            organization = "OIM - ONU Migración",
            position = "CONS 111 2026 - Generación de Ingresos (Cali)",
            stage = "POSTULADA",
            submissionDate = "2026-08-29",
            deadline = "2026-08-31",
            priority = "CRITICA",
            nextAction = "Revisión de avances del proceso de selección",
            nextActionDate = "2026-09-07",
            supportDocs = "DOC-000007 (HV adaptada), DOC-000008 (Constancia OIM Ref 22905)",
            notes = "Postulación confirmada por correo desde iomwave-do-not-reply@iom.int."
        ),
        EmploymentProcessEntity(
            id = "EMP-000002",
            organization = "VPC Alianzas",
            position = "Coordinador de Proyectos Regionales",
            stage = "IDENTIFICADA",
            submissionDate = "",
            deadline = "2026-09-15",
            priority = "ALTA",
            nextAction = "Evaluación de requisitos de experiencia y carta de motivación",
            nextActionDate = "2026-08-30",
            supportDocs = "",
            notes = "En etapa de revisión y ajuste documental."
        ),
        EmploymentProcessEntity(
            id = "EMP-000003",
            organization = "Grupo Alcomex",
            position = "Asesor de Estrategia Operativa",
            stage = "IDENTIFICADA",
            submissionDate = "",
            deadline = "2026-09-20",
            priority = "MEDIA",
            nextAction = "Revisión de términos de referencia",
            nextActionDate = "2026-08-30",
            supportDocs = "",
            notes = "Vacante identificada pendiente de confirmación."
        ),
        EmploymentProcessEntity(
            id = "EMP-000001",
            organization = "Ayuda en Acción",
            position = "Especialista de Monitoreo",
            stage = "CERRADA",
            submissionDate = "2026-08-20",
            deadline = "2026-08-25",
            priority = "BAJA",
            nextAction = "Proceso concluido",
            nextActionDate = "",
            supportDocs = "",
            notes = "Vacante no disponible por vencimiento de convocatoria."
        )
    )

    fun getDefaultDocuments(): List<DocumentEntity> = listOf(
        DocumentEntity("DOC-000001", "Base Maestra Histórica Recuperada Prospectiva 0.4", "CONTRATO", "PRY-000001", "drive://prospectiva/docs/base_2024.xlsx", "2026-08-20", "V1"),
        DocumentEntity("DOC-000006", "Soporte Alojamiento y Reunión Gerente RAP Pacífico", "EVIDENCIA_ACTA", "ACT-000001", "drive://prospectiva/docs/e1_3_01_rap.pdf", "2026-08-20", "V1"),
        DocumentEntity("DOC-000007", "Hoja de Vida adaptada Convocatoria OIM CONS 111 2026", "HOJA_VIDA", "EMP-000004", "drive://prospectiva/empleo/hv_oim_cons111_v2.pdf", "2026-08-29", "V2"),
        DocumentEntity("DOC-000008", "Constancia de Recepción de Postulación OIM Ref 22905", "EVIDENCIA_ACTA", "EMP-000004", "drive://prospectiva/empleo/constancia_oim_22905.pdf", "2026-08-29", "V1"),
        DocumentEntity("DOC-000010", "Factura Gases de Occidente Contrato 654929 Pagada", "FACTURA", "FIN-000077", "drive://prospectiva/finanzas/factura_gdo_654929.pdf", "2026-08-28", "V1", "sha256:7f60401d2f90178ac880a118ca2f1f9d")
    )

    fun getDefaultTrends(): List<TrendEntity> = listOf(
        TrendEntity(
            id = "TND-000001",
            title = "Automatización Inteligente de Trámites y Radicados Jurídicos",
            category = "TECNOLOGICA",
            horizon = "CORTO_PLAZO",
            maturity = "MEGATENDENCIA",
            impactScore = 5,
            probabilityScore = 5,
            description = "Estandarización y agilización de respuestas a derechos de petición y tutelas mediante modelos de procesamiento de lenguaje natural.",
            triggerFactor = "Adopción de juzgados digitales y masificación de canales de atención virtual en Colombia.",
            status = "ACTIVA"
        ),
        TrendEntity(
            id = "TND-000002",
            title = "Bre-B y Nuevo Sistema de Pagos Inmediatos Interoperables",
            category = "MERCADO",
            horizon = "CORTO_PLAZO",
            maturity = "MEGATENDENCIA",
            impactScore = 4,
            probabilityScore = 5,
            description = "Unificación de llaves, QR y transferencias inmediatas bajo el Banco de la República, reduciendo fricción de cobro y mora de cartera.",
            triggerFactor = "Lanzamiento y obligatoriedad gradual de Bre-B en el sistema financiero colombiano.",
            status = "ACTIVA"
        ),
        TrendEntity(
            id = "TND-000003",
            title = "Supervisión Estricta de la SIC en Habeas Data y Suplantaciones",
            category = "REGULATORIA",
            horizon = "MEDIANO_PLAZO",
            maturity = "TENDENCIA_EMERGENTE",
            impactScore = 5,
            probabilityScore = 4,
            description = "Intensificación de quejas por indebida permanencia de reportes negativos y fraudes de identidad en telecomunicaciones y crédito.",
            triggerFactor = "Circulares regulatorias y sentencias de unificación de la Corte Constitucional.",
            status = "ACTIVA"
        ),
        TrendEntity(
            id = "TND-000004",
            title = "Financiamiento Multilateral en Empleabilidad e Inclusión Productiva",
            category = "LABORAL",
            horizon = "CORTO_PLAZO",
            maturity = "TENDENCIA_EMERGENTE",
            impactScore = 4,
            probabilityScore = 4,
            description = "Organismos internacionales (OIM, USAID, agencias ONU) canalizando recursos hacia consultorías de caracterización y empleo en el Valle y Pacífico.",
            triggerFactor = "Llamados a interés competitivos para consultorías especializadas.",
            status = "ACTIVA"
        ),
        TrendEntity(
            id = "TND-000005",
            title = "Validación Biométrica y Cédula Digital Interoperable",
            category = "TECNOLOGICA",
            horizon = "LARGO_PLAZO",
            maturity = "SENAL_DEBIL",
            impactScore = 4,
            probabilityScore = 3,
            description = "Sustitución progresiva de documentos fotocopiados por verificación criptográfica directa con Registraduría.",
            triggerFactor = "Plataformas públicas de autenticación ciudadana de segunda generación.",
            status = "EN_MONITOREO"
        )
    )

    fun getDefaultScenarios(): List<ScenarioEntity> = listOf(
        ScenarioEntity(
            id = "SCN-000001",
            title = "Escenario 1: Crecimiento Digital & Eficiencia Exponencial",
            scenarioType = "OPTIMISTA",
            probability = 40,
            description = "Adopción plena de automatización de expedientes y pagos inmediatos. Capacidad operativa se duplica sin elevar costos fijos.",
            keyTriggers = "Adjudicación de consultoría OIM CONS 111, cobro puntual de RAP Pacífico y flujo comercial activo.",
            actionPlan = "Escalar captación comercial con Ingreso Único y formalizar alianzas de cooperación regional.",
            projectedGrowthPct = 42
        ),
        ScenarioEntity(
            id = "SCN-000002",
            title = "Escenario 2: Consolidación Regional Sostenible (Base)",
            scenarioType = "BASE_TENDENCIAL",
            probability = 45,
            description = "Crecimiento proyectado del 15% al 20%. Cobranza regular, rotación de cartera estable y cumplimiento riguroso de compromisos financieros.",
            keyTriggers = "Flujo mensual de peticiones y contratos de consultoría estables en el suroccidente.",
            actionPlan = "Mantener control de los primeros 10 días de cada mes y monitorear términos legales con cero vencimientos.",
            projectedGrowthPct = 18
        ),
        ScenarioEntity(
            id = "SCN-000003",
            title = "Escenario 3: Retrasos Institucionales y Restricción de Caja",
            scenarioType = "DESAFIANTE",
            probability = 15,
            description = "Demora prolongada en desembolsos institucionales (>60 días) que tensiona la liquidez inmediata.",
            keyTriggers = "Trabas burocráticas en contrapartes o aplazamiento de cuentas de cobro de convenios.",
            actionPlan = "Activar fondo de reserva contingente, acelerar recaudo de servicios individuales y priorizar obligaciones críticas.",
            projectedGrowthPct = -8
        )
    )

    fun getDefaultRoadmapMilestones(): List<RoadmapMilestoneEntity> = listOf(
        RoadmapMilestoneEntity(
            id = "MLS-000001",
            title = "Consolidación de Arquitectura y Auditoría QA 100/100",
            quarter = "Q3 2026",
            targetDate = "2026-08-31",
            impactLevel = "CRITICO",
            uncertaintyLevel = "BAJA",
            status = "COMPLETADO",
            associatedModule = "OPERACION",
            strategicGoal = "Estabilizar el Libro Núcleo con cero fallas de fórmulas e integridad de identificadores."
        ),
        RoadmapMilestoneEntity(
            id = "MLS-000002",
            title = "Confirmación y Puesta en Marcha Convocatoria OIM CONS 111",
            quarter = "Q3 2026",
            targetDate = "2026-09-07",
            impactLevel = "ALTO",
            uncertaintyLevel = "MEDIA",
            status = "EN_CURSO",
            associatedModule = "EMPLEO",
            strategicGoal = "Seguimiento formal a postulación y vinculación técnica en generación de ingresos."
        ),
        RoadmapMilestoneEntity(
            id = "MLS-000003",
            title = "Integración Operativa de Pasarela de Pagos Inmediatos Bre-B",
            quarter = "Q4 2026",
            targetDate = "2026-11-15",
            impactLevel = "ALTO",
            uncertaintyLevel = "MEDIA",
            status = "PLANIFICADO",
            associatedModule = "FINANZAS",
            strategicGoal = "Recaudo automático de abonos y honorarios con conciliación inmediata en caja."
        ),
        RoadmapMilestoneEntity(
            id = "MLS-000004",
            title = "Módulo Autónomo de Seguimiento Ciudadano a Peticiones",
            quarter = "Q1 2027",
            targetDate = "2027-02-28",
            impactLevel = "CRITICO",
            uncertaintyLevel = "ALTA",
            status = "PLANIFICADO",
            associatedModule = "CRM",
            strategicGoal = "Consulta de autos y respuestas en tiempo real para clientes de Prospectiva."
        )
    )

    fun getDefaultCatalogItems(): List<CatalogItemEntity> = listOf(
        // Línea 1: Diagnóstico crediticio
        CatalogItemEntity("CAT-000001", "Diagnóstico crediticio", "Consulta del perfil crediticio", "Consulta y reporte consolidado de centrales de riesgo DataCrédito y TransUnion.", "GESTION_CLIENTES", 50000.0),
        CatalogItemEntity("CAT-000002", "Diagnóstico crediticio", "Informe y análisis del perfil crediticio", "Análisis diagnóstico integral de capacidad de endeudamiento y estado de score.", "GESTION_CLIENTES", 100000.0),

        // Línea 2: Historial crediticio y hábeas data
        CatalogItemEntity("CAT-000003", "Historial crediticio y hábeas data", "Revisión de reportes negativos", "Auditoría de veracidad y legalidad de reportes financieros y comerciales.", "GESTION_JURIDICA", 120000.0),
        CatalogItemEntity("CAT-000004", "Historial crediticio y hábeas data", "Solicitudes de rectificación y actualización", "Gestión de actualización y eliminación de reportes caducos o inexactos.", "GESTION_JURIDICA", 250000.0),
        CatalogItemEntity("CAT-000005", "Historial crediticio y hábeas data", "Gestión de protección de datos personales", "Trámite especializado de protección constitucional de hábeas data ante operadores y fuentes.", "GESTION_JURIDICA", 200000.0),

        // Línea 3: Peticiones y reclamaciones
        CatalogItemEntity("CAT-000006", "Peticiones y reclamaciones", "Derechos de petición", "Redacción, radicación y seguimiento de derecho de petición constitucional.", "GESTION_JURIDICA", 150000.0),
        CatalogItemEntity("CAT-000007", "Peticiones y reclamaciones", "Oficios y comunicaciones formales", "Elaboración de oficios de solicitud o alcance ante entidades públicas o privadas.", "GESTION_JURIDICA", 100000.0),
        CatalogItemEntity("CAT-000008", "Peticiones y reclamaciones", "PQR ante entidades financieras y otras organizaciones", "Presentación de quejas formales por irregularidades en cobros o servicios.", "GESTION_JURIDICA", 180000.0),
        CatalogItemEntity("CAT-000009", "Peticiones y reclamaciones", "Seguimiento de respuestas", "Monitoreo procesal, control de términos legales e impugnaciones.", "GESTION_JURIDICA", 80000.0),

        // Línea 4: Gestión de obligaciones financieras
        CatalogItemEntity("CAT-000010", "Gestión de obligaciones financieras", "Revisión y aclaración de obligaciones", "Verificación de saldos, liquidación de intereses y estados de cuenta.", "OPERACION_ADMINISTRATIVA", 200000.0),
        CatalogItemEntity("CAT-000011", "Gestión de obligaciones financieras", "Solicitudes de pago total y paz y salvo", "Acompañamiento en acuerdos de pago y expedición de finiquitos.", "OPERACION_ADMINISTRATIVA", 150000.0),
        CatalogItemEntity("CAT-000012", "Gestión de obligaciones financieras", "Reclamaciones relacionadas con créditos", "Controversias por cobros no pactados, cláusulas abusivas o seguros vinculados.", "GESTION_JURIDICA", 250000.0),

        // Línea 5: Gestión jurídica de demandas
        CatalogItemEntity("CAT-000013", "Gestión jurídica de demandas", "Análisis de demandas y procesos ejecutivos", "Estudio de títulos valores, excepciones de mérito y estado procesal.", "GESTION_JURIDICA", 300000.0),
        CatalogItemEntity("CAT-000014", "Gestión jurídica de demandas", "Acompañamiento jurídico según alcance", "Defensa y representación extrajudicial o judicial según contrato de servicios.", "GESTION_JURIDICA", 500000.0),

        // Línea 6: Tránsito
        CatalogItemEntity("CAT-000015", "Tránsito", "Diagnóstico de multas y comparendos", "Revisión en SIMIT y secretarías de movilidad de prescripción y caducidad.", "GESTION_JURIDICA", 80000.0),
        CatalogItemEntity("CAT-000016", "Tránsito", "Peticiones y revisión de notificaciones", "Reclamación por indebida notificación de fotodetecciones o comparendos.", "GESTION_JURIDICA", 150000.0),
        CatalogItemEntity("CAT-000017", "Tránsito", "Revocatoria directa de fotomultas", "Recurso extraordinario de revocatoria por vulneración al debido proceso.", "GESTION_JURIDICA", 350000.0),
        CatalogItemEntity("CAT-000018", "Tránsito", "Gestiones relacionadas con cobro coactivo", "Defensa y levantamiento de medidas cautelares o embargos por tránsito.", "GESTION_JURIDICA", 400000.0),

        // Línea 7: Seguridad social
        CatalogItemEntity("CAT-000019", "Seguridad social", "Aclaración y conciliación de aportes", "Conciliación de semanas cotizadas y cobro de inconsistencias ante EPS y fondos.", "OPERACION_ADMINISTRATIVA", 300000.0),

        // Línea 8: Consultoría y acompañamiento integral
        CatalogItemEntity("CAT-000020", "Consultoría y acompañamiento integral", "Orientación financiera, laboral y académica", "Asesoría estratégica personalizada de saneamiento, empleo y proyectos.", "DIRECCION", 200000.0),
        CatalogItemEntity("CAT-000021", "Consultoría y acompañamiento integral", "Acompañamiento en transición migratoria", "Asistencia para regularización e inserción productiva de migrantes y retornados.", "PROYECTOS", 350000.0)
    )

    fun getDefaultPaymentObligations(): List<PaymentObligationEntity> = listOf(
        PaymentObligationEntity("OBL-000001", "SER-000003", "CLI-000061", "Contrato Derecho de Petición Martha Lucia Cuartas", 350000.0, 150000.0, 200000.0, "2026-08-30", "PAGADA_PARCIAL", "2026-08-20"),
        PaymentObligationEntity("OBL-000002", "SER-000021", "CLI-000079", "Informe y análisis jurídico José Francisco Riascos", 100000.0, 100000.0, 0.0, "2026-08-20", "PAGADA_TOTAL", "2026-08-20"),
        PaymentObligationEntity("OBL-000003", "SER-000031", "CLI-000103", "Trámite Derecho de Petición Yiver Mosquera", 250000.0, 250000.0, 0.0, "2026-08-20", "PAGADA_TOTAL", "2026-08-20"),
        PaymentObligationEntity("OBL-000004", "SER-000033", "CLI-000109", "Saneamiento Historial Crediticio Jhon Henry Hinestroza", 350000.0, 350000.0, 0.0, "2026-08-20", "PAGADA_TOTAL", "2026-08-20"),
        PaymentObligationEntity("OBL-000005", "SER-000034", "CLI-000110", "Gestión Hábeas Data Diana Lorena Peña", 300000.0, 300000.0, 0.0, "2026-08-20", "PAGADA_TOTAL", "2026-08-20"),
        PaymentObligationEntity("OBL-000006", "SER-000035", "CLI-000121", "Saneamiento Historial Crediticio Marlyn Vanin Vergara", 250000.0, 250000.0, 0.0, "2026-08-21", "PAGADA_TOTAL", "2026-08-21")
    )

    fun getDefaultPaymentRecords(): List<PaymentRecordEntity> = listOf(
        PaymentRecordEntity("PAG-000001", "OBL-000001", "SER-000003", "CLI-000061", 150000.0, "TRANSFERENCIA", "2026-08-20", "REC-001", "DOC-000001", "DOC", true, "Abono inicial 50% confirmado"),
        PaymentRecordEntity("PAG-000002", "OBL-000002", "SER-000021", "CLI-000079", 100000.0, "TRANSFERENCIA", "2026-08-20", "REC-002", "DOC-000002", "DOC", true, "Pago total de informe FV 06"),
        PaymentRecordEntity("PAG-000003", "OBL-000003", "SER-000031", "CLI-000103", 250000.0, "DAVIPLATA", "2026-08-20", "REC-003", "DOC-000002", "DOC", true, "Pago confirmado trámite Banco Unión"),
        PaymentRecordEntity("PAG-000004", "OBL-000004", "SER-000033", "CLI-000109", 350000.0, "TRANSFERENCIA", "2026-08-20", "REC-004", "DOC-000002", "DOC", true, "Pago total expediente Banco Bogotá"),
        PaymentRecordEntity("PAG-000005", "OBL-000005", "SER-000034", "CLI-000110", 300000.0, "BANCOLOMBIA", "2026-08-20", "REC-005", "DOC-000002", "DOC", true, "Pago confirmado trámite RCI-Mobilize"),
        PaymentRecordEntity("PAG-000006", "OBL-000006", "SER-000035", "CLI-000121", 250000.0, "EFECTIVO", "2026-08-21", "REC-006", "DOC-000002", "DOC", true, "Pago total saneamiento Claro")
    )

    fun getDefaultCommercialRequests(): List<CommercialRequestEntity> = listOf(
        CommercialRequestEntity("SOL-000001", "PROS-000001", "Gerley Angulo", "PENDIENTE", "3238374358", "gerleyangulo3@gmail.com", "CAT-000002", "Informe y análisis del perfil crediticio", "Diagnóstico crediticio", 100000.0, "COTIZACION", "2026-08-28", "Cita programada ACT-000009 para entrega de soportes"),
        CommercialRequestEntity("SOL-000002", "PROS-000006", "Laura Valentina Herrera Cardozo", "PENDIENTE", "3169201847", "valentinahcardozo@gmail.com", "CAT-000004", "Solicitudes de rectificación y actualización", "Historial crediticio y hábeas data", 250000.0, "EN_NEGOCIACION", "2026-09-09", "En validación de token REG-000012"),
        CommercialRequestEntity("SOL-000003", "", "Carlos Arturo Rengifo", "14892019", "3154829102", "carlosrengifo@gmail.com", "CAT-000017", "Revocatoria directa de fotomultas", "Tránsito", 350000.0, "APROBADA", "2026-08-30", "Listo para firma de contrato y creación de CLI")
    )
}

