package com.harun.splitledger.domain.usecase

import com.harun.splitledger.domain.model.DomainError
import com.harun.splitledger.domain.model.DomainException
import com.harun.splitledger.domain.model.Expense
import com.harun.splitledger.domain.model.ExpenseCategory
import com.harun.splitledger.domain.model.ExpenseSplit
import com.harun.splitledger.domain.model.FxRate
import com.harun.splitledger.domain.model.Participant
import com.harun.splitledger.domain.model.ParticipantBalance
import com.harun.splitledger.domain.model.RateSource
import com.harun.splitledger.domain.model.Settlement
import com.harun.splitledger.domain.model.SplitMode
import com.harun.splitledger.domain.model.TransferSuggestion
import com.harun.splitledger.domain.model.Trip
import com.harun.splitledger.domain.repository.ExpenseRepository
import com.harun.splitledger.domain.repository.FxRateRepository
import com.harun.splitledger.domain.repository.SettlementRepository
import com.harun.splitledger.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal

class DomainUseCasesTest {

    private fun domainErrorOf(result: Result<*>): DomainError =
        (result.exceptionOrNull() as DomainException).error

    // --- DeleteParticipantUseCase ---

    @Test
    fun `DeleteParticipantUseCase blocks payer, split member, and settlement participant`() = runTest {
        val tripRepo = FakeTripRepository()
        val expenseRepo = FakeExpenseRepository().apply {
            expenses.add(
                Expense(
                    id = "e1",
                    tripId = "t1",
                    title = "Ramen",
                    category = ExpenseCategory.FOOD,
                    amountMinor = 2_000L,
                    currencyCode = "USD",
                    homeAmountMinor = 2_000L,
                    fxRateDecimal = BigDecimal.ONE,
                    rateSource = RateSource.LIVE,
                    payerId = "payer",
                    splitMode = SplitMode.EQUAL,
                    createdAtEpochMs = 1_000L,
                    splits = listOf(ExpenseSplit("splitMember", 2_000L, 2_000L, 10_000))
                )
            )
        }
        val settlementRepo = FakeSettlementRepository().apply {
            settlements.add(Settlement("s1", "t1", "sender", "receiver", 500L, 1_000L))
        }
        val useCase = DeleteParticipantUseCase(tripRepo, expenseRepo, settlementRepo)

        for (blockedId in listOf("payer", "splitMember", "sender", "receiver")) {
            val result = useCase(blockedId)
            assertTrue("Expected failure for $blockedId", result.isFailure)
            assertEquals(DomainError.ParticipantInUse, domainErrorOf(result))
        }
        assertTrue(tripRepo.deletedParticipantIds.isEmpty())
    }

    @Test
    fun `DeleteParticipantUseCase deletes unreferenced participant cleanly`() = runTest {
        val tripRepo = FakeTripRepository()
        val useCase = DeleteParticipantUseCase(tripRepo, FakeExpenseRepository(), FakeSettlementRepository())

        val result = useCase("unused")
        assertTrue(result.isSuccess)
        assertEquals(listOf("unused"), tripRepo.deletedParticipantIds)
    }

    // --- SaveExpenseUseCase ---

    @Test
    fun `SaveExpenseUseCase rejects invalid amount, rate, missing fx, and split mismatch`() = runTest {
        val expenseRepo = FakeExpenseRepository()
        val fxRepo = FakeFxRateRepository(emptyList())
        val useCase = SaveExpenseUseCase(expenseRepo, fxRepo)

        val baseParams = SaveExpenseUseCase.Params(
            tripId = "t1",
            title = "Dinner",
            category = ExpenseCategory.FOOD,
            amountMinor = 1_000L,
            currencyCode = "USD",
            homeCurrency = "USD",
            payerId = "p1",
            splitMode = SplitMode.EQUAL,
            participantsId = listOf("p1", "p2")
        )

        assertEquals(DomainError.InvalidAmount, domainErrorOf(useCase(baseParams.copy(amountMinor = 0L))))
        assertEquals(
            DomainError.InvalidFxRate,
            domainErrorOf(useCase(baseParams.copy(manualFxRate = BigDecimal.ZERO)))
        )
        assertEquals(
            DomainError.MissingFxRate("JPY", "USD"),
            domainErrorOf(useCase(baseParams.copy(currencyCode = "JPY")))
        )
        assertEquals(
            DomainError.ExactSplitMismatch(expectedMinor = 1_000L, actualMinor = 900L),
            domainErrorOf(
                useCase(
                    baseParams.copy(
                        splitMode = SplitMode.EXACT,
                        exactMinorByParticipant = mapOf("p1" to 400L, "p2" to 500L)
                    )
                )
            )
        )
        assertTrue(expenseRepo.expenses.isEmpty())
    }

