package com.harun.splitledger.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import androidx.work.WorkManager
import com.harun.splitledger.data.local.db.SplitLedgerDatabase
import com.harun.splitledger.data.local.db.dao.ExpenseDao
import com.harun.splitledger.data.local.db.dao.FxRateDao
import com.harun.splitledger.data.local.db.dao.SettlementDao
import com.harun.splitledger.data.local.db.dao.TripDao
import com.harun.splitledger.data.local.prefs.userPreferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SplitLedgerDatabase {
        return Room.databaseBuilder(
            context,
            SplitLedgerDatabase::class.java,
            "splitledger.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideTripDao(database: SplitLedgerDatabase): TripDao {
        return database.tripDao()
    }

    @Provides
    @Singleton
    fun provideExpenseDao(database: SplitLedgerDatabase): ExpenseDao {
        return database.expenseDao()
    }

    @Provides
    @Singleton
    fun provideSettlementDao(database: SplitLedgerDatabase): SettlementDao {
        return database.settlementDao()
    }

    @Provides
    @Singleton
    fun provideFxRateDao(database: SplitLedgerDatabase): FxRateDao {
        return database.fxRateDao()
    }

    @Provides
    @Singleton
    fun provideUserPreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.userPreferencesDataStore
    }

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }
}