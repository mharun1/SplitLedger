package com.harun.splitledger.domain.model

import java.math.BigDecimal

data class Trip (
    val id: String,
    val name: String,
    val homeCurrency: String,
    val isSettled: Boolean,
    val isDemo: Boolean,
    val createdAtEpochMs: Long,
)
data class Participant (
    val id: String,
    val tripId: String,
    val name: String,
    val avatarColorIndex: Int
)
data class ExpenseSplit (
    val participantId: String,
    val originalShareMinor: Long,
    val homeShareMinor: Long,
    val percentBps: Int
)
data class Expense (
    val id: String,
    val tripId: String,
    val title: String,
    val category: ExpenseCategory,
    val amountMinor: Long,
    val currencyCode: String,
    val homeAmountMinor: Long,
    val fxRateDecimal: BigDecimal,
    val rateSource: RateSource,
    val payerId: String,
    val splitMode: SplitMode,
    val createdAtEpochMs: Long,
    val split: List<ExpenseSplit>
)
data class Settlement (
    val id: String,
    val tripId: String,
    val fromParticipantId: String,
    val toParticipantId: String,
    val amountHomeMinor: Long,
    val createdAtEpochMs: Long
)
data class FxRate (
    val quoteCurrency: String,
    val baseCurrency: String = "EUR",
    val rateDecimal: BigDecimal,
    val updatedAtEpochMs: Long,
    val isBundledFallback: Boolean
)
data class ParticipantBalance (
    val participant: Participant,
    val totalPaidHomeMinor: Long,
    val totalOwedHomeMinor: Long,
    val netHomeMinor: Long,
)
data class TransferSuggestion(
    val fromParticipant: Participant,
    val toParticipant: Participant,
    val amountHomeMinor: Long,
)
data class UserPreferences(
    val defaultHomeCurrency: String = "USD",
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val lastFxSyncEpochMs: Long? = null,
    val hasSeededDemo: Boolean = false,
)
sealed interface FxSyncState {
    data object Idle : FxSyncState
    data object Syncing : FxSyncState
    data class Synced(val syncedAtEpochMs: Long) : FxSyncState
    data class Error(val message: String) : FxSyncState
}
sealed interface DomainError {
    data object InvalidAmount : DomainError
    data object EmptyParticipants : DomainError
    data class ExactSplitMismatch(val expectedMinor: Long, val actualMinor: Long) : DomainError
    data class PercentSplitMismatch(val actualBps: Int) : DomainError
    data object InvalidFxRate : DomainError
    data object ParticipantInUse : DomainError
    data object SameSettlementParticipant : DomainError
    data class MissingFxRate(val fromCurrency: String, val toCurrency: String) : DomainError
}

class DomainException(val error: DomainError) : IllegalArgumentException(error.toString())

enum class ExpenseCategory {
    FOOD, STAY, TRANSPORT,
    ACTIVITY, SHOPPING, OTHER
}
enum class SplitMode {
    EQUAL, EXACT, PERCENT
}
enum class RateSource {
    LIVE, CACHED, MANUAL
}
enum class AppThemeMode {
    SYSTEM, LIGHT, DARK
}