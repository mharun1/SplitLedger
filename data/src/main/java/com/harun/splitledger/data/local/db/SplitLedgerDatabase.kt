package com.harun.splitledger.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.harun.splitledger.data.local.db.dao.ExpenseDao
import com.harun.splitledger.data.local.db.dao.FxRateDao
import com.harun.splitledger.data.local.db.dao.SettlementDao
import com.harun.splitledger.data.local.db.dao.TripDao
import com.harun.splitledger.data.local.db.entity.ExpenseEntity
import com.harun.splitledger.data.local.db.entity.ExpenseSplitEntity
import com.harun.splitledger.data.local.db.entity.FxRateEntity
import com.harun.splitledger.data.local.db.entity.ParticipantEntity
import com.harun.splitledger.data.local.db.entity.SettlementEntity
import com.harun.splitledger.data.local.db.entity.TripEntity

@Database(
    entities = [
        TripEntity::class,
        FxRateEntity::class,
        ExpenseEntity::class,
        SettlementEntity::class,
        ParticipantEntity::class,
        ExpenseSplitEntity::class,
    ],
    version = 1,
    exportSchema = true
)
abstract class SplitLedgerDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun settlementDao(): SettlementDao
    abstract fun fxRateDao(): FxRateDao
}
