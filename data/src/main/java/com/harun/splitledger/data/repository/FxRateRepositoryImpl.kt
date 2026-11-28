package com.harun.splitledger.data.repository

import com.harun.splitledger.data.local.db.dao.FxRateDao
import com.harun.splitledger.data.mapper.toDomain
import com.harun.splitledger.domain.model.FxRate
import com.harun.splitledger.domain.repository.FxRateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private const val STALE_THRESHOLD_MS = 86_400_000L
@Singleton
class FxRateRepositoryImpl @Inject constructor(
    private val fxRateDao: FxRateDao
): FxRateRepository {
    override fun observeRates(): Flow<List<FxRate>> {
        return fxRateDao.observeRates().map { data ->
            data.map { it.toDomain() }
        }
    }

    override suspend fun getRates(): List<FxRate> {
        return fxRateDao.getRates().map { it.toDomain() }
    }

    override fun isRateStale(
        rate: FxRate,
        nowEpochMs: Long
    ): Boolean {
        return rate.isBundledFallback || (nowEpochMs - rate.updatedAtEpochMs) > STALE_THRESHOLD_MS
    }
}