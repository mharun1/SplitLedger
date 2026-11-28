package com.harun.splitledger.data.local.seed

import androidx.room.withTransaction
import com.harun.splitledger.data.local.db.SplitLedgerDatabase
import com.harun.splitledger.data.local.db.dao.ExpenseWithSplits
import com.harun.splitledger.data.local.db.entity.ExpenseEntity
import com.harun.splitledger.data.local.db.entity.ExpenseSplitEntity
import com.harun.splitledger.data.local.db.entity.ParticipantEntity
import com.harun.splitledger.data.local.db.entity.SettlementEntity
import com.harun.splitledger.data.local.db.entity.TripEntity
import com.harun.splitledger.domain.math.FxConverter
import com.harun.splitledger.domain.math.SplitCalculator
import com.harun.splitledger.domain.model.ExpenseCategory
import com.harun.splitledger.domain.model.RateSource
import com.harun.splitledger.domain.model.SplitMode
import javax.inject.Inject

private const val DEMO_TRIP_ID = "demo-tokyo-2026"
private const val ALEX_ID = "demo-p-alex"
private const val MAYA_ID = "demo-p-maya"
private const val KENJI_ID = "demo-p-kenji"
private const val SAM_ID = "demo-p-sam"

private val eurBaseRates = BundledFxRates.eurBaseRates.mapValues { it.value.toBigDecimal() }
private val participants = listOf(
    ParticipantEntity(
        id = ALEX_ID,
        tripId = DEMO_TRIP_ID,
        name = "Alex",
        avatarColorIndex = 0
    ),
    ParticipantEntity(
        id = MAYA_ID,
        tripId = DEMO_TRIP_ID,
        name = "Maya",
        avatarColorIndex = 1
    ),
    ParticipantEntity(
        id = KENJI_ID,
        tripId = DEMO_TRIP_ID,
        name = "Kenji",
        avatarColorIndex = 2
    ),
    ParticipantEntity(
        id = SAM_ID,
        tripId = DEMO_TRIP_ID,
        name = "Sam",
        avatarColorIndex = 3
    ),
)

private val allMembersId = participants.map { it.id }
private const val dayMs = 86_400_000L

