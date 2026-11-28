package com.harun.splitledger.domain.repository

import com.harun.splitledger.domain.model.Expense
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun observeExpenses(tripId: String): Flow<List<Expense>>
    suspend fun getExpense(expenseId: String): Expense?
    suspend fun upsertExpense(expense: Expense)
    suspend fun deleteExpense(expenseId: String)
    suspend fun isParticipantUseByExpense(participantId: String): Boolean
}