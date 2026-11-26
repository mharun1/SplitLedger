package com.harun.splitledger.data.local.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "settlements",
    foreignKeys = [
        ForeignKey(
            entity = TripEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("tripId"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ParticipantEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("fromParticipantId"),
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = ParticipantEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("toParticipantId"),
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("tripId"), Index("fromParticipantId"), Index("toParticipantId")]
)
data class SettlementEntity(
    @PrimaryKey
    val id: String,
    val tripId: String,
    val fromParticipantId: String,
    val toParticipantId: String,
    val amountHomeMinor: Long,
    val createdAtEpochMs: Long
)