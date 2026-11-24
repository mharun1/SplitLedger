package com.harun.splitledger.domain.repository

import com.harun.splitledger.domain.model.Participant
import com.harun.splitledger.domain.model.Trip
import kotlinx.coroutines.flow.Flow

interface TripRepository {
    fun observeTrips(): Flow<List<Trip>>
    fun observeSingleTrip(tripId: String): Flow<Trip?>
    suspend fun getTripById(tripId: String): Trip?
    fun observeParticipants(tripId: String): Flow<List<Participant>>
    suspend fun getParticipants(tripId: String): List<Participant>
    suspend fun upsertTrip(trip: Trip)
    suspend fun setTripSettled(tripId: String, isSettled: Boolean)
    suspend fun deleteTrip(tripId: String)
    suspend fun upsertParticipant(participant: Participant)
    suspend fun deleteParticipant(participantId: String)
    suspend fun resetTripDemo()
}