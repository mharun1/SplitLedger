package com.harun.splitledger.domain.usecase

import com.harun.splitledger.domain.model.DomainError
import com.harun.splitledger.domain.model.DomainException
import com.harun.splitledger.domain.repository.ExpenseRepository
import com.harun.splitledger.domain.repository.SettlementRepository
import com.harun.splitledger.domain.repository.TripRepository
import javax.inject.Inject

class DeleteParticipantUseCase @Inject constructor(
    private val tripRepository: TripRepository,
    private val expenseRepository: ExpenseRepository,
    private val settlementRepository: SettlementRepository
) {
    suspend operator fun invoke(participantId: String): Result<Unit> {
        return if (expenseRepository.isParticipantUseByExpense(participantId) || settlementRepository.isParticipantUseBySettlement(participantId)) {
            Result.failure(DomainException(DomainError.ParticipantInUse))
        } else {
            tripRepository.deleteParticipant(participantId)
            Result.success(Unit)
        }
    }
}