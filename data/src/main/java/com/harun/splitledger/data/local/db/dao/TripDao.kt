package com.harun.splitledger.data.local.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.harun.splitledger.data.local.db.entity.ParticipantEntity
import com.harun.splitledger.data.local.db.entity.TripEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Query("SELECT * FROM trips ORDER BY createdAtEpochMs DESC")
    fun observeTrips(): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE id = :tripId")
    fun observeTrip(tripId: String): Flow<TripEntity?>

    @Query("SELECT * FROM trips WHERE id = :tripId")
    suspend fun getTripById(tripId: String): TripEntity?

    @Query("SELECT * FROM participants WHERE tripId = :tripId ORDER BY name ASC")
    fun observeParticipants(tripId: String): Flow<List<ParticipantEntity>>

    @Query("SELECT * FROM participants WHERE tripId = :tripId ORDER BY name ASC")
    suspend fun getParticipants(tripId: String): List<ParticipantEntity>

    @Upsert
    suspend fun upsertTrip(trip: TripEntity)

    @Upsert
    suspend fun upsertParticipant(participant: ParticipantEntity)

    @Upsert
    suspend fun upsertParticipants(participants: List<ParticipantEntity>)

    @Query("UPDATE trips SET isSettled = :isSettled WHERE id = :tripId ")
    suspend fun setTripSettled(tripId: String, isSettled: Boolean)

    @Query("DELETE FROM participants WHERE id = :participantId")
    suspend fun deleteParticipant(participantId: String)

    @Query("DELETE FROM settlements WHERE tripId = :tripId")
    suspend fun deleteSettlementByTripId(tripId: String)

    @Query("DELETE FROM expenses WHERE tripId = :tripId")
    suspend fun deleteExpenseByTripId(tripId: String)

    @Query("DELETE FROM trips WHERE id = :tripId")
    suspend fun deleteTripById(tripId: String)

    @Query("SELECT id FROM trips WHERE isDemo = 1")
    suspend fun getDemoTripIds(): List<String>

    @Transaction
    suspend fun deleteTrip(tripId: String) {
        deleteSettlementByTripId(tripId)
        deleteExpenseByTripId(tripId)
        deleteTripById(tripId)
    }
}