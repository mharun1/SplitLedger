package com.harun.splitledger.domain.repository

import com.harun.splitledger.domain.model.Settlement
import kotlinx.coroutines.flow.Flow

interface SettlementRepository {
    fun observeSettlements(tripId: String): Flow<List<Settlement>>
    suspend fun saveSettlement(settlement: Settlement)
    suspend fun deleteSettlement(settlementId: String)
    suspend fun isParticipantUseBySettlement(participantId: String): Boolean
}