package com.harun.splitledger.domain.model

data class SupportedCurrency (
    val code: String,
    val symbol: String,
    val name: String,
    val minorUnitDigits: Int
)

object CurrencyCatalog {
    val currencies = listOf(
        SupportedCurrency("USD", "\u0024", "US Dollar", 2),
        SupportedCurrency("SGD", "\u0024", "Singapore Dollar", 2),
        SupportedCurrency("CAD", "CA\u0024", "Canada Dollar", 2),
        SupportedCurrency("AUD", "\u0024", "Australian Dollar", 2),
        SupportedCurrency("HKD", "HK\u0024", "HongKong Dollar", 2),
        SupportedCurrency("NZD", "NZ\u0024", "New Zealand Dollar", 2),
        SupportedCurrency("MXN", "Mex\u0024", "Mexican Peso", 2),
        SupportedCurrency("BRL", "R\u0024", "Brazilian Real", 2),
        SupportedCurrency("EUR", "\u20AC", "Euro", 2),
        SupportedCurrency("JPY", "\u00A5", "Japanese Yen", 0),
        SupportedCurrency("CNY", "\u00A5", "Chinese Yuan", 2),
        SupportedCurrency("GBP", "\u00A3", "British Pound-sterling", 2),
        SupportedCurrency("CHF", "Fr.", "Swiss Franc", 2),
        SupportedCurrency("KRW", "\u20A9", "South Korean Won", 0),
        SupportedCurrency("THB", "\u0E3F", "Thai Baht", 2),
        SupportedCurrency("IDR", "Rp.", "Indonesian Rupiah", 0),
        SupportedCurrency("MYR", "RM", "Malaysian Ringgit", 2),
        SupportedCurrency("INR", "\u20B9", "Indian Rupee", 2),
        SupportedCurrency("PHP", "\u20B1", "Philipine Peso", 2),
        SupportedCurrency("SEK", "Kr.", "Swedish Krona", 2),
    )

    private val byCodeMap: Map<String, SupportedCurrency> = currencies.associateBy { it.code }

    fun findByCode(code: String): SupportedCurrency? = byCodeMap[code.uppercase()]
    fun minorUnitDigits(code: String): Int = findByCode(code)?.minorUnitDigits ?: 2
}