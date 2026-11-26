package com.harun.splitledger.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val homeCurrency: String,
    val isSettled: Boolean,
    val isDemo: Boolean,
    val createdAtEpochMs: Long
)