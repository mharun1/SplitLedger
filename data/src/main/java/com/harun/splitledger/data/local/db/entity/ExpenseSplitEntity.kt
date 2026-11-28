package com.harun.splitledger.data.local.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "expense_split",
    foreignKeys = [
        ForeignKey(
            entity = ExpenseEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("expenseId"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ParticipantEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("participantId"),
            onDelete = ForeignKey.RESTRICT
        )
    ],
    primaryKeys = ["expenseId", "participantId"],
    indices = [Index("expenseId"), Index("participantId")]
)
data class ExpenseSplitEntity(
    val expenseId: String,
    val participantId: String,
    val originalShareMinor: Long,
    val homeShareMinor: Long,
    val percentBps: Int,
)
