package com.harun.splitledger.data.repository

import com.harun.splitledger.data.local.db.dao.SettlementDao
import com.harun.splitledger.data.mapper.toDomain
import com.harun.splitledger.data.mapper.toEntity
import com.harun.splitledger.domain.model.Settlement
import com.harun.splitledger.domain.repository.SettlementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettlementRepositoryImpl @Inject constructor(
    private val settlementDao: SettlementDao
): SettlementRepository {
    override fun observeSettlements(tripId: String): Flow<List<Settlement>> {
        return settlementDao.observeSettlements(tripId).map { data ->
            data.map { it.toDomain() }
        }
    }

    override suspend fun saveSettlement(settlement: Settlement) {
        settlementDao.upsertSettlement(settlement.toEntity())
    }

    override suspend fun deleteSettlement(settlementId: String) {
        settlementDao.deleteSettlement(settlementId)
    }

    override suspend fun isParticipantUseBySettlement(participantId: String): Boolean {
        return settlementDao.isParticipantUsedBySettlement(participantId)
    }
}