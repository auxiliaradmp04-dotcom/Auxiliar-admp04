package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ProspectivaDatabase
import com.example.data.local.entity.*
import com.example.data.repository.MasterOperationsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DashboardSummary(
    val activeClientsCount: Int = 0,
    val prospectsCount: Int = 0,
    val openCasesCount: Int = 0,
    val termsDueUrgent: Int = 0,
    val availableCash: Double = 249102.0,
    val accountsPayable: Double = 2001235.0,
    val rapPacificoPending: Double = 3800000.0,
    val projectedBalance: Double = 2047867.0,
    val openActivitiesCount: Int = 0,
    val activeEmploymentCount: Int = 0,
    val qaScore: Int = 100,
    val qaStatus: String = "APROBADO (0 errores, 0 advertencias)"
)

data class Client360State(
    val client: ClientEntity? = null,
    val person: PersonEntity? = null,
    val services: List<ServiceEntity> = emptyList(),
    val obligations: List<PaymentObligationEntity> = emptyList(),
    val payments: List<PaymentRecordEntity> = emptyList(),
    val legalCases: List<LegalCaseEntity> = emptyList(),
    val activities: List<ActivityEntity> = emptyList(),
    val documents: List<DocumentEntity> = emptyList()
)

class MasterOperationsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MasterOperationsRepository

    init {
        val database = ProspectivaDatabase.getDatabase(application)
        repository = MasterOperationsRepository(database.masterOperationsDao())
        viewModelScope.launch {
            repository.initializeIfEmpty()
        }
    }

    // Streams from Repository
    val clients: StateFlow<List<ClientEntity>> = repository.clients
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val prospects: StateFlow<List<ProspectEntity>> = repository.prospects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val legalCases: StateFlow<List<LegalCaseEntity>> = repository.legalCases
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val financialMovements: StateFlow<List<FinancialMovementEntity>> = repository.financialMovements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activities: StateFlow<List<ActivityEntity>> = repository.activities
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val employmentProcesses: StateFlow<List<EmploymentProcessEntity>> = repository.employmentProcesses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val documents: StateFlow<List<DocumentEntity>> = repository.documents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val auditLogs: StateFlow<List<AuditLogEntity>> = repository.auditLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val trends: StateFlow<List<TrendEntity>> = repository.trends
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val scenarios: StateFlow<List<ScenarioEntity>> = repository.scenarios
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val roadmapMilestones: StateFlow<List<RoadmapMilestoneEntity>> = repository.roadmapMilestones
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val catalogItems: StateFlow<List<CatalogItemEntity>> = repository.catalogItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val paymentObligations: StateFlow<List<PaymentObligationEntity>> = repository.paymentObligations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val paymentRecords: StateFlow<List<PaymentRecordEntity>> = repository.paymentRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val commercialRequests: StateFlow<List<CommercialRequestEntity>> = repository.commercialRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dashboard State
    val dashboardSummary: StateFlow<DashboardSummary> = combine(
        clients, prospects, legalCases, financialMovements, activities
    ) { cList, pList, caseList, finList, actList ->
        val activeClients = cList.count { it.state == "ACTIVO" }
        val openCases = caseList.count { it.state == "EN_PROCESO" || it.state == "ABIERTO" }
        val urgentTerms = caseList.count { it.termState == "VENCIDO" || it.termState == "VENCE_HOY" }
        val openActs = actList.count { it.termState != "COMPLETADA" && it.termState != "CANCELADA" }

        // Compute actual finances
        val incomes = finList.filter { it.type == "INGRESO" }.sumOf { it.amount }
        val expenses = finList.filter { it.type == "GASTO" && it.state == "CONFIRMADO" }.sumOf { it.amount }
        val baseAvailable = 249102.0 // Recorded starting liquidity in master sheet
        val currentCash = (baseAvailable + incomes - (expenses - 2235.0)).coerceAtLeast(0.0)

        DashboardSummary(
            activeClientsCount = activeClients,
            prospectsCount = pList.count { it.stage != "CONVERTIDO" },
            openCasesCount = openCases,
            termsDueUrgent = urgentTerms,
            availableCash = currentCash,
            accountsPayable = 2001235.0,
            rapPacificoPending = 3800000.0,
            projectedBalance = 2047867.0,
            openActivitiesCount = openActs,
            activeEmploymentCount = 0,
            qaScore = 100,
            qaStatus = "APROBADO (0 errores, 0 advertencias)"
        )
    }.combine(employmentProcesses) { summary, empList ->
        summary.copy(activeEmploymentCount = empList.count { it.stage != "CERRADA" })
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardSummary())

    // 360 View State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedClient360 = MutableStateFlow<Client360State>(Client360State())
    val selectedClient360: StateFlow<Client360State> = _selectedClient360.asStateFlow()

    // Status message for Snackbars
    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectClientFor360(client: ClientEntity) {
        viewModelScope.launch {
            val person = repository.getPersonByDocument(client.documentNumber)
            repository.getServicesByClient(client.id).collectLatest { servs ->
                repository.getObligationsByClient(client.id).collectLatest { obls ->
                    repository.getPaymentsByClient(client.id).collectLatest { pags ->
                        repository.getLegalCasesByClient(client.id).collectLatest { cases ->
                            repository.getActivitiesByEntity(client.id).collectLatest { acts ->
                                repository.getDocumentsByEntity(client.id).collectLatest { docs ->
                                    _selectedClient360.value = Client360State(
                                        client = client,
                                        person = person,
                                        services = servs,
                                        obligations = obls,
                                        payments = pags,
                                        legalCases = cases,
                                        activities = acts,
                                        documents = docs
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun loadClient360ById(query: String) {
        viewModelScope.launch {
            val q = query.trim()
            val allClients = clients.value
            val allCases = legalCases.value
            val allServices = repository.getServicesByClient("").firstOrNull() ?: emptyList()

            // 1. Search directly in clients by ID, document, full name, phone, or email
            var match = allClients.find {
                it.id.equals(q, ignoreCase = true) ||
                        it.documentNumber.equals(q, ignoreCase = true) ||
                        it.fullName.contains(q, ignoreCase = true) ||
                        it.phone.contains(q) ||
                        it.email.contains(q, ignoreCase = true)
            }

            // 2. If not found, search in legal cases by radicado or target entity
            if (match == null) {
                val caseMatch = allCases.find {
                    it.id.equals(q, ignoreCase = true) ||
                            it.radicado.contains(q, ignoreCase = true) ||
                            it.targetEntity.contains(q, ignoreCase = true)
                }
                if (caseMatch != null) {
                    match = allClients.find { it.id == caseMatch.clientId }
                }
            }

            // 3. If not found, search in services by service ID
            if (match == null) {
                val servMatch = allServices.find { it.id.equals(q, ignoreCase = true) }
                if (servMatch != null) {
                    match = allClients.find { it.id == servMatch.clientId }
                }
            }

            if (match != null) {
                selectClientFor360(match)
                _userMessage.emit("Expediente cargado: ${match.fullName} (${match.id})")
            } else {
                _userMessage.emit("No se encontró ningún expediente con criterio: $query")
            }
        }
    }

    // Actions
    fun executeIngresoUnico(
        fullName: String,
        documentNumber: String,
        phone: String,
        email: String,
        serviceType: String,
        agreedValue: Double,
        paymentMethod: String,
        createCase: Boolean,
        caseMatter: String,
        targetEntity: String,
        radicado: String,
        risk: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.executeIngresoUnico(
                fullName = fullName,
                documentNumber = documentNumber,
                phone = phone,
                email = email,
                serviceType = serviceType,
                agreedValue = agreedValue,
                paymentMethod = paymentMethod,
                createLegalCase = createCase,
                caseMatter = caseMatter,
                targetEntity = targetEntity,
                radicado = radicado,
                risk = risk
            )
            result.onSuccess { (cli, ser, cas) ->
                val msg = if (cas != null) {
                    "Ingreso Único Exitoso: Cliente $cli, Servicio $ser y Caso $cas creados."
                } else {
                    "Ingreso Único Exitoso: Cliente $cli y Servicio $ser creados."
                }
                _userMessage.emit(msg)
                onSuccess()
            }.onFailure { err ->
                _userMessage.emit("Error en Ingreso Único: ${err.message}")
            }
        }
    }

    fun addLegalAction(caseId: String, actionType: String, description: String, radicado: String) {
        viewModelScope.launch {
            repository.addLegalAction(caseId, actionType, description, radicado)
                .onSuccess { actionId ->
                    _userMessage.emit("Actuación $actionId registrada para el caso $caseId")
                }.onFailure { err ->
                    _userMessage.emit("Error al registrar actuación: ${err.message}")
                }
        }
    }

    fun addFinancialMovement(
        type: String,
        scope: String,
        category: String,
        concept: String,
        thirdParty: String,
        amount: Double,
        paymentMethod: String,
        supportType: String,
        dueDate: String = "",
        notes: String = "",
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            repository.addFinancialMovement(
                type = type,
                scope = scope,
                category = category,
                concept = concept,
                thirdParty = thirdParty,
                amount = amount,
                paymentMethod = paymentMethod,
                supportType = supportType,
                dueDate = dueDate,
                notes = notes
            ).onSuccess { finId ->
                _userMessage.emit("Movimiento $finId registrado exitosamente.")
                onSuccess()
            }.onFailure { err ->
                _userMessage.emit("Error financiero: ${err.message}")
            }
        }
    }

    fun addActivity(
        title: String,
        module: String,
        blockType: String,
        date: String,
        startTime: String,
        endTime: String,
        priority: String,
        syncCalendar: Boolean,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            repository.addActivity(
                title = title,
                module = module,
                blockType = blockType,
                date = date,
                startTime = startTime,
                endTime = endTime,
                priority = priority,
                syncCalendar = syncCalendar
            ).onSuccess { actId ->
                val calMsg = if (syncCalendar) " (Programado para sincronización Google Calendar AUT-007)" else ""
                _userMessage.emit("Actividad $actId registrada$calMsg.")
                onSuccess()
            }.onFailure { err ->
                _userMessage.emit("Error al crear actividad: ${err.message}")
            }
        }
    }

    fun toggleActivityDone(activity: ActivityEntity) {
        viewModelScope.launch {
            val newState = if (activity.termState == "COMPLETADA") "PROGRAMADA" else "COMPLETADA"
            repository.updateActivityState(activity, newState)
            _userMessage.emit("Actividad ${activity.id} marcada como $newState")
        }
    }

    fun updateLegalCaseState(caseEntity: LegalCaseEntity, newState: String, termState: String) {
        viewModelScope.launch {
            repository.updateLegalCaseState(caseEntity, newState, termState)
            _userMessage.emit("Caso ${caseEntity.id} actualizado a $newState ($termState)")
        }
    }

    fun updateEmploymentStage(process: EmploymentProcessEntity, newStage: String, notes: String) {
        viewModelScope.launch {
            repository.updateEmploymentStage(process, newStage, notes)
            _userMessage.emit("Proceso ${process.id} actualizado a etapa $newStage")
        }
    }

    fun updateProspectStage(prospect: ProspectEntity, newStage: String) {
        viewModelScope.launch {
            repository.updateProspectStage(prospect, newStage)
            _userMessage.emit("Prospecto ${prospect.id} movido a etapa $newStage")
        }
    }

    // === PROSPECTIVA ESTRATÉGICA & SIMULADOR ===
    private val _simGrowthPct = MutableStateFlow(18)
    val simGrowthPct: StateFlow<Int> = _simGrowthPct.asStateFlow()

    private val _simCrmConversionPct = MutableStateFlow(45)
    val simCrmConversionPct: StateFlow<Int> = _simCrmConversionPct.asStateFlow()

    private val _simCollectionLagDays = MutableStateFlow(30)
    val simCollectionLagDays: StateFlow<Int> = _simCollectionLagDays.asStateFlow()

    private val _simRegulatoryRisk = MutableStateFlow("MEDIO")
    val simRegulatoryRisk: StateFlow<String> = _simRegulatoryRisk.asStateFlow()

    val simulationOutput = combine(
        _simGrowthPct, _simCrmConversionPct, _simCollectionLagDays, _simRegulatoryRisk
    ) { growth, conversion, lag, risk ->
        // Base semester income: approx $18,000,000 COP baseline
        val baseSemiAnnualIncome = 18000000.0
        val growthFactor = 1.0 + (growth / 100.0)
        val conversionMultiplier = conversion / 45.0 // normalized to baseline
        val lagPenalty = if (lag > 45) (lag - 45) * 40000.0 else 0.0
        val riskPenaltyFactor = when (risk) {
            "ALTO" -> 0.90
            "CRITICO" -> 0.80
            else -> 1.0
        }

        val projectedRevenue = ((baseSemiAnnualIncome * growthFactor * conversionMultiplier) - lagPenalty) * riskPenaltyFactor
        val projectedCases = (12 * growthFactor * (conversion / 35.0)).toInt().coerceAtLeast(3)

        // Liquidity stress score (0-100)
        val stressScore = (100 - (lag * 0.7).toInt() + (conversion * 0.3).toInt() + (if (risk == "ALTO") -15 else 0)).coerceIn(10, 100)

        val recommendation = when {
            stressScore > 75 -> "Condiciones favorables: Escalar captación comercial y formalizar alianzas."
            stressScore in 50..75 -> "Trayectoria balanceada: Monitorear cartera en los primeros 10 días y mitigar rezago."
            else -> "Alerta prospectiva: Riesgo de iliquidez. Activar reserva contingente y frenar gastos discrecionales."
        }

        SimulationResult(
            projectedSixMonthRevenue = projectedRevenue.coerceAtLeast(0.0),
            projectedCaseVolume = projectedCases,
            liquidityStressScore = stressScore,
            strategicRecommendation = recommendation
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SimulationResult(18000000.0, 12, 80, "Trayectoria balanceada"))

    fun updateSimulationGrowth(pct: Int) { _simGrowthPct.value = pct }
    fun updateSimulationConversion(pct: Int) { _simCrmConversionPct.value = pct }
    fun updateSimulationLag(days: Int) { _simCollectionLagDays.value = days }
    fun updateSimulationRisk(risk: String) { _simRegulatoryRisk.value = risk }

    fun addTrend(
        title: String,
        category: String,
        horizon: String,
        maturity: String,
        impactScore: Int,
        probabilityScore: Int,
        description: String,
        triggerFactor: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            repository.addTrend(
                title = title,
                category = category,
                horizon = horizon,
                maturity = maturity,
                impactScore = impactScore,
                probabilityScore = probabilityScore,
                description = description,
                triggerFactor = triggerFactor
            ).onSuccess { id ->
                _userMessage.emit("Tendencia $id registrada en el Radar Estratégico.")
                onSuccess()
            }.onFailure { err ->
                _userMessage.emit("Error al registrar tendencia: ${err.message}")
            }
        }
    }

    fun updateRoadmapMilestoneStatus(milestone: RoadmapMilestoneEntity, newStatus: String) {
        viewModelScope.launch {
            repository.updateRoadmapMilestoneStatus(milestone, newStatus)
            _userMessage.emit("Hito ${milestone.id} actualizado a $newStatus")
        }
    }

    // === GESTIÓN COMERCIAL & CATÁLOGO ===
    fun addCatalogItem(
        line: String,
        name: String,
        description: String,
        scope: String,
        responsible: String,
        referencePrice: Double,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            repository.addCatalogItem(line, name, description, scope, responsible, referencePrice)
                .onSuccess { id ->
                    _userMessage.emit("Servicio $id agregado al catálogo comercial.")
                    onSuccess()
                }.onFailure { err ->
                    _userMessage.emit("Error al agregar servicio: ${err.message}")
                }
        }
    }

    fun createCommercialRequest(
        prospectId: String = "",
        fullName: String,
        documentNumber: String,
        phone: String,
        email: String,
        catalogItemId: String,
        catalogItemName: String,
        catalogLine: String,
        quotedAmount: Double,
        notes: String = "",
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            repository.createCommercialRequest(
                prospectId = prospectId,
                fullName = fullName,
                documentNumber = documentNumber,
                phone = phone,
                email = email,
                catalogItemId = catalogItemId,
                catalogItemName = catalogItemName,
                catalogLine = catalogLine,
                quotedAmount = quotedAmount,
                notes = notes
            ).onSuccess { id ->
                _userMessage.emit("Solicitud comercial $id registrada para $fullName.")
                onSuccess()
            }.onFailure { err ->
                _userMessage.emit("Error en cotización: ${err.message}")
            }
        }
    }

    fun contractCommercialRequest(
        request: CommercialRequestEntity,
        agreedAmount: Double,
        paymentMethod: String,
        createCase: Boolean,
        targetEntity: String = "",
        radicado: String = "",
        risk: String = "MEDIO",
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            repository.contractCommercialRequest(
                request = request,
                agreedAmount = agreedAmount,
                paymentMethod = paymentMethod,
                createCase = createCase,
                targetEntity = targetEntity,
                radicado = radicado,
                risk = risk
            ).onSuccess { (cli, ser, cas) ->
                val caseText = if (cas != null) " y Caso $cas" else ""
                _userMessage.emit("¡Contratación Exitosa! Cliente $cli, Servicio $ser$caseText generados con obligación OBL.")
                onSuccess()
            }.onFailure { err ->
                _userMessage.emit("Error al contratar: ${err.message}")
            }
        }
    }

    // === GESTIÓN DE PAGOS Y OBLIGACIONES ===
    fun registerPaymentForObligation(
        obligationId: String,
        amount: Double,
        paymentMethod: String,
        receiptRef: String,
        supportType: String = "DOC",
        notes: String = "",
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            repository.registerPaymentForObligation(
                obligationId = obligationId,
                amount = amount,
                paymentMethod = paymentMethod,
                receiptRef = receiptRef,
                supportType = supportType,
                notes = notes
            ).onSuccess { pagId ->
                _userMessage.emit("Pago $pagId confirmado exitosamente y registrado en MOV_FINANCIEROS.")
                // Refresh client 360 if open
                _selectedClient360.value.client?.let { selectClientFor360(it) }
                onSuccess()
            }.onFailure { err ->
                _userMessage.emit("Error al procesar pago: ${err.message}")
            }
        }
    }

    fun createLegalCaseStandalone(
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
        notes: String = "",
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            repository.createLegalCaseStandalone(
                clientId = clientId,
                serviceId = serviceId,
                matter = matter,
                targetEntity = targetEntity,
                radicado = radicado,
                termType = termType,
                termDays = termDays,
                dueDate = dueDate,
                risk = risk,
                nextAction = nextAction,
                nextActionDate = nextActionDate,
                notes = notes
            ).onSuccess { caseId ->
                _userMessage.emit("Caso $caseId radicado exitosamente ante $targetEntity.")
                _selectedClient360.value.client?.let { selectClientFor360(it) }
                onSuccess()
            }.onFailure { err ->
                _userMessage.emit("Error al radicar caso: ${err.message}")
            }
        }
    }

    fun addServiceForClient(
        clientId: String,
        catalogItemId: String,
        catalogLine: String,
        serviceType: String,
        agreedValue: Double,
        paymentMethod: String = "TRANSFERENCIA",
        notes: String = "",
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            repository.addServiceForClient(
                clientId = clientId,
                catalogItemId = catalogItemId,
                catalogLine = catalogLine,
                serviceType = serviceType,
                agreedValue = agreedValue,
                paymentMethod = paymentMethod,
                notes = notes
            ).onSuccess { (serId, oblId) ->
                _userMessage.emit("Servicio $serId contratado y Obligación $oblId creada para cliente.")
                _selectedClient360.value.client?.let { selectClientFor360(it) }
                onSuccess()
            }.onFailure { err ->
                _userMessage.emit("Error al contratar servicio: ${err.message}")
            }
        }
    }

    fun addActivityForClient(
        clientId: String,
        title: String,
        module: String,
        blockType: String,
        date: String,
        startTime: String,
        endTime: String,
        priority: String,
        syncCalendar: Boolean,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            repository.addActivity(
                title = title,
                module = module,
                blockType = blockType,
                date = date,
                startTime = startTime,
                endTime = endTime,
                priority = priority,
                syncCalendar = syncCalendar,
                relatedEntityId = clientId
            ).onSuccess { actId ->
                _userMessage.emit("Actividad $actId programada para el expediente.")
                _selectedClient360.value.client?.let { selectClientFor360(it) }
                onSuccess()
            }.onFailure { err ->
                _userMessage.emit("Error al programar actividad: ${err.message}")
            }
        }
    }

    fun addDocumentForClient(
        clientId: String,
        title: String,
        docType: String,
        driveUrl: String = "",
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            repository.addDocument(
                title = title,
                docType = docType,
                relatedEntityId = clientId,
                driveUrl = driveUrl
            ).onSuccess { docId ->
                _userMessage.emit("Documento $docId vinculado al expediente.")
                _selectedClient360.value.client?.let { selectClientFor360(it) }
                onSuccess()
            }.onFailure { err ->
                _userMessage.emit("Error al vincular documento: ${err.message}")
            }
        }
    }
}

data class SimulationResult(
    val projectedSixMonthRevenue: Double,
    val projectedCaseVolume: Int,
    val liquidityStressScore: Int,
    val strategicRecommendation: String
)
