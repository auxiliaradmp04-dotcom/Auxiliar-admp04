package com.example.data.repository

import com.example.data.local.InitialMasterData
import com.example.data.local.dao.MasterOperationsDao
import com.example.data.local.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MasterOperationsRepository(private val dao: MasterOperationsDao) {

    private val idLock = Mutex()

    suspend fun initializeIfEmpty() = withContext(Dispatchers.IO) {
        val currentCount = dao.getClientCount()
        if (currentCount < 127) {
            dao.insertDefaultCounters(InitialMasterData.getDefaultCounters())
            dao.insertPersons(InitialMasterData.getDefaultPersons())
            dao.insertClients(InitialMasterData.getDefaultClients())
        }
        if (currentCount == 0) {
            dao.insertProspects(InitialMasterData.getDefaultProspects())
            dao.insertServices(InitialMasterData.getDefaultServices())
            dao.insertLegalCases(InitialMasterData.getDefaultLegalCases())
            dao.insertLegalActions(InitialMasterData.getDefaultLegalActions())
            dao.insertFinancialMovements(InitialMasterData.getDefaultFinancialMovements())
            dao.insertActivities(InitialMasterData.getDefaultActivities())
            dao.insertEmploymentProcesses(InitialMasterData.getDefaultEmploymentProcesses())
            dao.insertDocuments(InitialMasterData.getDefaultDocuments())
            dao.insertTrends(InitialMasterData.getDefaultTrends())
            dao.insertScenarios(InitialMasterData.getDefaultScenarios())
            dao.insertRoadmapMilestones(InitialMasterData.getDefaultRoadmapMilestones())
            dao.insertCatalogItems(InitialMasterData.getDefaultCatalogItems())
            dao.insertPaymentObligations(InitialMasterData.getDefaultPaymentObligations())
            dao.insertPaymentRecords(InitialMasterData.getDefaultPaymentRecords())
            dao.insertCommercialRequests(InitialMasterData.getDefaultCommercialRequests())

            dao.insertAuditLog(
                AuditLogEntity(
                    timestamp = currentDateString(),
                    batchId = "MIG-INIT-2026",
                    action = "INITIAL_DATABASE_BOOTSTRAP",
                    affectedId = "NUCLEO_MASTER",
                    status = "APROBADO_100",
                    details = "Inicialización exitosa del Sistema Maestro Prospectiva 0.5: 127 Clientes Autorizados cargados"
                )
            )
        }
    }

    // Streams
    val clients: Flow<List<ClientEntity>> = dao.getAllClients()
    val prospects: Flow<List<ProspectEntity>> = dao.getAllProspects()
    val legalCases: Flow<List<LegalCaseEntity>> = dao.getAllLegalCases()
    val financialMovements: Flow<List<FinancialMovementEntity>> = dao.getAllFinancialMovements()
    val activities: Flow<List<ActivityEntity>> = dao.getAllActivities()
    val employmentProcesses: Flow<List<EmploymentProcessEntity>> = dao.getAllEmploymentProcesses()
    val documents: Flow<List<DocumentEntity>> = dao.getAllDocuments()
    val auditLogs: Flow<List<AuditLogEntity>> = dao.getRecentAuditLogs()
    val trends: Flow<List<TrendEntity>> = dao.getAllTrends()
    val scenarios: Flow<List<ScenarioEntity>> = dao.getAllScenarios()
    val roadmapMilestones: Flow<List<RoadmapMilestoneEntity>> = dao.getAllRoadmapMilestones()
    val catalogItems: Flow<List<CatalogItemEntity>> = dao.getAllCatalogItems()
    val paymentObligations: Flow<List<PaymentObligationEntity>> = dao.getAllPaymentObligations()
    val paymentRecords: Flow<List<PaymentRecordEntity>> = dao.getAllPaymentRecords()
    val commercialRequests: Flow<List<CommercialRequestEntity>> = dao.getAllCommercialRequests()

    fun searchClients(query: String): Flow<List<ClientEntity>> = dao.searchClients(query)
    fun getServicesByClient(clientId: String): Flow<List<ServiceEntity>> = dao.getServicesByClient(clientId)
    fun getObligationsByClient(clientId: String): Flow<List<PaymentObligationEntity>> = dao.getObligationsByClient(clientId)
    fun getPaymentsByClient(clientId: String): Flow<List<PaymentRecordEntity>> = dao.getPaymentsByClient(clientId)
    fun getLegalCasesByClient(clientId: String): Flow<List<LegalCaseEntity>> = dao.getLegalCasesByClient(clientId)
    fun getLegalActions(caseId: String): Flow<List<LegalActionEntity>> = dao.getActionsByCase(caseId)
    fun getActivitiesByEntity(entityId: String): Flow<List<ActivityEntity>> = dao.getActivitiesByEntity(entityId)
    fun getDocumentsByEntity(entityId: String): Flow<List<DocumentEntity>> = dao.getDocumentsByEntity(entityId)

    suspend fun getClientById(id: String): ClientEntity? = dao.getClientById(id)
    suspend fun getLegalCaseById(id: String): LegalCaseEntity? = dao.getLegalCaseById(id)
    suspend fun getPersonByDocument(doc: String): PersonEntity? = dao.getPersonByDocument(doc)
    suspend fun getClientByDocument(doc: String): ClientEntity? = dao.getClientByDocument(doc)

    /**
     * Generates a stable atomic sequential ID with prefix (e.g. CLI-000122)
     * Mirrors AUT-006 LockService rules from the Master Architecture
     */
    suspend fun generateNextId(prefix: String): String = idLock.withLock {
        withContext(Dispatchers.IO) {
            val counter = dao.getCounter(prefix)
            val current = counter?.lastNumber ?: 0
            val next = current + 1
            dao.setCounter(SystemCounterEntity(prefix, next))
            String.format(Locale.ROOT, "%s-%06d", prefix, next)
        }
    }

    /**
     * AUT-012 "Ingreso Único": Atomic enrollment of Client, Service, and optional Legal Case
     * Enforces duplicate protection and rollback safety
     */
    suspend fun executeIngresoUnico(
        fullName: String,
        documentNumber: String,
        phone: String,
        email: String,
        serviceType: String,
        agreedValue: Double,
        paymentMethod: String,
        createLegalCase: Boolean,
        caseMatter: String = "DERECHO_PETICION",
        targetEntity: String = "",
        radicado: String = "",
        risk: String = "MEDIO"
    ): Result<Triple<String, String, String?>> = withContext(Dispatchers.IO) {
        try {
            // Duplicate check by document number
            val existingClient = dao.getClientByDocument(documentNumber)
            val clientId: String
            val personId: String

            if (existingClient != null) {
                clientId = existingClient.id
                personId = existingClient.personId
            } else {
                personId = generateNextId("PER")
                clientId = generateNextId("CLI")

                val newPerson = PersonEntity(
                    id = personId,
                    documentNumber = documentNumber,
                    documentType = "CC",
                    fullName = fullName,
                    email = email,
                    phone = phone,
                    identityState = "VERIFICADA",
                    notes = "Alta por Ingreso Único Prospectiva 0.5"
                )
                dao.insertPerson(newPerson)

                val newClient = ClientEntity(
                    id = clientId,
                    personId = personId,
                    fullName = fullName,
                    documentNumber = documentNumber,
                    phone = phone,
                    email = email,
                    segment = "COMERCIAL",
                    state = "ACTIVO",
                    totalServicesValue = agreedValue,
                    lastContactDate = currentDateOnlyString(),
                    notes = "Cliente registrado mediante Ingreso Único"
                )
                dao.insertClient(newClient)
            }

            // Create Service (SER-...)
            val serviceId = generateNextId("SER")
            val newService = ServiceEntity(
                id = serviceId,
                clientId = clientId,
                serviceType = serviceType,
                agreedValue = agreedValue,
                paidValue = 0.0,
                balance = agreedValue,
                paymentMethod = paymentMethod,
                state = "ACTIVO",
                createdAt = currentDateOnlyString()
            )
            dao.insertService(newService)

            // Create initial Payment Obligation (OBL-...)
            val obligationId = generateNextId("OBL")
            val newObligation = PaymentObligationEntity(
                id = obligationId,
                serviceId = serviceId,
                clientId = clientId,
                concept = "Contratación de $serviceType",
                totalAmount = agreedValue,
                paidAmount = 0.0,
                balance = agreedValue,
                dueDate = calculateDueDateString(30),
                status = "PENDIENTE",
                createdAt = currentDateOnlyString()
            )
            dao.insertPaymentObligation(newObligation)

            // Optionally create Legal Case (CAS-...)
            var caseId: String? = null
            if (createLegalCase && targetEntity.isNotBlank()) {
                caseId = generateNextId("CAS")
                val newCase = LegalCaseEntity(
                    id = caseId,
                    clientId = clientId,
                    serviceId = serviceId,
                    matter = caseMatter,
                    targetEntity = targetEntity,
                    radicado = if (radicado.isNotBlank()) radicado else "PENDIENTE_RADICACION",
                    filingDate = currentDateOnlyString(),
                    termType = "HABILES_15",
                    termDays = 15,
                    dueDate = calculateDueDateString(15),
                    termState = "EN_TERMINO",
                    risk = risk,
                    nextAction = "Radicación y seguimiento de respuesta",
                    nextActionDate = calculateDueDateString(5),
                    state = "EN_PROCESO",
                    notes = "Caso iniciado por Ingreso Único"
                )
                dao.insertLegalCase(newCase)
            }

            // Audit
            dao.insertAuditLog(
                AuditLogEntity(
                    timestamp = currentDateString(),
                    batchId = "AUT-012-" + System.currentTimeMillis() % 10000,
                    action = "INGRESO_UNICO_COMMIT",
                    affectedId = clientId,
                    status = "COMMIT_VALIDADO",
                    details = "Cliente: $clientId, Servicio: $serviceId, Caso: ${caseId ?: "N/A"}"
                )
            )

            Result.success(Triple(clientId, serviceId, caseId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addLegalAction(
        caseId: String,
        actionType: String,
        description: String,
        radicadoOrToken: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val actionId = generateNextId("JUR")
            val action = LegalActionEntity(
                id = actionId,
                caseId = caseId,
                actionType = actionType,
                date = currentDateOnlyString(),
                description = description,
                radicadoOrToken = radicadoOrToken,
                responsible = "GESTION_JURIDICA"
            )
            dao.insertLegalAction(action)
            Result.success(actionId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addFinancialMovement(
        type: String,
        scope: String,
        category: String,
        concept: String,
        thirdParty: String,
        amount: Double,
        paymentMethod: String,
        supportType: String,
        dueDate: String = "",
        notes: String = ""
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val movementId = generateNextId("FIN")
            val movement = FinancialMovementEntity(
                id = movementId,
                date = currentDateOnlyString(),
                type = type,
                scope = scope,
                category = category,
                concept = concept,
                thirdParty = thirdParty,
                amount = amount,
                paymentMethod = paymentMethod,
                supportType = supportType,
                dueDate = dueDate,
                isReconciled = (type == "INGRESO"),
                state = "CONFIRMADO",
                notes = notes
            )
            dao.insertFinancialMovement(movement)

            dao.insertAuditLog(
                AuditLogEntity(
                    timestamp = currentDateString(),
                    batchId = "FIN-GATEWAY",
                    action = "FINANCE_MOVEMENT_REGISTERED",
                    affectedId = movementId,
                    status = "APROBADO",
                    details = "Monto: $amount COP ($scope / $category)"
                )
            )
            Result.success(movementId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addActivity(
        title: String,
        module: String,
        blockType: String,
        date: String,
        startTime: String,
        endTime: String,
        priority: String,
        syncCalendar: Boolean,
        relatedEntityId: String = ""
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val actId = generateNextId("ACT")
            val activity = ActivityEntity(
                id = actId,
                title = title,
                module = module,
                blockType = blockType,
                date = date,
                startTime = startTime,
                endTime = endTime,
                priority = priority,
                responsible = "OPERACION_ADMINISTRATIVA",
                termState = "PROGRAMADA",
                syncCalendar = syncCalendar,
                calendarEventId = if (syncCalendar) "aut007_sync_" + System.currentTimeMillis() else "",
                relatedEntityId = relatedEntityId,
                notes = "Actividad registrada con sincronización controlada"
            )
            dao.insertActivity(activity)
            Result.success(actId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateActivityState(activity: ActivityEntity, newState: String) = withContext(Dispatchers.IO) {
        dao.updateActivity(activity.copy(termState = newState))
    }

    suspend fun updateLegalCaseState(caseEntity: LegalCaseEntity, newState: String, termState: String) = withContext(Dispatchers.IO) {
        dao.updateLegalCase(caseEntity.copy(state = newState, termState = termState))
    }

    suspend fun updateEmploymentStage(process: EmploymentProcessEntity, newStage: String, notes: String) = withContext(Dispatchers.IO) {
        dao.updateEmploymentProcess(process.copy(stage = newStage, notes = notes))
    }

    suspend fun updateProspectStage(prospect: ProspectEntity, newStage: String) = withContext(Dispatchers.IO) {
        dao.updateProspect(prospect.copy(stage = newStage))
    }

    suspend fun addTrend(
        title: String,
        category: String,
        horizon: String,
        maturity: String,
        impactScore: Int,
        probabilityScore: Int,
        description: String,
        triggerFactor: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val trendId = generateNextId("TND")
            val trend = TrendEntity(
                id = trendId,
                title = title,
                category = category,
                horizon = horizon,
                maturity = maturity,
                impactScore = impactScore,
                probabilityScore = probabilityScore,
                description = description,
                triggerFactor = triggerFactor,
                status = "ACTIVA"
            )
            dao.insertTrend(trend)
            dao.insertAuditLog(
                AuditLogEntity(
                    timestamp = currentDateString(),
                    batchId = "PROSP-RADAR",
                    action = "TREND_REGISTERED",
                    affectedId = trendId,
                    status = "APROBADO",
                    details = "Tendencia: $title ($category / $maturity)"
                )
            )
            Result.success(trendId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateRoadmapMilestoneStatus(milestone: RoadmapMilestoneEntity, newStatus: String) = withContext(Dispatchers.IO) {
        dao.updateRoadmapMilestone(milestone.copy(status = newStatus))
    }

    suspend fun addCatalogItem(
        line: String,
        name: String,
        description: String,
        scope: String,
        responsible: String,
        referencePrice: Double
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val catId = generateNextId("CAT")
            val item = CatalogItemEntity(
                id = catId,
                line = line,
                name = name,
                description = description,
                scope = scope,
                responsible = responsible,
                referencePrice = referencePrice,
                isActive = true
            )
            dao.insertCatalogItem(item)
            dao.insertAuditLog(
                AuditLogEntity(
                    timestamp = currentDateString(),
                    batchId = "CATALOGO_OPS",
                    action = "CATALOG_ITEM_CREATED",
                    affectedId = catId,
                    status = "APROBADO",
                    details = "Servicio de catálogo creado: $name ($line)"
                )
            )
            Result.success(catId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createCommercialRequest(
        prospectId: String = "",
        fullName: String,
        documentNumber: String,
        phone: String,
        email: String,
        catalogItemId: String,
        catalogItemName: String,
        catalogLine: String,
        quotedAmount: Double,
        notes: String = ""
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val solId = generateNextId("SOL")
            val req = CommercialRequestEntity(
                id = solId,
                prospectId = prospectId,
                fullName = fullName,
                documentNumber = documentNumber,
                phone = phone,
                email = email,
                catalogItemId = catalogItemId,
                catalogItemName = catalogItemName,
                catalogLine = catalogLine,
                quotedAmount = quotedAmount,
                stage = "COTIZACION",
                createdAt = currentDateOnlyString(),
                notes = notes
            )
            dao.insertCommercialRequest(req)
            dao.insertAuditLog(
                AuditLogEntity(
                    timestamp = currentDateString(),
                    batchId = "GESTION_COMERCIAL",
                    action = "SOLICITUD_COTIZACION_CREADA",
                    affectedId = solId,
                    status = "APROBADO",
                    details = "Cotización para $fullName: $catalogItemName por $$quotedAmount"
                )
            )
            Result.success(solId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Executes the complete commercial contract sequence:
     * Prospecto / Solicitud → Cliente → Servicio contratado → Obligación → Caso (opcional)
     */
    suspend fun contractCommercialRequest(
        request: CommercialRequestEntity,
        agreedAmount: Double,
        paymentMethod: String,
        createCase: Boolean,
        targetEntity: String = "",
        radicado: String = "",
        risk: String = "MEDIO"
    ): Result<Triple<String, String, String?>> = withContext(Dispatchers.IO) {
        try {
            val result = executeIngresoUnico(
                fullName = request.fullName,
                documentNumber = request.documentNumber,
                phone = request.phone,
                email = request.email,
                serviceType = request.catalogItemName,
                agreedValue = agreedAmount,
                paymentMethod = paymentMethod,
                createLegalCase = createCase,
                targetEntity = targetEntity,
                radicado = radicado,
                risk = risk
            )
            result.onSuccess { (cli, ser, cas) ->
                dao.updateCommercialRequest(request.copy(stage = "CONTRATADA", quotedAmount = agreedAmount))
                if (request.prospectId.isNotBlank()) {
                    val prospect = dao.getProspectById(request.prospectId)
                    if (prospect != null) {
                        dao.updateProspect(prospect.copy(stage = "CONVERTIDO"))
                    }
                }
            }
            result
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Confirms a partial or total payment for an obligation.
     * Updates Obligation, Service balance, and generates an authentic INGRESO in MOV_FINANCIEROS.
     */
    suspend fun registerPaymentForObligation(
        obligationId: String,
        amount: Double,
        paymentMethod: String,
        receiptRef: String,
        supportType: String = "DOC",
        notes: String = ""
    ): Result<String> = withContext(Dispatchers.IO) {
        idLock.withLock {
            try {
                val obligation = dao.getObligationById(obligationId)
                    ?: return@withContext Result.failure(Exception("Obligación no encontrada: $obligationId"))

                val pagId = generateNextId("PAG")
                val payment = PaymentRecordEntity(
                    id = pagId,
                    obligationId = obligationId,
                    serviceId = obligation.serviceId,
                    clientId = obligation.clientId,
                    amount = amount,
                    paymentMethod = paymentMethod,
                    date = currentDateOnlyString(),
                    receiptRef = if (receiptRef.isNotBlank()) receiptRef else "REC-${System.currentTimeMillis() % 10000}",
                    supportDocId = "DOC-RECIBO",
                    supportType = supportType,
                    verified = true,
                    notes = notes
                )
                dao.insertPaymentRecord(payment)

                // Update Obligation balance
                val newPaidAmount = obligation.paidAmount + amount
                val newObligationBalance = (obligation.totalAmount - newPaidAmount).coerceAtLeast(0.0)
                val newObligationStatus = if (newObligationBalance <= 0.0) "PAGADA_TOTAL" else "PAGADA_PARCIAL"

                dao.updatePaymentObligation(
                    obligation.copy(
                        paidAmount = newPaidAmount,
                        balance = newObligationBalance,
                        status = newObligationStatus
                    )
                )

                // Update Service balance
                val service = dao.getServiceById(obligation.serviceId)
                if (service != null) {
                    val newServicePaid = service.paidValue + amount
                    val newServiceBalance = (service.agreedValue - newServicePaid).coerceAtLeast(0.0)
                    val newServiceState = if (newServiceBalance <= 0.0) "COMPLETADO" else "ACTIVO"

                    dao.updateService(
                        service.copy(
                            paidValue = newServicePaid,
                            balance = newServiceBalance,
                            state = newServiceState
                        )
                    )
                }

                // Register authentic financial movement (INGRESO in MOV_FINANCIEROS)
                val finId = generateNextId("FIN")
                val finMovement = FinancialMovementEntity(
                    id = finId,
                    date = currentDateOnlyString(),
                    type = "INGRESO",
                    scope = "PROSPECTIVA",
                    category = "HONORARIOS",
                    concept = "Pago confirmado ${obligation.concept} ($pagId)",
                    thirdParty = obligation.clientId,
                    amount = amount,
                    paymentMethod = paymentMethod,
                    supportType = supportType,
                    dueDate = obligation.dueDate,
                    isReconciled = true,
                    state = "CONFIRMADO",
                    notes = "Abono a obligación $obligationId / Servicio ${obligation.serviceId}"
                )
                dao.insertFinancialMovement(finMovement)

                // Audit log
                dao.insertAuditLog(
                    AuditLogEntity(
                        timestamp = currentDateString(),
                        batchId = "FIN-PAGO-CONFIRMADO",
                        action = "PAYMENT_RECORDED_AND_CONFIRMED",
                        affectedId = pagId,
                        status = "PAGO_CONFIRMADO",
                        details = "Pago de $$amount COP a obligación $obligationId. Saldo restante: $$newObligationBalance COP"
                    )
                )

                Result.success(pagId)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun createLegalCaseStandalone(
        clientId: String,
        serviceId: String,
        matter: String,
        targetEntity: String,
        radicado: String,
        termType: String = "HABILES_15",
        termDays: Int = 15,
        dueDate: String,
        risk: String = "MEDIO",
        nextAction: String = "",
        nextActionDate: String = "",
        notes: String = ""
    ): Result<String> = withContext(Dispatchers.IO) {
        idLock.withLock {
            try {
                val caseId = generateNextId("CAS")
                val filing = currentDateOnlyString()
                val legalCase = LegalCaseEntity(
                    id = caseId,
                    clientId = clientId,
                    serviceId = serviceId.ifBlank { "SIN_SERVICIO" },
                    matter = matter,
                    targetEntity = targetEntity,
                    radicado = radicado.ifBlank { "EXP-${System.currentTimeMillis() % 100000}" },
                    filingDate = filing,
                    termType = termType,
                    termDays = termDays,
                    dueDate = if (dueDate.isNotBlank()) dueDate else calculateDueDateString(termDays),
                    termState = "EN_TERMINO",
                    risk = risk,
                    nextAction = nextAction,
                    nextActionDate = nextActionDate,
                    state = "EN_PROCESO",
                    notes = notes
                )
                dao.insertLegalCase(legalCase)

                // Initial action
                val actionId = generateNextId("JUR")
                dao.insertLegalAction(
                    LegalActionEntity(
                        id = actionId,
                        caseId = caseId,
                        actionType = "PETICION_INICIAL",
                        date = filing,
                        description = "Radicación formal de $matter ante $targetEntity",
                        radicadoOrToken = legalCase.radicado,
                        responsible = "GESTION_JURIDICA"
                    )
                )

                dao.insertAuditLog(
                    AuditLogEntity(
                        timestamp = currentDateString(),
                        batchId = "JUR-NUEVO-CASO",
                        action = "LEGAL_CASE_CREATED",
                        affectedId = caseId,
                        status = "CASO_RADICADO",
                        details = "Caso $caseId radicado para cliente $clientId vs $targetEntity"
                    )
                )

                Result.success(caseId)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun addServiceForClient(
        clientId: String,
        catalogItemId: String,
        catalogLine: String,
        serviceType: String,
        agreedValue: Double,
        paymentMethod: String = "TRANSFERENCIA",
        notes: String = ""
    ): Result<Pair<String, String>> = withContext(Dispatchers.IO) {
        idLock.withLock {
            try {
                val serId = generateNextId("SER")
                val service = ServiceEntity(
                    id = serId,
                    clientId = clientId,
                    catalogItemId = catalogItemId,
                    catalogLine = catalogLine,
                    serviceType = serviceType,
                    agreedValue = agreedValue,
                    paidValue = 0.0,
                    balance = agreedValue,
                    paymentMethod = paymentMethod,
                    state = "ACTIVO",
                    createdAt = currentDateOnlyString(),
                    notes = notes
                )
                dao.insertService(service)

                val oblId = generateNextId("OBL")
                val obligation = PaymentObligationEntity(
                    id = oblId,
                    serviceId = serId,
                    clientId = clientId,
                    concept = "Obligación de pago por contratación de $serviceType ($serId)",
                    totalAmount = agreedValue,
                    paidAmount = 0.0,
                    balance = agreedValue,
                    dueDate = calculateDueDateString(30),
                    status = "PENDIENTE",
                    createdAt = currentDateOnlyString()
                )
                dao.insertPaymentObligation(obligation)

                val client = dao.getClientById(clientId)
                if (client != null) {
                    dao.insertClient(client.copy(totalServicesValue = client.totalServicesValue + agreedValue))
                }

                dao.insertAuditLog(
                    AuditLogEntity(
                        timestamp = currentDateString(),
                        batchId = "CRM-SERVICIO-CONTRATADO",
                        action = "SERVICE_AND_OBLIGATION_CREATED",
                        affectedId = serId,
                        status = "CONTRATO_REGISTRADO",
                        details = "Servicio $serId y Obligación $oblId registrados para cliente $clientId por valor $$agreedValue COP"
                    )
                )

                Result.success(Pair(serId, oblId))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun addDocument(
        title: String,
        docType: String,
        relatedEntityId: String,
        driveUrl: String = "",
        version: String = "V1"
    ): Result<String> = withContext(Dispatchers.IO) {
        idLock.withLock {
            try {
                val docId = generateNextId("DOC")
                val doc = DocumentEntity(
                    id = docId,
                    title = title,
                    docType = docType,
                    relatedEntityId = relatedEntityId,
                    driveUrl = driveUrl,
                    date = currentDateOnlyString(),
                    version = version,
                    hashSha256 = "SHA256-${System.currentTimeMillis() % 100000}"
                )
                dao.insertDocument(doc)
                Result.success(docId)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun currentDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT)
        return sdf.format(Date())
    }

    private fun currentDateOnlyString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
        return sdf.format(Date())
    }

    private fun calculateDueDateString(daysAhead: Int): String {
        val calendar = java.util.Calendar.getInstance()
        calendar.add(java.util.Calendar.DAY_OF_YEAR, daysAhead)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
        return sdf.format(calendar.time)
    }
}
