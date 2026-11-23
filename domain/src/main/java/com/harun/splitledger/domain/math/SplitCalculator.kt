package com.harun.splitledger.domain.math

import com.harun.splitledger.domain.model.DomainError
import com.harun.splitledger.domain.model.DomainException
import com.harun.splitledger.domain.model.ExpenseSplit
import com.harun.splitledger.domain.model.SplitMode

object SplitCalculator {
    private const val TOTAL_BPS = 10_000

    fun calculate(
        amountMinor: Long,
        homeAmountMinor: Long,
        mode: SplitMode,
        participantIds: List<String>,
        exactMinorByParticipant: Map<String, Long> = emptyMap(),
        percentBpsByParticipant: Map<String, Int> = emptyMap()
    ): Result<List<ExpenseSplit>> {
        if (amountMinor <= 0L || homeAmountMinor <= 0L) {
            return Result.failure(DomainException(DomainError.InvalidAmount))
        }

        if (participantIds.isEmpty()) {
            return Result.failure(DomainException(DomainError.EmptyParticipants))
        }

        val sortedParticipantIds = participantIds.distinct().sorted()
        val count = sortedParticipantIds.size

        return when (mode) {
            SplitMode.EQUAL -> {
                val baseOriginal = amountMinor / count
                val remOriginal = (amountMinor % count).toInt()

                val baseHome = homeAmountMinor / count
                val remHome = (homeAmountMinor % count).toInt()

                val baseBps = TOTAL_BPS / count
                val remBps = TOTAL_BPS % count

                val splits = sortedParticipantIds.mapIndexed { index, id ->
                    val originalShare = if (index < remOriginal) baseOriginal + 1L else baseOriginal
                    val homeShare = if (index < remHome) baseHome + 1L else baseHome
                    val bpsShare = if (index < remBps) baseBps + 1 else baseBps

                    ExpenseSplit(
                        participantId = id,
                        originalShareMinor = originalShare,
                        homeShareMinor = homeShare,
                        percentBps = bpsShare
                    )
                }
                Result.success(splits)
            }

            SplitMode.EXACT -> {
                val exactShares = sortedParticipantIds.map { id ->
                    exactMinorByParticipant[id] ?: 0L
                }

                if (exactShares.any { it < 0L }) {
                    return Result.failure(DomainException(DomainError.InvalidAmount))
                }

                val totalExact = exactShares.sum()
                if (totalExact != amountMinor) {
                    return Result.failure(
                        DomainException(
                            DomainError.ExactSplitMismatch(
                                expectedMinor = amountMinor,
                                actualMinor = totalExact
                            )
                        )
                    )
                }

                val homeShares = allocateProportional(homeAmountMinor, exactShares)
                val bpsShares = allocateProportional(TOTAL_BPS.toLong(), exactShares).map { it.toInt() }

                val splits = sortedParticipantIds.mapIndexed { index, id ->
                    ExpenseSplit(
                        participantId = id,
                        originalShareMinor = exactShares[index],
                        homeShareMinor = homeShares[index],
                        percentBps = bpsShares[index]
                    )
                }
                Result.success(splits)
            }

            SplitMode.PERCENT -> {
                val bpsShares = sortedParticipantIds.map { id ->
                    percentBpsByParticipant[id] ?: 0
                }

                if (bpsShares.any { it < 0 }) {
                    return Result.failure(DomainException(DomainError.InvalidAmount))
                }

                val totalBps = bpsShares.sum()
                if (totalBps != TOTAL_BPS) {
                    return Result.failure(
                        DomainException(DomainError.PercentSplitMismatch(actualBps = totalBps))
                    )
                }

                val bpsWeights = bpsShares.map { it.toLong() }
                val originalShares = allocateProportional(amountMinor, bpsWeights)
                val homeShares = allocateProportional(homeAmountMinor, bpsWeights)

                val splits = sortedParticipantIds.mapIndexed { index, id ->
                    ExpenseSplit(
                        participantId = id,
                        originalShareMinor = originalShares[index],
                        homeShareMinor = homeShares[index],
                        percentBps = bpsShares[index]
                    )
                }
                Result.success(splits)
            }
        }
    }

    private fun allocateProportional(total: Long, weights: List<Long>): List<Long> {
        val totalWeight = weights.sum().toBigInteger()
        val totalBig = total.toBigInteger()

        val baseShares = weights.map { weight ->
            (weight.toBigInteger() * totalBig / totalWeight).toLong()
        }

        var remaining = total - baseShares.sum()
        return baseShares.mapIndexed { index, base ->
            if (remaining > 0L && weights[index] > 0L) {
                remaining--
                base + 1L
            } else {
                base
            }
        }
    }
}