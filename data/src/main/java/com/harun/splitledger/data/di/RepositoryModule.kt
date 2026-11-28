package com.harun.splitledger.data.di

import com.harun.splitledger.data.repository.ExpenseRepositoryImpl
import com.harun.splitledger.data.repository.FxRateRepositoryImpl
import com.harun.splitledger.data.repository.SettlementRepositoryImpl
import com.harun.splitledger.data.repository.TripRepositoryImpl
import com.harun.splitledger.data.repository.UserPreferenceRepositoryImpl
import com.harun.splitledger.data.worker.WorkManagerFxSyncScheduler
import com.harun.splitledger.domain.repository.ExpenseRepository
import com.harun.splitledger.domain.repository.FxRateRepository
import com.harun.splitledger.domain.repository.FxSyncScheduler
import com.harun.splitledger.domain.repository.SettlementRepository
import com.harun.splitledger.domain.repository.TripRepository
import com.harun.splitledger.domain.repository.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindTripRepository(
        tripRepositoryImpl: TripRepositoryImpl
    ): TripRepository

    @Binds
    @Singleton
    abstract fun bindExpenseRepository(
        expenseRepositoryImpl: ExpenseRepositoryImpl
    ): ExpenseRepository

    @Binds
    @Singleton
    abstract fun bindSettlementRepository(
        settlementRepositoryImpl: SettlementRepositoryImpl
    ): SettlementRepository

    @Binds
    @Singleton
    abstract fun bindFxRateRepository(
        fxRateRepositoryImpl: FxRateRepositoryImpl
    ): FxRateRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        userPreferenceRepositoryImpl: UserPreferenceRepositoryImpl
    ): UserPreferencesRepository

    @Binds
    @Singleton
    abstract fun bindWorkMangerFxSyncScheduler(
        fxSyncScheduler: WorkManagerFxSyncScheduler
    ): FxSyncScheduler
}