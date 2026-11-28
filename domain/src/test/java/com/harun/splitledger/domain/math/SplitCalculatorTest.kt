package com.harun.splitledger.domain.math

import com.harun.splitledger.domain.model.DomainError
import com.harun.splitledger.domain.model.DomainException
import com.harun.splitledger.domain.model.SplitMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SplitCalculatorTest {

    @Test
    fun `rejects non-positive amount or homeAmount`() {
        val zeroAmount = SplitCalculator.calculate(
            amountMinor = 0L,
            homeAmountMinor = 1000L,
            mode = SplitMode.EQUAL,
            participantIds = listOf("p1", "p2")
        )
        assertDomainError(zeroAmount, DomainError.InvalidAmount)

        val negativeAmount = SplitCalculator.calculate(
            amountMinor = -500L,
            homeAmountMinor = 1000L,
            mode = SplitMode.EQUAL,
            participantIds = listOf("p1", "p2")
        )
        assertDomainError(negativeAmount, DomainError.InvalidAmount)

        val zeroHomeAmount = SplitCalculator.calculate(
            amountMinor = 1000L,
            homeAmountMinor = 0L,
            mode = SplitMode.EQUAL,
            participantIds = listOf("p1", "p2")
        )
        assertDomainError(zeroHomeAmount, DomainError.InvalidAmount)
    }

    @Test
    fun `rejects empty participant list`() {
        val result = SplitCalculator.calculate(
            amountMinor = 1000L,
            homeAmountMinor = 1000L,
            mode = SplitMode.EQUAL,
            participantIds = emptyList()
        )
        assertDomainError(result, DomainError.EmptyParticipants)
    }

    @Test
    fun `equal split assigns remainder cents deterministically by sorted participant ID`() {
        // Review Focus #3: $10.00 / 3 -> 334, 333, 333 regardless of input order
        val result = SplitCalculator.calculate(
            amountMinor = 1_000L,
            homeAmountMinor = 1_000L,
            mode = SplitMode.EQUAL,
            participantIds = listOf("p3", "p1", "p2")
        )
        assertTrue(result.isSuccess)
        val splits = result.getOrThrow()

        assertEquals(listOf("p1", "p2", "p3"), splits.map { it.participantId })
        assertEquals(listOf(334L, 333L, 333L), splits.map { it.originalShareMinor })
        assertEquals(listOf(334L, 333L, 333L), splits.map { it.homeShareMinor })
        assertEquals(listOf(3334, 3333, 3333), splits.map { it.percentBps })
        assertEquals(1_000L, splits.sumOf { it.originalShareMinor })
        assertEquals(1_000L, splits.sumOf { it.homeShareMinor })
        assertEquals(10_000, splits.sumOf { it.percentBps })
    }

    @Test
    fun `cross-currency equal split reconciles both original and home totals strictly`() {
        // ¥10,000 / $67.00 home among 3 members
        val result = SplitCalculator.calculate(
            amountMinor = 10_000L,
            homeAmountMinor = 6_700L,
            mode = SplitMode.EQUAL,
            participantIds = listOf("sam", "alex", "maya")
        )
        assertTrue(result.isSuccess)
        val splits = result.getOrThrow()

        assertEquals(listOf("alex", "maya", "sam"), splits.map { it.participantId })
        assertEquals(listOf(3334L, 3333L, 3333L), splits.map { it.originalShareMinor })
        assertEquals(listOf(2234L, 2233L, 2233L), splits.map { it.homeShareMinor })
        assertEquals(10_000L, splits.sumOf { it.originalShareMinor })
        assertEquals(6_700L, splits.sumOf { it.homeShareMinor })
    }

    @Test
    fun `exact split rejects mismatched sum and negative share`() {
        val mismatch = SplitCalculator.calculate(
            amountMinor = 1_000L,
            homeAmountMinor = 1_000L,
            mode = SplitMode.EXACT,
            participantIds = listOf("p1", "p2"),
            exactMinorByParticipant = mapOf("p1" to 400L, "p2" to 500L)
        )
        assertDomainError(
            mismatch,
            DomainError.ExactSplitMismatch(expectedMinor = 1_000L, actualMinor = 900L)
        )

        val negativeShare = SplitCalculator.calculate(
            amountMinor = 1_000L,
            homeAmountMinor = 1_000L,
            mode = SplitMode.EXACT,
            participantIds = listOf("p1", "p2"),
            exactMinorByParticipant = mapOf("p1" to -100L, "p2" to 1_100L)
        )
        assertDomainError(negativeShare, DomainError.InvalidAmount)
    }

    @Test
    fun `exact split reconciles cross-currency home shares and respects zero share`() {
        val result = SplitCalculator.calculate(
            amountMinor = 10_000L,
            homeAmountMinor = 6_701L,
            mode = SplitMode.EXACT,
            participantIds = listOf("p3", "p1", "p2"),
            exactMinorByParticipant = mapOf("p1" to 0L, "p2" to 5_000L, "p3" to 5_000L)
        )
        assertTrue(result.isSuccess)
        val splits = result.getOrThrow()

        assertEquals(listOf("p1", "p2", "p3"), splits.map { it.participantId })
        assertEquals(listOf(0L, 5_000L, 5_000L), splits.map { it.originalShareMinor })
        // p1 has 0 original share, so remainder cent goes to p2, not p1
        assertEquals(listOf(0L, 3_351L, 3_350L), splits.map { it.homeShareMinor })
        assertEquals(10_000L, splits.sumOf { it.originalShareMinor })
        assertEquals(6_701L, splits.sumOf { it.homeShareMinor })
        assertEquals(10_000, splits.sumOf { it.percentBps })
    }

    @Test
    fun `percent split rejects sum not equal to 10000 bps`() {
        val under = SplitCalculator.calculate(
            amountMinor = 1_000L,
            homeAmountMinor = 1_000L,
            mode = SplitMode.PERCENT,
            participantIds = listOf("p1", "p2", "p3"),
            percentBpsByParticipant = mapOf("p1" to 3333, "p2" to 3333, "p3" to 3333)
        )
        assertDomainError(under, DomainError.PercentSplitMismatch(actualBps = 9999))

        val over = SplitCalculator.calculate(
            amountMinor = 1_000L,
            homeAmountMinor = 1_000L,
            mode = SplitMode.PERCENT,
            participantIds = listOf("p1", "p2", "p3"),
            percentBpsByParticipant = mapOf("p1" to 3334, "p2" to 3334, "p3" to 3333)
        )
        assertDomainError(over, DomainError.PercentSplitMismatch(actualBps = 10_001))
    }

    @Test
    fun `valid percent split reconciles strictly to original and home totals`() {
        val result = SplitCalculator.calculate(
            amountMinor = 10_000L,
            homeAmountMinor = 6_700L,
            mode = SplitMode.PERCENT,
            participantIds = listOf("p3", "p1", "p2"),
            percentBpsByParticipant = mapOf("p1" to 3334, "p2" to 3333, "p3" to 3333)
        )
        assertTrue(result.isSuccess)
        val splits = result.getOrThrow()

        assertEquals(listOf("p1", "p2", "p3"), splits.map { it.participantId })
        assertEquals(10_000L, splits.sumOf { it.originalShareMinor })
        assertEquals(6_700L, splits.sumOf { it.homeShareMinor })
        assertEquals(listOf(3334, 3333, 3333), splits.map { it.percentBps })
    }

    private fun assertDomainError(result: Result<*>, expected: DomainError) {
        assertTrue("Expected failure with $expected but was $result", result.isFailure)
        val ex = result.exceptionOrNull()
        assertTrue("Expected DomainException but got $ex", ex is DomainException)
        assertEquals(expected, (ex as DomainException).error)
    }
}