    @Test
    fun `SaveExpenseUseCase converts FX, reconciles splits, and sets rate source`() = runTest {
        val expenseRepo = FakeExpenseRepository()
        val fxRepo = FakeFxRateRepository(
            rates = listOf(
                FxRate("USD", "EUR", BigDecimal("1.08"), 1_000L, isBundledFallback = false),
                FxRate("JPY", "EUR", BigDecimal("162.0"), 1_000L, isBundledFallback = false)
            ),
            stale = true
        )
        val useCase = SaveExpenseUseCase(expenseRepo, fxRepo)

        // ¥10,000 -> EUR/USD cross-rate (1.08 / 162.0 = 0.0066666667) -> $66.67 (6667 cents)
        val result = useCase(
            SaveExpenseUseCase.Params(
                tripId = "t1",
                title = "  Sushi  ",
                category = ExpenseCategory.FOOD,
                amountMinor = 10_000L,
                currencyCode = "jpy",
                homeCurrency = "USD",
                payerId = "p1",
                splitMode = SplitMode.EQUAL,
                participantsId = listOf("p1", "p2", "p3"),
                expenseId = "exp-1",
                createdAtEpochMs = 2_000L
            )
        )

        assertTrue(result.isSuccess)
        val saved = result.getOrThrow()
        assertEquals("Sushi", saved.title)
        assertEquals("JPY", saved.currencyCode)
        assertEquals(RateSource.CACHED, saved.rateSource)
        assertEquals(6_667L, saved.homeAmountMinor)
        assertEquals(10_000L, saved.splits.sumOf { it.originalShareMinor })
        assertEquals(saved.homeAmountMinor, saved.splits.sumOf { it.homeShareMinor })
        assertEquals(listOf(saved), expenseRepo.expenses)
    }

    // --- RecordSettlementUseCase ---

    @Test
    fun `RecordSettlementUseCase validates inputs and persists valid settlement`() = runTest {
        val settlementRepo = FakeSettlementRepository()
        val useCase = RecordSettlementUseCase(settlementRepo)

        assertEquals(
            DomainError.InvalidAmount,
            domainErrorOf(useCase("t1", "p1", "p2", 0L))
        )
        assertEquals(
            DomainError.SameSettlementParticipant,
            domainErrorOf(useCase("t1", "p1", "p1", 500L))
        )
        assertTrue(settlementRepo.settlements.isEmpty())

        val ok = useCase("t1", "p1", "p2", 2_500L, id = "s-1", createdAtEpochMs = 5_000L)
        assertTrue(ok.isSuccess)
        assertEquals(1, settlementRepo.settlements.size)
        assertEquals(2_500L, ok.getOrThrow().amountHomeMinor)
    }

    // --- FormatSettlementSummaryUseCase ---

