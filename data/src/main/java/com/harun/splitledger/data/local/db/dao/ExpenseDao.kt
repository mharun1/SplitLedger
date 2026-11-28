package com.harun.splitledger.data.local.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.harun.splitledger.data.local.db.entity.ExpenseEntity
import com.harun.splitledger.data.local.db.entity.ExpenseSplitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Transaction
    @Query("SELECT * FROM expenses WHERE tripId = :tripId ORDER BY createdAtEpochMs DESC")
    fun observeExpenseWithSplit(tripId: String): Flow<List<ExpenseWithSplits>>

    @Transaction
    @Query("SELECT * FROM expenses WHERE id = :expenseId")
    suspend fun getExpenseWithSplits(expenseId: String): ExpenseWithSplits?

    @Upsert
    suspend fun upsertExpense(expense: ExpenseEntity)

    @Query("DELETE FROM expense_split WHERE expenseId = :expenseId")
    suspend fun deleteSplitsWithExpense(expenseId: String)

    @Upsert
    suspend fun upsertSplits(splits: List<ExpenseSplitEntity>)

    @Query("DELETE FROM expenses WHERE id = :expenseId")
    suspend fun deleteExpense(expenseId: String)

    @Query(
        "SELECT EXISTS(SELECT payerId FROM expenses WHERE payerId = :participantId)" +
            " OR EXISTS(SELECT participantId FROM expense_split WHERE participantId = :participantId)"
    )
    suspend fun isParticipantUsedByExpense(participantId: String): Boolean

    @Transaction
    suspend fun upsertExpenseWithSplits(
        expense: ExpenseEntity,
        splits: List<ExpenseSplitEntity>,
    ) {
        upsertExpense(expense)
        deleteSplitsWithExpense(expense.id)
        upsertSplits(splits)
    }
}