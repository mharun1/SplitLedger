package com.harun.splitledger.data.repository

import com.harun.splitledger.data.local.db.dao.ExpenseDao
import com.harun.splitledger.data.mapper.toDomain
import com.harun.splitledger.data.mapper.toEntity
import com.harun.splitledger.domain.model.Expense
import com.harun.splitledger.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao
): ExpenseRepository {
    override fun observeExpenses(tripId: String): Flow<List<Expense>> {
        return expenseDao.observeExpenseWithSplit(tripId).map { data ->
            data.map { it.toDomain() }
        }
    }

    override suspend fun getExpense(expenseId: String): Expense? {
        return expenseDao.getExpenseWithSplits(expenseId)?.toDomain()
    }

    override suspend fun upsertExpense(expense: Expense) {
        expenseDao.upsertExpenseWithSplits(
            expense.toEntity(),
            expense.splits.map { it.toEntity(expense.id) }
        )
    }

    override suspend fun deleteExpense(expenseId: String) {
        expenseDao.deleteExpense(expenseId)
    }

    override suspend fun isParticipantUseByExpense(participantId: String): Boolean {
        return expenseDao.isParticipantUsedByExpense(participantId)
    }
}