package com.harun.splitledger.domain.math

import com.harun.splitledger.domain.model.Expense
import com.harun.splitledger.domain.model.Participant
import com.harun.splitledger.domain.model.ParticipantBalance
import com.harun.splitledger.domain.model.Settlement

object BalanceCalculator {
    fun calculate(
        participants: List<Participant>,
        expenses: List<Expense>,
        settlements: List<Settlement>
    ): List<ParticipantBalance> {
        val paid = mutableMapOf<String, Long>()
        val owed = mutableMapOf<String, Long>()
        val settlementNet = mutableMapOf<String, Long>()

        expenses.forEach { expense ->
            paid[expense.payerId] = (paid[expense.payerId] ?: 0L) + expense.homeAmountMinor
            expense.splits.forEach { split ->
                owed[split.participantId] = (owed[split.participantId] ?: 0L) + split.homeShareMinor
            }
        }

        settlements.forEach { settlement ->
            val from = settlement.fromParticipantId
            val to = settlement.toParticipantId

            settlementNet[from] = (settlementNet[from] ?: 0L) + settlement.amountHomeMinor
            settlementNet[to] = (settlementNet[to] ?: 0L) - settlement.amountHomeMinor
        }

        return participants.map { participant ->
            val paidAmount = paid[participant.id] ?: 0L
            val owedAmount = owed[participant.id] ?: 0L
            val netAmount = paidAmount - owedAmount + (settlementNet[participant.id] ?: 0L)
            ParticipantBalance(
                participant = participant,
                totalPaidHomeMinor = paidAmount,
                totalOwedHomeMinor = owedAmount,
                netHomeMinor = netAmount
            )
        }
    }
}