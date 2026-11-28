package com.harun.splitledger.domain.usecase

import com.harun.splitledger.domain.model.DomainError
import com.harun.splitledger.domain.model.DomainException
import com.harun.splitledger.domain.model.Settlement
import com.harun.splitledger.domain.repository.SettlementRepository
import java.util.UUID
import javax.inject.Inject

class RecordSettlementUseCase @Inject constructor(
    private val settlementRepository: SettlementRepository
) {
    suspend operator fun invoke(
        tripId: String,
        fromParticipantId: String,
        toParticipantId: String,
        amountHomeMinor: Long,
        id: String = UUID.randomUUID().toString(),
        createdAtEpochMs: Long = System.currentTimeMillis()
    ): Result<Settlement> {
        if (amountHomeMinor <= 0L)
            return Result.failure(DomainException(DomainError.InvalidAmount))
        if (fromParticipantId == toParticipantId)
            return Result.failure(DomainException(DomainError.SameSettlementParticipant))

        val settlement = Settlement(
            id = id,
            tripId = tripId,
            fromParticipantId = fromParticipantId,
            toParticipantId = toParticipantId,
            amountHomeMinor = amountHomeMinor,
            createdAtEpochMs = createdAtEpochMs
        )

        settlementRepository.saveSettlement(settlement)

        return Result.success(settlement)
    }
}