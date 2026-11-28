package com.harun.splitledger.data.local.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.harun.splitledger.data.local.db.entity.SettlementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SettlementDao {
    @Query("SELECT * FROM settlements WHERE tripId = :tripId ORDER BY createdAtEpochMs DESC")
    fun observeSettlements(tripId: String): Flow<List<SettlementEntity>>

    @Query("SELECT EXISTS(SELECT fromParticipantId FROM settlements WHERE fromParticipantId = :participantId OR toParticipantId = :participantId)")
    suspend fun isParticipantUsedBySettlement(participantId: String): Boolean

    @Upsert
    suspend fun upsertSettlement(settlement: SettlementEntity)

    @Query("DELETE FROM settlements WHERE id = :settlementId")
    suspend fun deleteSettlement(settlementId: String)

}