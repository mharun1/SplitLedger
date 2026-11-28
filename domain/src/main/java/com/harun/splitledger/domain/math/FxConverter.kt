package com.harun.splitledger.domain.math

import com.harun.splitledger.domain.model.CurrencyCatalog
import com.harun.splitledger.domain.model.DomainError
import com.harun.splitledger.domain.model.DomainException
import java.math.BigDecimal
import java.math.RoundingMode

object FxConverter {
    private const val RATE_SCALE = 10

    fun resolveRate(fromCurrency: String, toCurrency: String, eurRates: Map<String, BigDecimal>): BigDecimal? {
        val from = fromCurrency.uppercase()
        val to = toCurrency.uppercase()

        if (from == to) return BigDecimal.ONE

        val eurToFrom = if (from == "EUR") BigDecimal.ONE else eurRates[from]
        val eurToTo = if (to == "EUR") BigDecimal.ONE else eurRates[to]

        if (eurToFrom == null || eurToTo == null) return null
        if (eurToFrom <= BigDecimal.ZERO || eurToTo <= BigDecimal.ZERO) return null

        return eurToTo.divide(eurToFrom, RATE_SCALE, RoundingMode.HALF_UP)
    }
    fun convertMinor(amountMinor: Long, fromCurrency: String, toCurrency: String, rate: BigDecimal): Long {
        if (rate <= BigDecimal.ZERO) throw DomainException(DomainError.InvalidFxRate)

        if (fromCurrency.equals(toCurrency, ignoreCase = true) && rate.compareTo(BigDecimal.ONE) == 0) {
            return amountMinor
        }

        val fromDigits = CurrencyCatalog.minorUnitDigits(fromCurrency)
        val toDigits = CurrencyCatalog.minorUnitDigits(toCurrency)

        val majorFrom = BigDecimal.valueOf(amountMinor).movePointLeft(fromDigits)
        val majorTo = majorFrom.multiply(rate)
        return majorTo
            .movePointRight(toDigits)
            .setScale(0, RoundingMode.HALF_UP)
            .longValueExact()
    }
}