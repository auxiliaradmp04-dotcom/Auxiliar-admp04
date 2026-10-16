package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MasterOperationsDao {

    // === PERSONS ===
    @Query("SELECT * FROM persons ORDER BY id ASC")
    fun getAllPersons(): Flow<List<PersonEntity>>

    @Query("SELECT * FROM persons WHERE documentNumber = :doc LIMIT 1")
    suspend fun getPersonByDocument(doc: String): PersonEntity?

    @Query("SELECT * FROM persons WHERE id = :id LIMIT 1")
    suspend fun getPersonById(id: String): PersonEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerson(person: PersonEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPersons(persons: List<PersonEntity>)

    // === CLIENTS ===
    @Query("SELECT * FROM clients ORDER BY id ASC")
    fun getAllClients(): Flow<List<ClientEntity>>

    @Query("SELECT * FROM clients WHERE id = :id LIMIT 1")
    suspend fun getClientById(id: String): ClientEntity?

    @Query("SELECT * FROM clients WHERE documentNumber = :doc LIMIT 1")
    suspend fun getClientByDocument(doc: String): ClientEntity?

    @Query("SELECT * FROM clients WHERE fullName LIKE '%' || :query || '%' OR documentNumber LIKE '%' || :query || '%' OR phone LIKE '%' || :query || '%' OR id = :query")
    fun searchClients(query: String): Flow<List<ClientEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClient(client: ClientEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClients(clients: List<ClientEntity>)

    // === PROSPECTS ===
    @Query("SELECT * FROM prospects ORDER BY id ASC")
    fun getAllProspects(): Flow<List<ProspectEntity>>

    @Query("SELECT * FROM prospects WHERE id = :id LIMIT 1")
    suspend fun getProspectById(id: String): ProspectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProspect(prospect: ProspectEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProspects(prospects: List<ProspectEntity>)

    @Update
    suspend fun updateProspect(prospect: ProspectEntity)

    // === SERVICES ===
    @Query("SELECT * FROM services ORDER BY id ASC")
    fun getAllServices(): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM services WHERE clientId = :clientId")
    fun getServicesByClient(clientId: String): Flow<List<ServiceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: ServiceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServices(services: List<ServiceEntity>)

    // === LEGAL CASES ===
    @Query("SELECT * FROM legal_cases ORDER BY id ASC")
    fun getAllLegalCases(): Flow<List<LegalCaseEntity>>

    @Query("SELECT * FROM legal_cases WHERE clientId = :clientId")
    fun getLegalCasesByClient(clientId: String): Flow<List<LegalCaseEntity>>

    @Query("SELECT * FROM legal_cases WHERE id = :caseId LIMIT 1")
    suspend fun getLegalCaseById(caseId: String): LegalCaseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLegalCase(caseEntity: LegalCaseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLegalCases(cases: List<LegalCaseEntity>)

    @Update
    suspend fun updateLegalCase(caseEntity: LegalCaseEntity)

    // === LEGAL ACTIONS ===
    @Query("SELECT * FROM legal_actions WHERE caseId = :caseId ORDER BY date DESC")
    fun getActionsByCase(caseId: String): Flow<List<LegalActionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLegalAction(action: LegalActionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLegalActions(actions: List<LegalActionEntity>)

    // === FINANCIAL MOVEMENTS ===
    @Query("SELECT * FROM financial_movements ORDER BY date DESC, id DESC")
    fun getAllFinancialMovements(): Flow<List<FinancialMovementEntity>>

    @Query("SELECT * FROM financial_movements WHERE scope = :scope ORDER BY date DESC")
    fun getFinancialMovementsByScope(scope: String): Flow<List<FinancialMovementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFinancialMovement(movement: FinancialMovementEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFinancialMovements(movements: List<FinancialMovementEntity>)

    // === ACTIVITIES / AGENDA ===
    @Query("SELECT * FROM activities ORDER BY date ASC, startTime ASC")
    fun getAllActivities(): Flow<List<ActivityEntity>>

    @Query("SELECT * FROM activities WHERE termState != 'COMPLETADA' AND termState != 'CANCELADA' ORDER BY date ASC")
    fun getOpenActivities(): Flow<List<ActivityEntity>>

    @Query("SELECT * FROM activities WHERE relatedEntityId = :entityId")
    fun getActivitiesByEntity(entityId: String): Flow<List<ActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ActivityEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivities(activities: List<ActivityEntity>)

    @Update
    suspend fun updateActivity(activity: ActivityEntity)

    // === EMPLOYMENT PROCESSES ===
    @Query("SELECT * FROM employment_processes ORDER BY id ASC")
    fun getAllEmploymentProcesses(): Flow<List<EmploymentProcessEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmploymentProcess(process: EmploymentProcessEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmploymentProcesses(processes: List<EmploymentProcessEntity>)

    @Update
    suspend fun updateEmploymentProcess(process: EmploymentProcessEntity)

    // === DOCUMENTS ===
    @Query("SELECT * FROM documents ORDER BY id ASC")
    fun getAllDocuments(): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE relatedEntityId = :entityId")
    fun getDocumentsByEntity(entityId: String): Flow<List<DocumentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(doc: DocumentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocuments(docs: List<DocumentEntity>)

    // === COUNTERS & ATOMIC ID GENERATION ===
    @Query("SELECT * FROM system_counters WHERE prefix = :prefix LIMIT 1")
    suspend fun getCounter(prefix: String): SystemCounterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setCounter(counter: SystemCounterEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDefaultCounters(counters: List<SystemCounterEntity>)

    // === AUDIT LOGS ===
    @Query("SELECT * FROM audit_logs ORDER BY logId DESC LIMIT 100")
    fun getRecentAuditLogs(): Flow<List<AuditLogEntity>>

    @Insert
    suspend fun insertAuditLog(log: AuditLogEntity)

    @Query("SELECT COUNT(*) FROM clients")
    suspend fun getClientCount(): Int

    // === TRENDS & WEAK SIGNALS ===
    @Query("SELECT * FROM trends ORDER BY impactScore DESC, probabilityScore DESC")
    fun getAllTrends(): Flow<List<TrendEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrend(trend: TrendEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrends(trends: List<TrendEntity>)

    // === SCENARIOS ===
    @Query("SELECT * FROM scenarios ORDER BY probability DESC")
    fun getAllScenarios(): Flow<List<ScenarioEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScenario(scenario: ScenarioEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScenarios(scenarios: List<ScenarioEntity>)

    // === ROADMAP MILESTONES ===
    @Query("SELECT * FROM roadmap_milestones ORDER BY targetDate ASC")
    fun getAllRoadmapMilestones(): Flow<List<RoadmapMilestoneEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoadmapMilestone(milestone: RoadmapMilestoneEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoadmapMilestones(milestones: List<RoadmapMilestoneEntity>)

    @Update
    suspend fun updateRoadmapMilestone(milestone: RoadmapMilestoneEntity)

    // === COMMERCIAL CATALOG ===
    @Query("SELECT * FROM catalog_items WHERE isActive = 1 ORDER BY line ASC, name ASC")
    fun getAllCatalogItems(): Flow<List<CatalogItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCatalogItem(item: CatalogItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCatalogItems(items: List<CatalogItemEntity>)

    @Update
    suspend fun updateCatalogItem(item: CatalogItemEntity)

    // === PAYMENT OBLIGATIONS ===
    @Query("SELECT * FROM payment_obligations ORDER BY dueDate ASC")
    fun getAllPaymentObligations(): Flow<List<PaymentObligationEntity>>

    @Query("SELECT * FROM payment_obligations WHERE clientId = :clientId ORDER BY dueDate ASC")
    fun getObligationsByClient(clientId: String): Flow<List<PaymentObligationEntity>>

    @Query("SELECT * FROM payment_obligations WHERE serviceId = :serviceId ORDER BY dueDate ASC")
    fun getObligationsByService(serviceId: String): Flow<List<PaymentObligationEntity>>

    @Query("SELECT * FROM payment_obligations WHERE id = :id LIMIT 1")
    suspend fun getObligationById(id: String): PaymentObligationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaymentObligation(obligation: PaymentObligationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaymentObligations(obligations: List<PaymentObligationEntity>)

    @Update
    suspend fun updatePaymentObligation(obligation: PaymentObligationEntity)

    // === PAYMENT RECORDS ===
    @Query("SELECT * FROM payment_records ORDER BY date DESC, id DESC")
    fun getAllPaymentRecords(): Flow<List<PaymentRecordEntity>>

    @Query("SELECT * FROM payment_records WHERE clientId = :clientId ORDER BY date DESC")
    fun getPaymentsByClient(clientId: String): Flow<List<PaymentRecordEntity>>

    @Query("SELECT * FROM payment_records WHERE serviceId = :serviceId ORDER BY date DESC")
    fun getPaymentsByService(serviceId: String): Flow<List<PaymentRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaymentRecord(record: PaymentRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaymentRecords(records: List<PaymentRecordEntity>)

    // === COMMERCIAL REQUESTS / COTIZACIONES ===
    @Query("SELECT * FROM commercial_requests ORDER BY createdAt DESC")
    fun getAllCommercialRequests(): Flow<List<CommercialRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommercialRequest(request: CommercialRequestEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommercialRequests(requests: List<CommercialRequestEntity>)

    @Update
    suspend fun updateCommercialRequest(request: CommercialRequestEntity)

    @Update
    suspend fun updateService(service: ServiceEntity)

    @Query("SELECT * FROM services WHERE id = :id LIMIT 1")
    suspend fun getServiceById(id: String): ServiceEntity?
}
