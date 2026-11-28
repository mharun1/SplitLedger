package com.harun.splitledger.domain.math

import com.harun.splitledger.domain.model.Expense
import com.harun.splitledger.domain.model.ExpenseCategory
import com.harun.splitledger.domain.model.ExpenseSplit
import com.harun.splitledger.domain.model.Participant
import com.harun.splitledger.domain.model.RateSource
import com.harun.splitledger.domain.model.Settlement
import com.harun.splitledger.domain.model.SplitMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal

class BalanceAndSettlementTest {

    private val alice = Participant("p1", "trip-1", "Alice", 0)
    private val bob = Participant("p2", "trip-1", "Bob", 1)
    private val charlie = Participant("p3", "trip-1", "Charlie", 2)
    private val dave = Participant("p4", "trip-1", "Dave", 3)

    private fun expense(
        id: String,
        payerId: String,
        homeAmountMinor: Long,
        splits: List<Pair<String, Long>>
    ) = Expense(
        id = id,
        tripId = "trip-1",
        title = id,
        category = ExpenseCategory.FOOD,
        amountMinor = homeAmountMinor,
        currencyCode = "USD",
        homeAmountMinor = homeAmountMinor,
        fxRateDecimal = BigDecimal.ONE,
        rateSource = RateSource.LIVE,
        payerId = payerId,
        splitMode = SplitMode.EXACT,
        createdAtEpochMs = 1_000L,
        splits = splits.map { (pid, share) ->
            ExpenseSplit(pid, share, share, 0)
        }
    )

    @Test
    fun `4-person Tokyo trip sums net balances to zero and optimizer zeros everyone in at most N-1 transfers`() {
        val participants = listOf(alice, bob, charlie, dave)
        val expenses = listOf(
            expense("e1", alice.id, 12_000L, listOf(alice.id to 3_000L, bob.id to 3_000L, charlie.id to 3_000L, dave.id to 3_000L)),
            expense("e2", bob.id, 4_000L, listOf(bob.id to 2_000L, charlie.id to 2_000L)),
            expense("e3", dave.id, 2_000L, listOf(alice.id to 1_000L, dave.id to 1_000L))
        )

        val balances = BalanceCalculator.calculate(participants, expenses, emptyList())
        assertEquals(0L, balances.sumOf { it.netHomeMinor })

        val suggestions = SettlementOptimizer.optimize(balances)
        assertTrue(suggestions.size <= participants.size - 1)

        val appliedSettlements = suggestions.mapIndexed { idx, s ->
            Settlement("s$idx", "trip-1", s.fromParticipant.id, s.toParticipant.id, s.amountHomeMinor, 2_000L)
        }
        val after = BalanceCalculator.calculate(participants, expenses, appliedSettlements)
        assertTrue(after.all { it.netHomeMinor == 0L })
    }

    @Test
    fun `partial settlement, overpayment flip, and zero-balance pruning`() {
        // Bob owes Alice $50 (5000); Charlie is $0 (settled)
        val participants = listOf(alice, bob, charlie)
        val expenses = listOf(
            expense("e1", alice.id, 10_000L, listOf(alice.id to 5_000L, bob.id to 5_000L))
        )

        // Zero-balance Charlie emits no phantom transfer
        val initial = BalanceCalculator.calculate(participants, expenses, emptyList())
        val initialPlan = SettlementOptimizer.optimize(initial)
        assertEquals(1, initialPlan.size)
        assertEquals(bob, initialPlan[0].fromParticipant)
        assertEquals(alice, initialPlan[0].toParticipant)
        assertEquals(5_000L, initialPlan[0].amountHomeMinor)

        // Partial settlement: Bob pays $20 -> owes $30
        val partial = BalanceCalculator.calculate(
            participants,
            expenses,
            listOf(Settlement("s1", "trip-1", bob.id, alice.id, 2_000L, 2_000L))
        )
        val partialPlan = SettlementOptimizer.optimize(partial)
        assertEquals(1, partialPlan.size)
        assertEquals(bob, partialPlan[0].fromParticipant)
        assertEquals(alice, partialPlan[0].toParticipant)
        assertEquals(3_000L, partialPlan[0].amountHomeMinor)

        // Overpayment: Bob pays $60 -> direction flips, Alice owes Bob $10
        val overpaid = BalanceCalculator.calculate(
            participants,
            expenses,
            listOf(Settlement("s2", "trip-1", bob.id, alice.id, 6_000L, 2_000L))
        )
        val overpaidPlan = SettlementOptimizer.optimize(overpaid)
        assertEquals(1, overpaidPlan.size)
        assertEquals(alice, overpaidPlan[0].fromParticipant)
        assertEquals(bob, overpaidPlan[0].toParticipant)
        assertEquals(1_000L, overpaidPlan[0].amountHomeMinor)
    }
}
