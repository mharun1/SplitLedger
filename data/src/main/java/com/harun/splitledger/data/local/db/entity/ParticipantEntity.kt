package com.harun.splitledger.data.local.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "participants",
    foreignKeys = [ForeignKey(
        entity = TripEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("tripId"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("tripId")]
)
data class ParticipantEntity(
    @PrimaryKey
    val id: String,
    val tripId: String,
    val name: String,
    val avatarColorIndex: Int,
)