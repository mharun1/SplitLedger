package com.harun.splitledger.data.repository

import com.harun.splitledger.data.local.db.dao.TripDao
import com.harun.splitledger.data.local.seed.DemoTripSeeder
import com.harun.splitledger.data.mapper.toDomain
import com.harun.splitledger.data.mapper.toEntity
import com.harun.splitledger.domain.model.Participant
import com.harun.splitledger.domain.model.Trip
import com.harun.splitledger.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TripRepositoryImpl @Inject constructor(
    private val tripDao: TripDao,
    private val demoTripSeeder: DemoTripSeeder
): TripRepository {
    override fun observeTrips(): Flow<List<Trip>> {
        return tripDao.observeTrips().map { data ->
            data.map { it.toDomain() }
        }
    }

    override fun observeSingleTrip(tripId: String): Flow<Trip?> {
        return tripDao.observeTrip(tripId).map { it?.toDomain() }
    }

    override suspend fun getTripById(tripId: String): Trip? {
        return tripDao.getTripById(tripId)?.toDomain()
    }

    override fun observeParticipants(tripId: String): Flow<List<Participant>> {
        return tripDao.observeParticipants(tripId).map { data ->
            data.map { it.toDomain() }
        }
    }

    override suspend fun getParticipants(tripId: String): List<Participant> {
        return tripDao.getParticipants(tripId).map { it.toDomain() }
    }

    override suspend fun upsertTrip(trip: Trip) {
        tripDao.upsertTrip(trip.toEntity())
    }

    override suspend fun setTripSettled(tripId: String, isSettled: Boolean) {
        tripDao.setTripSettled(tripId, isSettled)
    }

    override suspend fun deleteTrip(tripId: String) {
        tripDao.deleteTrip(tripId)
    }

    override suspend fun upsertParticipant(participant: Participant) {
        tripDao.upsertParticipant(participant.toEntity())
    }

    override suspend fun deleteParticipant(participantId: String) {
        tripDao.deleteParticipant(participantId)
    }

    override suspend fun resetTripDemo() {
        demoTripSeeder.resetDemoTrip()
    }
}