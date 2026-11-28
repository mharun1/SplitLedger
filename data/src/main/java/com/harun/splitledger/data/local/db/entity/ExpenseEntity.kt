package com.harun.splitledger.data.local.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.harun.splitledger.domain.model.RateSource
import com.harun.splitledger.domain.model.SplitMode

@Entity(
    tableName = "expenses",
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
            childColumns = arrayOf("payerId"),
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("tripId"), Index("payerId")]
)
data class ExpenseEntity (
    @PrimaryKey
    val id: String,
    val tripId: String,
    val payerId: String,
    val title: String,
    val category: String,
    val amountMinor: Long,
    val currencyCode: String,
    val homeAmountMinor: Long,
    val fxRateDecimal: String,
    val rateSource: RateSource,
    val splitMode: SplitMode,
    val createdAtEpochMs: Long
)
