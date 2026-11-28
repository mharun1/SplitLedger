package com.harun.splitledger.data.local

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.harun.splitledger.data.local.db.SplitLedgerDatabase
import com.harun.splitledger.data.local.db.entity.ExpenseEntity
import com.harun.splitledger.data.local.db.entity.ExpenseSplitEntity
import com.harun.splitledger.data.local.db.entity.FxRateEntity
import com.harun.splitledger.data.local.db.entity.ParticipantEntity
import com.harun.splitledger.data.local.db.entity.SettlementEntity
import com.harun.splitledger.data.local.db.entity.TripEntity
import com.harun.splitledger.data.local.prefs.UserPreferencesDataStore
import com.harun.splitledger.data.local.seed.BundledFxRates
import com.harun.splitledger.data.local.seed.DemoTripSeeder
import com.harun.splitledger.domain.math.BalanceCalculator
import com.harun.splitledger.domain.model.AppThemeMode
import com.harun.splitledger.domain.model.CurrencyCatalog
import com.harun.splitledger.domain.model.Expense
import com.harun.splitledger.domain.model.ExpenseCategory
import com.harun.splitledger.domain.model.ExpenseSplit
import com.harun.splitledger.domain.model.Participant
import com.harun.splitledger.domain.model.RateSource
import com.harun.splitledger.domain.model.Settlement
import com.harun.splitledger.domain.model.SplitMode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.math.BigDecimal

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SplitLedgerDaoTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private lateinit var database: SplitLedgerDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, SplitLedgerDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun reactiveFlowQueriesReturnInsertedEntities() = runTest {
        val trip = TripEntity("t1", "Kyoto", "USD", isSettled = false, isDemo = false, createdAtEpochMs = 1000L)
        val p1 = ParticipantEntity("p1", "t1", "Alice", 0)
        val p2 = ParticipantEntity("p2", "t1", "Bob", 1)
        val expense = ExpenseEntity(
            id = "e1",
            tripId = "t1",
            payerId = "p1",
            title = "Matcha",
            category = ExpenseCategory.FOOD.name,
            amountMinor = 2000L,
            currencyCode = "USD",
            homeAmountMinor = 2000L,
            fxRateDecimal = "1",
            rateSource = RateSource.LIVE,
            splitMode = SplitMode.EQUAL,
            createdAtEpochMs = 2000L,
        )
        val splits = listOf(
            ExpenseSplitEntity("e1", "p1", 1000L, 1000L, 5000),
            ExpenseSplitEntity("e1", "p2", 1000L, 1000L, 5000),
        )
        val settlement = SettlementEntity("s1", "t1", "p2", "p1", 500L, 3000L)
        val rate = FxRateEntity("USD", "EUR", "1.0951", 4000L, isBundledFallback = false)

        database.tripDao().upsertTrip(trip)
        database.tripDao().upsertParticipants(listOf(p1, p2))
        database.expenseDao().upsertExpenseWithSplits(expense, splits)
        database.settlementDao().upsertSettlement(settlement)
        database.fxRateDao().upsertRates(listOf(rate))

        assertEquals(listOf(trip), database.tripDao().observeTrips().first())
        assertEquals(trip, database.tripDao().observeTrip("t1").first())
        assertEquals(listOf(p1, p2), database.tripDao().observeParticipants("t1").first())

        val observedExpenses = database.expenseDao().observeExpenseWithSplit("t1").first()
        assertEquals(1, observedExpenses.size)
        assertEquals(expense, observedExpenses.first().expense)
        assertEquals(splits.toSet(), observedExpenses.first().splits.toSet())

        assertEquals(listOf(settlement), database.settlementDao().observeSettlements("t1").first())
        assertEquals(listOf(rate), database.fxRateDao().observeRates().first())
    }

    @Test
    fun atomicExpenseUpdateReplacesSplitsWithZeroOrphans() = runTest {
        val trip = TripEntity("t1", "Trip", "USD", isSettled = false, isDemo = false, createdAtEpochMs = 1000L)
        val p1 = ParticipantEntity("p1", "t1", "Alice", 0)
        val p2 = ParticipantEntity("p2", "t1", "Bob", 1)
        val p3 = ParticipantEntity("p3", "t1", "Cara", 2)
        database.tripDao().upsertTrip(trip)
        database.tripDao().upsertParticipants(listOf(p1, p2, p3))

        val initialExpense = ExpenseEntity(
            id = "e1",
            tripId = "t1",
            payerId = "p1",
            title = "Dinner",
            category = ExpenseCategory.FOOD.name,
            amountMinor = 3000L,
            currencyCode = "USD",
            homeAmountMinor = 3000L,
            fxRateDecimal = "1",
            rateSource = RateSource.LIVE,
            splitMode = SplitMode.EQUAL,
            createdAtEpochMs = 2000L,
        )
        database.expenseDao().upsertExpenseWithSplits(
            initialExpense,
            listOf(
                ExpenseSplitEntity("e1", "p1", 1000L, 1000L, 3334),
                ExpenseSplitEntity("e1", "p2", 1000L, 1000L, 3333),
                ExpenseSplitEntity("e1", "p3", 1000L, 1000L, 3333),
            ),
        )

        val updatedExpense = initialExpense.copy(amountMinor = 2000L, homeAmountMinor = 2000L)
        database.expenseDao().upsertExpenseWithSplits(
            updatedExpense,
            listOf(
                ExpenseSplitEntity("e1", "p1", 1000L, 1000L, 5000),
                ExpenseSplitEntity("e1", "p2", 1000L, 1000L, 5000),
            ),
        )

        val fetched = database.expenseDao().getExpenseWithSplits("e1")
        assertNotNull(fetched)
        assertEquals(2000L, fetched!!.expense.amountMinor)
        assertEquals(setOf("p1", "p2"), fetched.splits.map { it.participantId }.toSet())
        assertFalse(database.expenseDao().isParticipantUsedByExpense("p3"))
    }

    @Test
    fun foreignKeyRestrictBlocksDeletingReferencedParticipantAndAllowsUnreferenced() = runTest {
        val trip = TripEntity("t1", "Trip", "USD", isSettled = false, isDemo = false, createdAtEpochMs = 1000L)
        val payer = ParticipantEntity("p-payer", "t1", "Alice", 0)
        val splitMember = ParticipantEntity("p-split", "t1", "Bob", 1)
        val settlementReceiver = ParticipantEntity("p-recv", "t1", "Cara", 2)
        val unused = ParticipantEntity("p-unused", "t1", "Dan", 3)

        database.tripDao().upsertTrip(trip)
        database.tripDao().upsertParticipants(listOf(payer, splitMember, settlementReceiver, unused))

        val expense = ExpenseEntity(
            id = "e1",
            tripId = "t1",
            payerId = payer.id,
            title = "Taxi",
            category = ExpenseCategory.TRANSPORT.name,
            amountMinor = 1000L,
            currencyCode = "USD",
            homeAmountMinor = 1000L,
            fxRateDecimal = "1",
            rateSource = RateSource.LIVE,
            splitMode = SplitMode.EQUAL,
            createdAtEpochMs = 2000L,
        )
        database.expenseDao().upsertExpenseWithSplits(
            expense,
            listOf(
                ExpenseSplitEntity("e1", payer.id, 500L, 500L, 5000),
                ExpenseSplitEntity("e1", splitMember.id, 500L, 500L, 5000),
            ),
        )
        database.settlementDao().upsertSettlement(
            SettlementEntity("s1", "t1", splitMember.id, settlementReceiver.id, 500L, 3000L)
        )

        // All 3 referenced participants must be blocked by SQLite FK RESTRICT
        for (blockedId in listOf(payer.id, splitMember.id, settlementReceiver.id)) {
            try {
                database.tripDao().deleteParticipant(blockedId)
                fail("Expected SQLiteConstraintException when deleting referenced participant $blockedId")
            } catch (_: SQLiteConstraintException) {
                // expected
            }
        }
        assertTrue(database.expenseDao().isParticipantUsedByExpense(payer.id))
        assertTrue(database.expenseDao().isParticipantUsedByExpense(splitMember.id))
        assertTrue(database.settlementDao().isParticipantUsedBySettlement(settlementReceiver.id))

        // Unreferenced participant can be deleted cleanly
        database.tripDao().deleteParticipant(unused.id)
        assertFalse(database.tripDao().getParticipants("t1").any { it.id == unused.id })

        // Deleting whole trip succeeds cleanly despite FK RESTRICT on participants
        database.tripDao().deleteTrip("t1")
        assertNull(database.tripDao().getTripById("t1"))
        assertTrue(database.tripDao().getParticipants("t1").isEmpty())
    }

    @Test
    fun bundledFxSeedPopulatesAll20CurrenciesAndPreservesLiveRates() = runTest {
        val seeder = DemoTripSeeder(database)
        seeder.seedBundledFxRatesIfNeeded()

        val seededRates = database.fxRateDao().getRates()
        val expectedCodes = CurrencyCatalog.currencies.map { it.code }.toSet()
        assertEquals(20, seededRates.size)
        assertEquals(expectedCodes, seededRates.map { it.quoteCurrency }.toSet())
        assertTrue(seededRates.all { it.isBundledFallback && it.baseCurrency == "EUR" })

        // Live sync updates USD; re-running bundled seed must not overwrite it
        val liveUsd = FxRateEntity("USD", "EUR", "1.1234", 999_999L, isBundledFallback = false)
        database.fxRateDao().upsertRates(listOf(liveUsd))
        seeder.seedBundledFxRatesIfNeeded()

        val usdAfterReseed = database.fxRateDao().getRates().first { it.quoteCurrency == "USD" }
        assertEquals("1.1234", usdAfterReseed.rateDecimal)
        assertFalse(usdAfterReseed.isBundledFallback)
        assertEquals(BundledFxRates.BUNDLED_TIMESTAMP_EPOCH_MS, database.fxRateDao().getRates().first { it.quoteCurrency == "JPY" }.updatedAtEpochMs)
    }

    @Test
    fun demoTripSeederProducesZeroSumLedgerAndIsIdempotent() = runTest {
        val seeder = DemoTripSeeder(database)
        seeder.seedIfEmpty(nowEpochMs = 1_750_000_000_000L)
        seeder.seedIfEmpty(nowEpochMs = 1_750_000_001_000L)

        val trips = database.tripDao().observeTrips().first()
        assertEquals(1, trips.size)
        val trip = trips.first()
        assertEquals("Tokyo Trip 2026", trip.name)
        assertEquals("USD", trip.homeCurrency)
        assertTrue(trip.isDemo)

        val participantEntities = database.tripDao().getParticipants(trip.id)
        val expenseWithSplits = database.expenseDao().observeExpenseWithSplit(trip.id).first()
        val settlementEntities = database.settlementDao().observeSettlements(trip.id).first()

        assertEquals(4, participantEntities.size)
        assertEquals(5, expenseWithSplits.size)
        assertEquals(1, settlementEntities.size)

        val participants = participantEntities.map {
            Participant(it.id, it.tripId, it.name, it.avatarColorIndex)
        }
        val expenses = expenseWithSplits.map { item ->
            Expense(
                id = item.expense.id,
                tripId = item.expense.tripId,
                title = item.expense.title,
                category = ExpenseCategory.valueOf(item.expense.category),
                amountMinor = item.expense.amountMinor,
                currencyCode = item.expense.currencyCode,
                homeAmountMinor = item.expense.homeAmountMinor,
                fxRateDecimal = BigDecimal(item.expense.fxRateDecimal),
                rateSource = item.expense.rateSource,
                payerId = item.expense.payerId,
                splitMode = item.expense.splitMode,
                createdAtEpochMs = item.expense.createdAtEpochMs,
                splits = item.splits.map { s ->
                    ExpenseSplit(s.participantId, s.originalShareMinor, s.homeShareMinor, s.percentBps)
                },
            )
        }
        val settlements = settlementEntities.map {
            Settlement(it.id, it.tripId, it.fromParticipantId, it.toParticipantId, it.amountHomeMinor, it.createdAtEpochMs)
        }

        val balances = BalanceCalculator.calculate(participants, expenses, settlements)
        assertEquals(4, balances.size)
        assertEquals(0L, balances.sumOf { it.netHomeMinor })
    }

    @Test
    fun userPreferencesDataStoreReadsDefaultsAndPersistsUpdates() = runTest {
        val dataStore = PreferenceDataStoreFactory.create(
            scope = backgroundScope,
            produceFile = { File(tempFolder.newFolder(), "test_prefs.preferences_pb") },
        )
        val prefsStore = UserPreferencesDataStore(dataStore)

        val defaults = prefsStore.preferences.first()
        assertEquals("USD", defaults.defaultHomeCurrency)
        assertEquals(AppThemeMode.SYSTEM, defaults.themeMode)
        assertNull(defaults.lastFxSyncEpochMs)
        assertFalse(defaults.hasSeededDemo)

        prefsStore.setDefaultHomeCurrency("jpy")
        prefsStore.setThemeMode(AppThemeMode.DARK)
        prefsStore.setLastFxSyncEpochMs(123_456L)
        prefsStore.setHasSeededDemo(true)

        val updated = prefsStore.preferences.first()
        assertEquals("JPY", updated.defaultHomeCurrency)
        assertEquals(AppThemeMode.DARK, updated.themeMode)
        assertEquals(123_456L, updated.lastFxSyncEpochMs)
        assertTrue(updated.hasSeededDemo)
    }
}
