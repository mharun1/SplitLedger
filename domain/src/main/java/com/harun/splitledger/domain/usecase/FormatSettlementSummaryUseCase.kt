package com.harun.splitledger.domain.usecase

import com.harun.splitledger.domain.model.CurrencyCatalog
import com.harun.splitledger.domain.model.ParticipantBalance
import com.harun.splitledger.domain.model.TransferSuggestion
import com.harun.splitledger.domain.model.Trip
import java.math.BigDecimal
import javax.inject.Inject

class FormatSettlementSummaryUseCase @Inject constructor() {
    operator fun invoke(
        trip: Trip,
        totalSpendHomeMinor: Long,
        balances: List<ParticipantBalance>,
        suggestions: List<TransferSuggestion>
    ): String {
        return buildString {
            appendLine("${trip.name} - Total Spend: ${formatMoney(totalSpendHomeMinor, trip.homeCurrency)}")
            appendLine("Balance: ")
            balances.forEach { balance ->
                appendLine("- ${balance.participant.name} ${formatMoney(balance.netHomeMinor, trip.homeCurrency, true)}.")
            }

            appendLine("Transfers: ")
            if (suggestions.isEmpty()) {
                appendLine("All settled up!")
            } else {
                suggestions.forEach { suggestion ->
                    appendLine("- ${suggestion.fromParticipant.name} -> ${suggestion.toParticipant.name}: ${formatMoney(suggestion.amountHomeMinor, trip.homeCurrency)}")
                }
            }
        }.trimEnd()
    }

    private fun formatMoney(
        amountMinor: Long,
        currencyCode: String,
        signed: Boolean = false
    ): String {
        val currency = CurrencyCatalog.findByCode(currencyCode)

        val symbol = currency?.symbol ?: "$currencyCode "
        val digits = currency?.minorUnitDigits ?: 2
        val prefix =
            if (amountMinor < 0L) { "-" }
            else if (signed && amountMinor > 0L) { "+" }
            else { "" }

        val formatted = BigDecimal.valueOf(amountMinor)
            .abs()
            .movePointLeft(digits)
            .setScale(digits)
            .toPlainString()

        return "$prefix$symbol$formatted"
    }
}