package com.harun.splitledger.data.local.seed

import com.harun.splitledger.data.local.db.entity.FxRateEntity

object BundledFxRates {
    const val BUNDLED_TIMESTAMP_EPOCH_MS = 1735689600000L

    val eurBaseRates: Map<String, String> = mapOf(
        "EUR" to "1.0000",
        "USD" to "1.0951",
        "CAD" to "1.563",
        "SGD" to "1.4139",
        "AUD" to "1.6576",
        "HKD" to "8.5915",
        "NZD" to "2.0017",
        "MXN" to "21.441",
        "BRL" to "6.4504",
        "JPY" to "174.48",
        "CNY" to "7.3019",
        "GBP" to "0.83654",
        "CHF" to "0.91395",
        "KRW" to "1471.16",
        "THB" to "37.291",
        "IDR" to "20031",
        "MYR" to "4.5086",
        "INR" to "101.21",
        "PHP" to "69.732",
        "SEK" to "11.2337",
    )

    fun toEntities(updatedAtEpochMs: Long = BUNDLED_TIMESTAMP_EPOCH_MS): List<FxRateEntity> =
        eurBaseRates.map { (quoteCurrency, rateDecimal) ->
            FxRateEntity(
                quoteCurrency = quoteCurrency,
                baseCurrency = "EUR",
                rateDecimal = rateDecimal,
                updatedAtEpochMs = updatedAtEpochMs,
                isBundledFallback = true
            )
        }
}