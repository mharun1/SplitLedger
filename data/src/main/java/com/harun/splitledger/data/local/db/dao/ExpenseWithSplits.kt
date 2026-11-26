package com.harun.splitledger.data.local.db.dao

import androidx.room.Embedded
import androidx.room.Relation
import com.harun.splitledger.data.local.db.entity.ExpenseEntity
import com.harun.splitledger.data.local.db.entity.ExpenseSplitEntity

data class ExpenseWithSplits(
    @Embedded val expense: ExpenseEntity,
    @Relation(parentColumn = "id", entityColumn = "expenseId") val splits: List<ExpenseSplitEntity>
)
