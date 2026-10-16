package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.MasterOperationsDao
import com.example.data.local.entity.*

@Database(
    entities = [
        PersonEntity::class,
        ClientEntity::class,
        ProspectEntity::class,
        ServiceEntity::class,
        LegalCaseEntity::class,
        LegalActionEntity::class,
        FinancialMovementEntity::class,
        ActivityEntity::class,
        EmploymentProcessEntity::class,
        DocumentEntity::class,
        SystemCounterEntity::class,
        AuditLogEntity::class,
        TrendEntity::class,
        ScenarioEntity::class,
        RoadmapMilestoneEntity::class,
        CatalogItemEntity::class,
        PaymentObligationEntity::class,
        PaymentRecordEntity::class,
        CommercialRequestEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class ProspectivaDatabase : RoomDatabase() {
    abstract fun masterOperationsDao(): MasterOperationsDao

    companion object {
        @Volatile
        private var INSTANCE: ProspectivaDatabase? = null

        fun getDatabase(context: Context): ProspectivaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ProspectivaDatabase::class.java,
                    "prospectiva_master_ops.db"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
