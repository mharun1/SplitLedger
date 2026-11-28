package com.harun.splitledger.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fx_rate")
data class FxRateEntity(
    @PrimaryKey
    val quoteCurrency: String,
    val baseCurrency: String,
    val rateDecimal: String,
    val updatedAtEpochMs: Long,
    val isBundledFallback: Boolean
)