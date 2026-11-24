package com.harun.splitledger.domain.math

import com.harun.splitledger.domain.model.Participant
import com.harun.splitledger.domain.model.ParticipantBalance
import com.harun.splitledger.domain.model.TransferSuggestion
import java.util.PriorityQueue

object SettlementOptimizer {
    private data class TempBalance(val participant: Participant, val remaining: Long)

    fun optimize(balances: List<ParticipantBalance>): List<TransferSuggestion> {
        val comparator = compareByDescending<TempBalance> { it.remaining }.thenBy { it.participant.id }

        val debtors = PriorityQueue(comparator)
        val creditors = PriorityQueue(comparator)
        val suggestions = mutableListOf<TransferSuggestion>()

        balances.forEach { balance ->
            if (balance.netHomeMinor < 0L)
                debtors.add(TempBalance(balance.participant, -balance.netHomeMinor))
            else if (balance.netHomeMinor > 0L)
                creditors.add(TempBalance(balance.participant, balance.netHomeMinor))
        }

        while (debtors.isNotEmpty() && creditors.isNotEmpty()) {
            val debtor = debtors.remove()
            val creditor = creditors.remove()

            val transferAmount = minOf(debtor.remaining, creditor.remaining)

            suggestions.add(
                TransferSuggestion(
                    fromParticipant = debtor.participant,
                    toParticipant = creditor.participant,
                    amountHomeMinor = transferAmount
                )
            )

            val debtorRemains = debtor.remaining - transferAmount
            val creditorRemains = creditor.remaining - transferAmount

            if (debtorRemains > 0L) debtors.add(debtor.copy(remaining = debtorRemains))
            if (creditorRemains > 0L) creditors.add(creditor.copy(remaining = creditorRemains))
        }

        return suggestions
    }
}