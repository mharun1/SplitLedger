package com.harun.splitledger.domain.repository

import com.harun.splitledger.domain.model.FxRate
import kotlinx.coroutines.flow.Flow

interface FxRateRepository {
    fun observeRates(): Flow<List<FxRate>>
    suspend fun getRates(): List<FxRate>
    fun isRateStale(rate: FxRate, nowEpochMs: Long): Boolean
}