    @Test
    fun `FormatSettlementSummaryUseCase formats unsettled and settled trips cleanly`() {
        val useCase = FormatSettlementSummaryUseCase()
        val trip = Trip("t1", "Tokyo Trip", "USD", isSettled = false, isDemo = false, createdAtEpochMs = 1_000L)
        val alice = Participant("p1", "t1", "Alice", 0)
        val bob = Participant("p2", "t1", "Bob", 1)

        val unsettledText = useCase(
            trip = trip,
            totalSpendHomeMinor = 10_000L,
            balances = listOf(
                ParticipantBalance(alice, 10_000L, 5_000L, 5_000L),
                ParticipantBalance(bob, 0L, 5_000L, -5_000L)
            ),
            suggestions = listOf(TransferSuggestion(bob, alice, 5_000L))
        )
        assertTrue(unsettledText.contains("Tokyo Trip"))
        assertTrue(unsettledText.contains("$100.00"))
        assertTrue(unsettledText.contains("Alice"))
        assertTrue(unsettledText.contains("+$50.00"))
        assertTrue(unsettledText.contains("-$50.00"))
        assertTrue(unsettledText.contains("Bob -> Alice: $50.00"))

        val settledText = useCase(
            trip = trip.copy(isSettled = true),
            totalSpendHomeMinor = 10_000L,
            balances = listOf(
                ParticipantBalance(alice, 10_000L, 5_000L, 0L),
                ParticipantBalance(bob, 0L, 5_000L, 0L)
            ),
            suggestions = emptyList()
        )
        assertTrue(settledText.contains("Tokyo Trip"))
        assertTrue(settledText.contains("$100.00"))
        assertTrue(settledText.contains("All settled up!"))
    }

    // --- Minimal In-Memory Fakes ---

    private class FakeTripRepository : TripRepository {
        val deletedParticipantIds = mutableListOf<String>()
        override fun observeTrips(): Flow<List<Trip>> = emptyFlow()
        override fun observeSingleTrip(tripId: String): Flow<Trip?> = emptyFlow()
        override suspend fun getTripById(tripId: String): Trip? = null
        override fun observeParticipants(tripId: String): Flow<List<Participant>> = emptyFlow()
        override suspend fun getParticipants(tripId: String): List<Participant> = emptyList()
        override suspend fun upsertTrip(trip: Trip) = Unit
        override suspend fun setTripSettled(tripId: String, isSettled: Boolean) = Unit
        override suspend fun deleteTrip(tripId: String) = Unit
        override suspend fun upsertParticipant(participant: Participant) = Unit
        override suspend fun deleteParticipant(participantId: String) {
            deletedParticipantIds.add(participantId)
        }
        override suspend fun resetTripDemo() = Unit
    }

    private class FakeExpenseRepository : ExpenseRepository {
        val expenses = mutableListOf<Expense>()
        override fun observeExpenses(tripId: String): Flow<List<Expense>> = emptyFlow()
        override suspend fun getExpense(expenseId: String): Expense? = expenses.find { it.id == expenseId }
        override suspend fun upsertExpense(expense: Expense) {
            expenses.removeAll { it.id == expense.id }
            expenses.add(expense)
        }
        override suspend fun deleteExpense(expenseId: String) {
            expenses.removeAll { it.id == expenseId }
        }
        override suspend fun isParticipantUseByExpense(participantId: String): Boolean =
            expenses.any { e -> e.payerId == participantId || e.splits.any { it.participantId == participantId } }
    }

    private class FakeSettlementRepository : SettlementRepository {
        val settlements = mutableListOf<Settlement>()
        override fun observeSettlements(tripId: String): Flow<List<Settlement>> = emptyFlow()
        override suspend fun saveSettlement(settlement: Settlement) {
            settlements.add(settlement)
        }
        override suspend fun deleteSettlement(settlementId: String) {
            settlements.removeAll { it.id == settlementId }
        }
        override suspend fun isParticipantUseBySettlement(participantId: String): Boolean =
            settlements.any { it.fromParticipantId == participantId || it.toParticipantId == participantId }
    }

    private class FakeFxRateRepository(
        private val rates: List<FxRate>,
        private val stale: Boolean = false
    ) : FxRateRepository {
        override fun observeRates(): Flow<List<FxRate>> = emptyFlow()
        override suspend fun getRates(): List<FxRate> = rates
        override fun isRateStale(rate: FxRate, nowEpochMs: Long): Boolean = stale
    }
}
