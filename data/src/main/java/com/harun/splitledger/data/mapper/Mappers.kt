package com.harun.splitledger.data.mapper

import com.harun.splitledger.data.local.db.dao.ExpenseWithSplits
import com.harun.splitledger.data.local.db.entity.ExpenseEntity
import com.harun.splitledger.data.local.db.entity.ExpenseSplitEntity
import com.harun.splitledger.data.local.db.entity.FxRateEntity
import com.harun.splitledger.data.local.db.entity.ParticipantEntity
import com.harun.splitledger.data.local.db.entity.SettlementEntity
import com.harun.splitledger.data.local.db.entity.TripEntity
import com.harun.splitledger.domain.model.Expense
import com.harun.splitledger.domain.model.ExpenseCategory
import com.harun.splitledger.domain.model.ExpenseSplit
import com.harun.splitledger.domain.model.FxRate
import com.harun.splitledger.domain.model.Participant
import com.harun.splitledger.domain.model.Settlement
import com.harun.splitledger.domain.model.Trip
import java.math.BigDecimal

fun TripEntity.toDomain(): Trip = Trip(
    id = id,
    name = name,
    homeCurrency = homeCurrency,
    isSettled = isSettled,
    isDemo = isDemo,
    createdAtEpochMs = createdAtEpochMs
)

fun Trip.toEntity(): TripEntity = TripEntity(
    id = id,
    name = name,
    homeCurrency = homeCurrency,
    isSettled = isSettled,
    isDemo = isDemo,
    createdAtEpochMs = createdAtEpochMs
)

fun ParticipantEntity.toDomain(): Participant = Participant(
    id = id,
    tripId = tripId,
    name = name,
    avatarColorIndex = avatarColorIndex
)

fun Participant.toEntity(): ParticipantEntity = ParticipantEntity(
    id = id,
    tripId = tripId,
    name = name,
    avatarColorIndex = avatarColorIndex
)

fun SettlementEntity.toDomain(): Settlement = Settlement(
    id = id,
    tripId = tripId,
    fromParticipantId = fromParticipantId,
    toParticipantId = toParticipantId,
    amountHomeMinor = amountHomeMinor,
    createdAtEpochMs = createdAtEpochMs
)

fun Settlement.toEntity(): SettlementEntity = SettlementEntity(
    id = id,
    tripId = tripId,
    fromParticipantId = fromParticipantId,
    toParticipantId = toParticipantId,
    amountHomeMinor = amountHomeMinor,
    createdAtEpochMs = createdAtEpochMs
)

fun FxRateEntity.toDomain(): FxRate = FxRate(
    quoteCurrency = quoteCurrency,
    baseCurrency = baseCurrency,
    rateDecimal = BigDecimal(this.rateDecimal),
    updatedAtEpochMs = updatedAtEpochMs,
    isBundledFallback = isBundledFallback
)

fun FxRate.toEntity(): FxRateEntity = FxRateEntity(
    quoteCurrency = quoteCurrency,
    baseCurrency = baseCurrency,
    rateDecimal = rateDecimal.stripTrailingZeros().toPlainString(),
    updatedAtEpochMs = updatedAtEpochMs,
    isBundledFallback = isBundledFallback
)

fun ExpenseSplitEntity.toDomain(): ExpenseSplit = ExpenseSplit(
    participantId = participantId,
    originalShareMinor = originalShareMinor,
    homeShareMinor = homeShareMinor,
    percentBps = percentBps
)

fun ExpenseWithSplits.toDomain(): Expense = Expense(
    id = expense.id,
    tripId = expense.tripId,
    title = expense.title,
    category = runCatching { ExpenseCategory.valueOf(expense.category) }.getOrDefault(ExpenseCategory.OTHER),
    amountMinor = expense.amountMinor,
    currencyCode = expense.currencyCode,
    homeAmountMinor = expense.homeAmountMinor,
    fxRateDecimal = BigDecimal(expense.fxRateDecimal),
    rateSource = expense.rateSource,
    payerId = expense.payerId,
    splitMode = expense.splitMode,
    createdAtEpochMs = expense.createdAtEpochMs,
    splits = splits.map { it.toDomain() }
)

fun Expense.toEntity(): ExpenseEntity = ExpenseEntity(
    id = id,
    tripId = tripId,
    payerId = payerId,
    title = title,
    category = category.name,
    amountMinor = amountMinor,
    currencyCode = currencyCode,
    homeAmountMinor = homeAmountMinor,
    fxRateDecimal = fxRateDecimal.stripTrailingZeros().toPlainString(),
    rateSource = rateSource,
    splitMode = splitMode,
    createdAtEpochMs = createdAtEpochMs
)

fun ExpenseSplit.toEntity(expenseId: String): ExpenseSplitEntity = ExpenseSplitEntity(
    expenseId = expenseId,
    participantId = participantId,
    originalShareMinor = originalShareMinor,
    homeShareMinor = homeShareMinor,
    percentBps = percentBps,
)