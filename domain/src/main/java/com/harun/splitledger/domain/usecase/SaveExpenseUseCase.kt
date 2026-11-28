package com.harun.splitledger.domain.usecase

import com.harun.splitledger.domain.math.FxConverter
import com.harun.splitledger.domain.math.SplitCalculator
import com.harun.splitledger.domain.model.DomainError
import com.harun.splitledger.domain.model.DomainException
import com.harun.splitledger.domain.model.Expense
import com.harun.splitledger.domain.model.ExpenseCategory
import com.harun.splitledger.domain.model.RateSource
import com.harun.splitledger.domain.model.SplitMode
import com.harun.splitledger.domain.repository.ExpenseRepository
import com.harun.splitledger.domain.repository.FxRateRepository
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject

class SaveExpenseUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val fxRateRepository: FxRateRepository
) {
    suspend operator fun invoke(params: Params): Result<Expense> {
        val rate: BigDecimal
        val rateSource: RateSource

        if (params.amountMinor <= 0L)
            return Result.failure(DomainException(DomainError.InvalidAmount))
        if (params.participantsId.isEmpty())
            return Result.failure(DomainException(DomainError.EmptyParticipants))

        when {
            params.manualFxRate != null -> {
                if (params.manualFxRate <= BigDecimal.ZERO) {
                    return Result.failure(DomainException(DomainError.InvalidFxRate))
                }
                rate = params.manualFxRate
                rateSource = RateSource.MANUAL
            }
            params.currencyCode.equals(params.homeCurrency, ignoreCase = true) -> {
                rate = BigDecimal.ONE
                rateSource = RateSource.LIVE
            }
            else -> {
                val from = params.currencyCode.uppercase()
                val to = params.homeCurrency.uppercase()
                val rateByCode = fxRateRepository.getRates().associateBy { it.quoteCurrency.uppercase() }
                val eurRates = rateByCode.mapValues { it.value.rateDecimal }

                rate = FxConverter.resolveRate(from, to, eurRates)
                    ?: return Result.failure(DomainException(DomainError.MissingFxRate(from, to)))

                val usedRates = listOfNotNull(rateByCode[from], rateByCode[to])
                val isStale = usedRates.any { fxRateRepository.isRateStale(it, params.createdAtEpochMs) }
                rateSource = if (isStale) RateSource.CACHED else RateSource.LIVE
            }
        }

        val homeAmountMinor = FxConverter.convertMinor(
            amountMinor = params.amountMinor,
            fromCurrency = params.currencyCode,
            toCurrency = params.homeCurrency,
            rate = rate
        )

        val splits = SplitCalculator.calculate(
            amountMinor = params.amountMinor,
            homeAmountMinor = homeAmountMinor,
            mode = params.splitMode,
            participantIds = params.participantsId,
            exactMinorByParticipant = params.exactMinorByParticipant,
            percentBpsByParticipant = params.percentBpsByParticipant
        ).getOrElse { return Result.failure(it) }

        val expense = Expense(
            id = params.expenseId ?: UUID.randomUUID().toString(),
            tripId = params.tripId,
            title = params.title.trim(),
            category = params.category,
            amountMinor = params.amountMinor,
            currencyCode = params.currencyCode.uppercase(),
            homeAmountMinor = homeAmountMinor,
            fxRateDecimal = rate,
            rateSource = rateSource,
            payerId = params.payerId,
            splitMode = params.splitMode,
            createdAtEpochMs = params.createdAtEpochMs,
            splits = splits
        )

        expenseRepository.upsertExpense(expense)

        return Result.success(expense)
    }

    data class Params(
        val tripId: String,
        val title: String,
        val category: ExpenseCategory,
        val amountMinor: Long,
        val currencyCode: String,
        val homeCurrency: String,
        val payerId: String,
        val splitMode: SplitMode,
        val participantsId: List<String>,
        val exactMinorByParticipant: Map<String, Long> = emptyMap(),
        val percentBpsByParticipant: Map<String, Int> = emptyMap(),
        val manualFxRate: BigDecimal? = null,
        val expenseId: String? = null,
        val createdAtEpochMs: Long = System.currentTimeMillis()
    )
}