class DemoTripSeeder @Inject constructor(
    private val database: SplitLedgerDatabase
) {
    private suspend fun insertDemoTrips(nowEpochMs: Long) {
        val expenses = listOf(
            buildDemoExpense(
                id = "demo-exp-1",
                payerId = ALEX_ID,
                title = "Shinjuku Ramen",
                category = ExpenseCategory.FOOD,
                amountMinor = 12400L,
                currencyCode = "JPY",
                splitMode = SplitMode.EQUAL,
                participantIds = allMembersId,
                createdAtEpochMs = nowEpochMs - 4 * dayMs,
            ),
            buildDemoExpense(
                id = "demo-exp-2",
                payerId = MAYA_ID,
                title = "TeamLab & Souvenirs",
                category = ExpenseCategory.SHOPPING,
                amountMinor = 18000L,
                currencyCode = "JPY",
                splitMode = SplitMode.EXACT,
                participantIds = allMembersId,
                createdAtEpochMs = nowEpochMs - 2 * dayMs,
                exactMinorByParticipant = mapOf(ALEX_ID to 2000L, MAYA_ID to 4000L, KENJI_ID to 5000L, SAM_ID to 7000L),
            ),
            buildDemoExpense(
                id = "demo-exp-3",
                payerId = KENJI_ID,
                title = "Shibuya Hotel 3 Nights",
                category = ExpenseCategory.SHOPPING,
                amountMinor = 42000L,
                currencyCode = "USD",
                splitMode = SplitMode.PERCENT,
                participantIds = allMembersId,
                createdAtEpochMs = nowEpochMs - 3 * dayMs,
                percentBpsByParticipant = mapOf(ALEX_ID to 3500, MAYA_ID to 2500, KENJI_ID to 2000, SAM_ID to 2000)
            ),
            buildDemoExpense(
                id = "demo-exp-4",
                payerId = SAM_ID,
                title = "Travel Sim & Railway Pass",
                category = ExpenseCategory.SHOPPING,
                amountMinor = 9600L,
                currencyCode = "EUR",
                splitMode = SplitMode.EQUAL,
                participantIds = allMembersId,
                createdAtEpochMs = nowEpochMs - 1 * dayMs,
            ),
            buildDemoExpense(
                id = "demo-exp-5",
                payerId = ALEX_ID,
                title = "Narita Express",
                category = ExpenseCategory.SHOPPING,
                amountMinor = 13200L,
                currencyCode = "JPY",
                splitMode = SplitMode.EQUAL,
                participantIds = allMembersId,
                createdAtEpochMs = nowEpochMs - 3 * dayMs,
            )
        )
        database.withTransaction {
            seedBundledFxRatesIfNeeded()
            database.tripDao().upsertTrip(
                TripEntity(
                    id = DEMO_TRIP_ID,
                    name = "Tokyo Trip 2026",
                    homeCurrency = "USD",
                    isSettled = false,
                    isDemo = true,
                    createdAtEpochMs = nowEpochMs
                )
            )
            database.tripDao().upsertParticipants(participants)
            expenses.forEach { item ->
                database.expenseDao().upsertExpenseWithSplits(item.expense, item.splits)
            }
            database.settlementDao().upsertSettlement(
                SettlementEntity(
                    id = "demo-settlement-1",
                    tripId = DEMO_TRIP_ID,
                    fromParticipantId = MAYA_ID,
                    toParticipantId = KENJI_ID,
                    amountHomeMinor = 5000L,
                    createdAtEpochMs = nowEpochMs - 3_600_000L
                )
            )
        }
    }

    suspend fun seedBundledFxRatesIfNeeded() {
        database.fxRateDao().insertRateIfMissing(BundledFxRates.toEntities())
    }

    suspend fun seedIfEmpty(nowEpochMs: Long = System.currentTimeMillis()) {
        seedBundledFxRatesIfNeeded()
        if (database.tripDao().getDemoTripIds().isNotEmpty()) {
            return
        } else {
            insertDemoTrips(nowEpochMs)
        }
    }

    suspend fun resetDemoTrip(nowEpochMs: Long = System.currentTimeMillis()) {
        database.withTransaction {
            database.tripDao().getDemoTripIds().forEach { database.tripDao().deleteTrip(it) }
            insertDemoTrips(nowEpochMs)
        }
    }
}

private fun buildDemoExpense(
    id: String,
    payerId: String,
    title: String,
    category: ExpenseCategory,
    amountMinor: Long,
    currencyCode: String,
    splitMode: SplitMode,
    participantIds: List<String>,
    createdAtEpochMs: Long,
    exactMinorByParticipant: Map<String, Long> = emptyMap(),
    percentBpsByParticipant: Map<String, Int> = emptyMap(),
): ExpenseWithSplits {
    val rate = requireNotNull(FxConverter.resolveRate(currencyCode, "USD", eurBaseRates))
    val homeAmountMinor = FxConverter.convertMinor(amountMinor, currencyCode, "USD", rate)
    val domainSplits = SplitCalculator.calculate(
        amountMinor = amountMinor,
        homeAmountMinor = homeAmountMinor,
        mode = splitMode,
        participantIds = participantIds,
        exactMinorByParticipant = exactMinorByParticipant,
        percentBpsByParticipant = percentBpsByParticipant,
    ).getOrThrow()

    val expense = ExpenseEntity(
        id = id,
        tripId = DEMO_TRIP_ID,
        payerId = payerId,
        title = title,
        category = category.name,
        amountMinor = amountMinor,
        currencyCode = currencyCode,
        homeAmountMinor = homeAmountMinor,
        fxRateDecimal = rate.stripTrailingZeros().toPlainString(),
        rateSource = RateSource.CACHED,
        splitMode = splitMode,
        createdAtEpochMs = createdAtEpochMs,
    )
    val splits = domainSplits.map {
        ExpenseSplitEntity(
            expenseId = id,
            participantId = it.participantId,
            originalShareMinor = it.originalShareMinor,
            homeShareMinor = it.homeShareMinor,
            percentBps = it.percentBps,
        )
    }
    return ExpenseWithSplits(expense, splits)
}