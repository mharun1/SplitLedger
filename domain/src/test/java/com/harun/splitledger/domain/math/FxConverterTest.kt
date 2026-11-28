package com.harun.splitledger.domain.math

import com.harun.splitledger.domain.model.DomainError
import com.harun.splitledger.domain.model.DomainException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test
import java.math.BigDecimal
import java.math.RoundingMode

class FxConverterTest {

    private val sampleEurRates = mapOf(
        "USD" to BigDecimal("1.08"),
        "JPY" to BigDecimal("162.0"),
        "IDR" to BigDecimal("17000.0"),
        "GBP" to BigDecimal("0.85")
    )

    @Test
    fun `same currency returns rate 1 and unchanged minor units`() {
        val rate = FxConverter.resolveRate("USD", "usd", sampleEurRates)
        assertEquals(0, BigDecimal.ONE.compareTo(rate))

        val converted = FxConverter.convertMinor(
            amountMinor = 12345L,
            fromCurrency = "USD",
            toCurrency = "USD",
            rate = BigDecimal.ONE
        )
        assertEquals(12345L, converted)
    }

    @Test
    fun `zero-decimal JPY to 2-decimal USD scales minor units accurately`() {
        // Review Focus #1: ¥10,000 at 0.0067 = $67.00 (6700 cents, never 67 cents)
        val converted = FxConverter.convertMinor(
            amountMinor = 10_000L,
            fromCurrency = "JPY",
            toCurrency = "USD",
            rate = BigDecimal("0.0067")
        )
        assertEquals(6_700L, converted)
    }

    @Test
    fun `2-decimal USD to zero-decimal JPY scales minor units accurately`() {
        // Review Focus #1: $67.00 (6700 cents) at 149.25 = ¥9,999.75 -> rounds HALF_UP to ¥10,000
        val converted = FxConverter.convertMinor(
            amountMinor = 6_700L,
            fromCurrency = "USD",
            toCurrency = "JPY",
            rate = BigDecimal("149.25")
        )
        assertEquals(10_000L, converted)
    }

    @Test
    fun `non-EUR cross-rate JPY to USD matches eurToUsd divided by eurToJpy`() {
        val resolved = FxConverter.resolveRate("JPY", "USD", sampleEurRates)
        val expected = BigDecimal("1.08").divide(BigDecimal("162.0"), 10, RoundingMode.HALF_UP)

        assertEquals(0, expected.compareTo(resolved))
    }

    @Test
    fun `EUR base rate resolves directly in both directions`() {
        val eurToUsd = FxConverter.resolveRate("EUR", "USD", sampleEurRates)
        assertEquals(0, BigDecimal("1.08").compareTo(eurToUsd))

        val usdToEur = FxConverter.resolveRate("USD", "EUR", sampleEurRates)
        val expectedUsdToEur = BigDecimal.ONE.divide(BigDecimal("1.08"), 10, RoundingMode.HALF_UP)
        assertEquals(0, expectedUsdToEur.compareTo(usdToEur))
    }

    @Test
    fun `missing or non-positive rate in eurRates returns null`() {
        assertNull(FxConverter.resolveRate("USD", "XYZ", sampleEurRates))

        val badRates = mapOf("USD" to BigDecimal.ZERO, "JPY" to BigDecimal("-150"))
        assertNull(FxConverter.resolveRate("USD", "EUR", badRates))
        assertNull(FxConverter.resolveRate("EUR", "JPY", badRates))
    }

    @Test
    fun `convertMinor rejects zero or negative FX rate`() {
        val zeroEx = assertThrows(DomainException::class.java) {
            FxConverter.convertMinor(1000L, "USD", "EUR", BigDecimal.ZERO)
        }
        assertEquals(DomainError.InvalidFxRate, zeroEx.error)

        val negativeEx = assertThrows(DomainException::class.java) {
            FxConverter.convertMinor(1000L, "USD", "EUR", BigDecimal("-1.05"))
        }
        assertEquals(DomainError.InvalidFxRate, negativeEx.error)
    }
}
