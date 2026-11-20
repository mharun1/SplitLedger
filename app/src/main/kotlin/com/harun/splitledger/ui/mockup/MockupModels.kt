package com.harun.splitledger.ui.mockup

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.Hotel
import androidx.compose.material.icons.rounded.LocalActivity
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material.icons.rounded.Train
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.harun.splitledger.ui.theme.SplitLedgerTheme
import com.harun.splitledger.ui.theme.tabular

// Pure stateless UI preview models — no ViewModels, Room entities, or navigation state.

@Immutable
enum class MockFxSyncStatus { LIVE, CACHED_STALE, SYNCING, OFFLINE }

@Immutable
enum class MockRateProvenance { LIVE, CACHED, MANUAL, HOME }

@Immutable
enum class MockExpenseCategory(val label: String, val icon: ImageVector) {
    FOOD("Food", Icons.Rounded.Restaurant),
    STAY("Stay", Icons.Rounded.Hotel),
    TRANSPORT("Transport", Icons.Rounded.Train),
    ACTIVITY("Activity", Icons.Rounded.LocalActivity),
    SHOPPING("Shopping", Icons.Rounded.ShoppingBag),
    OTHER("Other", Icons.AutoMirrored.Rounded.ReceiptLong),
}

@Immutable
enum class MockTripFilter { ACTIVE, SETTLED }

@Immutable
enum class MockTripDetailTab(val label: String) {
    EXPENSES("Expenses"),
    BALANCES("Balances"),
    SETTLE("Settle"),
}

@Immutable
enum class MockSplitMode(val label: String) {
    EQUAL("Equal"),
    EXACT("Exact"),
    PERCENT("Percent"),
}

@Immutable
data class MockMember(
    val id: String,
    val name: String,
    val initials: String,
    val colorIndex: Int,
)

@Immutable
data class MockTripSummary(
    val id: String,
    val name: String,
    val dateRange: String,
    val homeCurrency: String,
    val spentCurrencies: List<String>,
    val totalSpentFormatted: String,
    val netBalanceFormatted: String,
    val isUserOwed: Boolean,
    val isSettled: Boolean,
    val isDemo: Boolean,
    val hasStaleFx: Boolean,
    val expenseCount: Int,
    val members: List<MockMember>,
)

@Immutable
data class MockTripsHomeState(
    val selectedFilter: MockTripFilter,
    val activeCount: Int,
    val settledCount: Int,
    val totalActiveSpendFormatted: String,
    val overallNetFormatted: String,
    val isOverallPositive: Boolean,
    val fxSyncStatus: MockFxSyncStatus,
    val fxSyncLabel: String,
    val trips: List<MockTripSummary>,
)

@Immutable
data class MockExpenseItem(
    val id: String,
    val title: String,
    val dateGroup: String,
    val category: MockExpenseCategory,
    val payerName: String,
    val splitSummary: String,
    val originalAmountFormatted: String,
    val homeAmountFormatted: String,
    val rateProvenance: MockRateProvenance,
)

@Immutable
data class MockMemberBalance(
    val member: MockMember,
    val paidFormatted: String,
    val shareFormatted: String,
    val netFormatted: String,
    val netFraction: Float, // -1.0f..1.0f relative to max absolute balance
)

@Immutable
data class MockTransferItem(
    val id: String,
    val from: MockMember,
    val to: MockMember,
    val amountFormatted: String,
)

@Immutable
data class MockRecordedPayment(
    val id: String,
    val fromName: String,
    val toName: String,
    val amountFormatted: String,
    val dateLabel: String,
    val note: String,
)

@Immutable
data class MockTripDetailState(
    val tripName: String,
    val homeCurrency: String,
    val totalSpentFormatted: String,
    val memberCount: Int,
    val expenseCount: Int,
    val isSettled: Boolean,
    val members: List<MockMember>,
    val selectedTab: MockTripDetailTab,
    val expenses: List<MockExpenseItem>,
    val balances: List<MockMemberBalance>,
    val transfers: List<MockTransferItem>,
    val recordedPayments: List<MockRecordedPayment>,
)

@Immutable
data class MockSplitParticipantRow(
    val member: MockMember,
    val included: Boolean,
    val inputValue: String,
    val computedOriginalFormatted: String,
    val computedHomeFormatted: String,
)

@Immutable
data class MockExpenseEditorState(
    val screenTitle: String,
    val amountDisplay: String,
    val currencyCode: String,
    val currencySymbol: String,
    val homeCurrencyCode: String,
    val convertedHomeFormatted: String,
    val fxRateSummary: String,
    val rateProvenance: MockRateProvenance,
    val fxFreshnessLabel: String,
    val isRateStale: Boolean,
    val title: String,
    val selectedCategory: MockExpenseCategory,
    val selectedPayerId: String,
    val members: List<MockMember>,
    val splitMode: MockSplitMode,
    val splitRows: List<MockSplitParticipantRow>,
    val isSplitBalanced: Boolean,
    val remainderMessage: String,
    val saveButtonLabel: String,
)

@Immutable
data class MockTripEditorMemberRow(
    val member: MockMember,
    val usageSummary: String,
    val canDelete: Boolean,
)

@Immutable
data class MockTripEditorState(
    val screenTitle: String,
    val tripName: String,
    val homeCurrencyCode: String,
    val homeCurrencyLabel: String,
    val newMemberDraft: String,
    val members: List<MockTripEditorMemberRow>,
    val integrityNotice: String?,
    val saveButtonLabel: String,
    val canSave: Boolean,
)

@Immutable
enum class MockThemeMode(val label: String) {
    SYSTEM("System"),
    LIGHT("Light"),
    DARK("Dark"),
}

@Immutable
data class MockFxRateRow(
    val currencyCode: String,
    val currencyName: String,
    val ratePerEurFormatted: String,
    val isBundledFallback: Boolean,
)

@Immutable
data class MockSettingsState(
    val defaultHomeCurrencyCode: String,
    val defaultHomeCurrencyName: String,
    val selectedThemeMode: MockThemeMode,
    val fxSyncStatus: MockFxSyncStatus,
    val fxSyncLabel: String,
    val lastSyncedSubtitle: String,
    val cachedRatesCount: Int,
    val rates: List<MockFxRateRow>,
    val hasDemoTrip: Boolean,
)

// Shared Reusable Visual Atoms

@Composable
fun AvatarInitials(
    initials: String,
    colorIndex: Int,
    size: Dp = 32.dp,
    borderColor: Color? = null,
    modifier: Modifier = Modifier,
) {
    val palette = SplitLedgerTheme.semanticColors.avatarColors
    val fill = palette[colorIndex % palette.size]
    val borderModifier = if (borderColor != null) {
        Modifier.border(1.5.dp, borderColor, CircleShape)
    } else {
        Modifier
    }
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(fill)
            .then(borderModifier),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initials,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
            ),
        )
    }
}

@Composable
fun MemberAvatarStack(
    members: List<MockMember>,
    borderColor: Color,
    avatarSize: Dp = 28.dp,
    modifier: Modifier = Modifier,
) {
    val visible = members.take(4)
    val overlap = 8.dp
    Box(modifier = modifier) {
        visible.forEachIndexed { index, member ->
            AvatarInitials(
                initials = member.initials,
                colorIndex = member.colorIndex,
                size = avatarSize,
                borderColor = borderColor,
                modifier = Modifier.offset(x = (avatarSize - overlap) * index),
            )
        }
    }
}

@Composable
fun FxSyncPill(
    status: MockFxSyncStatus,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val semantic = SplitLedgerTheme.semanticColors
    val (container, content, icon) = when (status) {
        MockFxSyncStatus.LIVE -> Triple(
            semantic.creditContainer,
            semantic.onCreditContainer,
            Icons.Rounded.CloudDone,
        )
        MockFxSyncStatus.CACHED_STALE -> Triple(
            semantic.warningContainer,
            semantic.onWarningContainer,
            Icons.Rounded.CloudOff,
        )
        MockFxSyncStatus.SYNCING -> Triple(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer,
            Icons.Rounded.Sync,
        )
        MockFxSyncStatus.OFFLINE -> Triple(
            MaterialTheme.colorScheme.surfaceContainerHigh,
            MaterialTheme.colorScheme.onSurfaceVariant,
            Icons.Rounded.CloudOff,
        )
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(999.dp),
        color = container,
        modifier = modifier
            .heightIn(min = 36.dp)
            .semantics { contentDescription = "Exchange rate status: $label" },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = content,
                modifier = Modifier.size(15.dp),
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.tabular(),
                color = content,
            )
        }
    }
}

@Composable
fun RateProvenanceBadge(
    provenance: MockRateProvenance,
    customLabel: String? = null,
    modifier: Modifier = Modifier,
) {
    if (provenance == MockRateProvenance.HOME && customLabel == null) return
    val semantic = SplitLedgerTheme.semanticColors
    val (bg, fg, defaultLabel) = when (provenance) {
        MockRateProvenance.LIVE -> Triple(
            semantic.creditContainer,
            semantic.onCreditContainer,
            "LIVE",
        )
        MockRateProvenance.CACHED -> Triple(
            semantic.warningContainer,
            semantic.onWarningContainer,
            "CACHED",
        )
        MockRateProvenance.MANUAL -> Triple(
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer,
            "MANUAL",
        )
        MockRateProvenance.HOME -> Triple(
            MaterialTheme.colorScheme.surfaceContainer,
            MaterialTheme.colorScheme.onSurfaceVariant,
            "HOME",
        )
    }
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = bg,
        modifier = modifier,
    ) {
        Text(
            text = customLabel ?: defaultLabel,
            style = MaterialTheme.typography.labelSmall.tabular(),
            color = fg,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
        )
    }
}

@Composable
fun CategoryIconBadge(
    category: MockExpenseCategory,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier.size(42.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = category.icon,
                contentDescription = category.label,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

// Realistic, Mathematically Reconciled Dummy Data

object SplitLedgerMockData {
    val alex = MockMember("m1", "Alex", "AL", 0)
    val maya = MockMember("m2", "Maya", "MY", 1)
    val kenji = MockMember("m3", "Kenji", "KJ", 2)
    val sam = MockMember("m4", "Sam", "SM", 3)
    val priya = MockMember("m5", "Priya", "PR", 4)
    val tom = MockMember("m6", "Tom", "TM", 1)

    val tokyoMembers = listOf(alex, maya, kenji, sam)

    val tripsHomeActive = MockTripsHomeState(
        selectedFilter = MockTripFilter.ACTIVE,
        activeCount = 2,
        settledCount = 1,
        totalActiveSpendFormatted = "$2,486.40",
        overallNetFormatted = "+$104.40",
        isOverallPositive = true,
        fxSyncStatus = MockFxSyncStatus.CACHED_STALE,
        fxSyncLabel = "FX Cached · 26h",
        trips = listOf(
            MockTripSummary(
                id = "trip_tokyo",
                name = "Tokyo Trip 2026",
                dateRange = "Nov 14 – Nov 19",
                homeCurrency = "USD",
                spentCurrencies = listOf("JPY", "EUR", "USD"),
                totalSpentFormatted = "$1,842.90",
                netBalanceFormatted = "+$142.80",
                isUserOwed = true,
                isSettled = false,
                isDemo = true,
                hasStaleFx = true,
                expenseCount = 5,
                members = tokyoMembers,
            ),
            MockTripSummary(
                id = "trip_lisbon",
                name = "Lisbon Long Weekend",
                dateRange = "Oct 24 – Oct 27",
                homeCurrency = "EUR",
                spentCurrencies = listOf("EUR", "GBP"),
                totalSpentFormatted = "€643.50",
                netBalanceFormatted = "-€38.40",
                isUserOwed = false,
                isSettled = false,
                isDemo = false,
                hasStaleFx = false,
                expenseCount = 8,
                members = listOf(alex, priya, tom),
            ),
        ),
    )

    val tokyoExpenses = listOf(
        MockExpenseItem(
            id = "e1",
            title = "Omoide Yokocho Izakaya",
            dateGroup = "Tue, Nov 18",
            category = MockExpenseCategory.FOOD,
            payerName = "Alex",
            splitSummary = "Equal · 4 people",
            originalAmountFormatted = "¥18,400",
            homeAmountFormatted = "$123.28",
            rateProvenance = MockRateProvenance.CACHED,
        ),
        MockExpenseItem(
            id = "e2",
            title = "TeamLab Planets Tickets",
            dateGroup = "Tue, Nov 18",
            category = MockExpenseCategory.ACTIVITY,
            payerName = "Sam",
            splitSummary = "Percent · 4 people",
            originalAmountFormatted = "€112.00",
            homeAmountFormatted = "$120.96",
            rateProvenance = MockRateProvenance.LIVE,
        ),
        MockExpenseItem(
            id = "e3",
            title = "Shinjuku Granbell Hotel",
            dateGroup = "Mon, Nov 17",
            category = MockExpenseCategory.STAY,
            payerName = "Maya",
            splitSummary = "Exact · 4 people",
            originalAmountFormatted = "¥142,000",
            homeAmountFormatted = "$951.40",
            rateProvenance = MockRateProvenance.LIVE,
        ),
        MockExpenseItem(
            id = "e4",
            title = "Narita Express Round-Trip",
            dateGroup = "Mon, Nov 17",
            category = MockExpenseCategory.TRANSPORT,
            payerName = "Kenji",
            splitSummary = "Equal · 4 people",
            originalAmountFormatted = "¥16,280",
            homeAmountFormatted = "$109.08",
            rateProvenance = MockRateProvenance.MANUAL,
        ),
        MockExpenseItem(
            id = "e5",
            title = "eSIMs & Lounge Access",
            dateGroup = "Sun, Nov 16",
            category = MockExpenseCategory.OTHER,
            payerName = "Alex",
            splitSummary = "Equal · 4 people",
            originalAmountFormatted = "$68.00",
            homeAmountFormatted = "$68.00",
            rateProvenance = MockRateProvenance.HOME,
        ),
    )

    // Reconciled balances: +218.50 + 142.80 - 167.20 - 194.10 == 0.00
    val tokyoBalances = listOf(
        MockMemberBalance(
            member = maya,
            paidFormatted = "$951.40",
            shareFormatted = "$732.90",
            netFormatted = "+$218.50",
            netFraction = 1.00f,
        ),
        MockMemberBalance(
            member = alex,
            paidFormatted = "$486.20",
            shareFormatted = "$343.40",
            netFormatted = "+$142.80",
            netFraction = 0.65f,
        ),
        MockMemberBalance(
            member = sam,
            paidFormatted = "$246.22",
            shareFormatted = "$413.42",
            netFormatted = "-$167.20",
            netFraction = -0.77f,
        ),
        MockMemberBalance(
            member = kenji,
            paidFormatted = "$159.08",
            shareFormatted = "$353.18",
            netFormatted = "-$194.10",
            netFraction = -0.89f,
        ),
    )

    // Minimal transfers zeroing all 4 balances exactly:
    // Kenji pays Maya $194.10 -> Kenji 0, Maya +24.40
    // Sam pays Alex $142.80 -> Alex 0, Sam -24.40
    // Sam pays Maya $24.40 -> Sam 0, Maya 0
    val tokyoTransfers = listOf(
        MockTransferItem("tr1", from = kenji, to = maya, amountFormatted = "$194.10"),
        MockTransferItem("tr2", from = sam, to = alex, amountFormatted = "$142.80"),
        MockTransferItem("tr3", from = sam, to = maya, amountFormatted = "$24.40"),
    )

    val tokyoRecordedPayments = listOf(
        MockRecordedPayment(
            id = "p1",
            fromName = "Kenji",
            toName = "Alex",
            amountFormatted = "$50.00",
            dateLabel = "Nov 18",
            note = "Partial cash settlement",
        ),
    )

    fun tokyoDetail(tab: MockTripDetailTab): MockTripDetailState = MockTripDetailState(
        tripName = "Tokyo Trip 2026",
        homeCurrency = "USD",
        totalSpentFormatted = "$1,842.90",
        memberCount = 4,
        expenseCount = 5,
        isSettled = false,
        members = tokyoMembers,
        selectedTab = tab,
        expenses = tokyoExpenses,
        balances = tokyoBalances,
        transfers = tokyoTransfers,
        recordedPayments = tokyoRecordedPayments,
    )

    val expenseEditorEqualCached = MockExpenseEditorState(
        screenTitle = "Add expense",
        amountDisplay = "18,400",
        currencyCode = "JPY",
        currencySymbol = "¥",
        homeCurrencyCode = "USD",
        convertedHomeFormatted = "$123.28",
        fxRateSummary = "1 JPY = 0.00670 USD",
        rateProvenance = MockRateProvenance.CACHED,
        fxFreshnessLabel = "Cached · 26h",
        isRateStale = true,
        title = "Omoide Yokocho Izakaya",
        selectedCategory = MockExpenseCategory.FOOD,
        selectedPayerId = alex.id,
        members = tokyoMembers,
        splitMode = MockSplitMode.EQUAL,
        splitRows = listOf(
            MockSplitParticipantRow(alex, true, "", "¥4,600", "$30.82"),
            MockSplitParticipantRow(maya, true, "", "¥4,600", "$30.82"),
            MockSplitParticipantRow(kenji, true, "", "¥4,600", "$30.82"),
            MockSplitParticipantRow(sam, true, "", "¥4,600", "$30.82"),
        ),
        isSplitBalanced = true,
        remainderMessage = "Balanced · ¥18,400 of ¥18,400",
        saveButtonLabel = "Save expense · $123.28",
    )

    val expenseEditorPercentWarning = MockExpenseEditorState(
        screenTitle = "Edit expense",
        amountDisplay = "142,000",
        currencyCode = "JPY",
        currencySymbol = "¥",
        homeCurrencyCode = "USD",
        convertedHomeFormatted = "$951.40",
        fxRateSummary = "1 JPY = 0.00670 USD",
        rateProvenance = MockRateProvenance.MANUAL,
        fxFreshnessLabel = "Manual card rate",
        isRateStale = false,
        title = "Shinjuku Granbell Hotel",
        selectedCategory = MockExpenseCategory.STAY,
        selectedPayerId = maya.id,
        members = tokyoMembers,
        splitMode = MockSplitMode.PERCENT,
        splitRows = listOf(
            MockSplitParticipantRow(alex, true, "30.0%", "¥42,600", "$285.42"),
            MockSplitParticipantRow(maya, true, "30.0%", "¥42,600", "$285.42"),
            MockSplitParticipantRow(kenji, true, "20.0%", "¥28,400", "$190.28"),
            MockSplitParticipantRow(sam, true, "16.0%", "¥22,720", "$152.22"),
        ),
        isSplitBalanced = false,
        remainderMessage = "4.0% remaining to assign (¥5,680)",
        saveButtonLabel = "Adjust split to save",
    )

    val nina = MockMember("m7", "Nina", "NI", 4)

    val tripEditorEditTokyo = MockTripEditorState(
        screenTitle = "Edit trip",
        tripName = "Tokyo Trip 2026",
        homeCurrencyCode = "USD",
        homeCurrencyLabel = "US Dollar ($)",
        newMemberDraft = "",
        members = listOf(
            MockTripEditorMemberRow(alex, "Paid 2 expenses · In 5 splits", canDelete = false),
            MockTripEditorMemberRow(maya, "Paid 1 expense · In 5 splits", canDelete = false),
            MockTripEditorMemberRow(kenji, "Paid 1 expense · 1 settlement", canDelete = false),
            MockTripEditorMemberRow(sam, "Paid 1 expense · In 5 splits", canDelete = false),
            MockTripEditorMemberRow(nina, "No expenses yet", canDelete = true),
        ),
        integrityNotice = "Maya can't be removed while linked to existing expenses or settlements.",
        saveButtonLabel = "Save trip changes",
        canSave = true,
    )

    val tripEditorCreateNew = MockTripEditorState(
        screenTitle = "New trip",
        tripName = "Seoul Food Crawl",
        homeCurrencyCode = "USD",
        homeCurrencyLabel = "US Dollar ($)",
        newMemberDraft = "Devon",
        members = listOf(
            MockTripEditorMemberRow(alex, "Trip creator", canDelete = true),
            MockTripEditorMemberRow(priya, "Ready to split", canDelete = true),
        ),
        integrityNotice = null,
        saveButtonLabel = "Create trip",
        canSave = true,
    )

    val fxRatesSampleStale = listOf(
        MockFxRateRow("USD", "US Dollar", "1.0842", isBundledFallback = true),
        MockFxRateRow("JPY", "Japanese Yen", "161.85", isBundledFallback = true),
        MockFxRateRow("GBP", "British Pound", "0.8541", isBundledFallback = true),
        MockFxRateRow("SGD", "Singapore Dollar", "1.4480", isBundledFallback = true),
        MockFxRateRow("KRW", "South Korean Won", "1,492.40", isBundledFallback = true),
        MockFxRateRow("AUD", "Australian Dollar", "1.6520", isBundledFallback = true),
        MockFxRateRow("THB", "Thai Baht", "37.62", isBundledFallback = true),
        MockFxRateRow("IDR", "Indonesian Rupiah", "17,180", isBundledFallback = true),
    )

    val fxRatesSampleLive = fxRatesSampleStale.map { it.copy(isBundledFallback = false) }

    val settingsStale = MockSettingsState(
        defaultHomeCurrencyCode = "USD",
        defaultHomeCurrencyName = "US Dollar ($)",
        selectedThemeMode = MockThemeMode.SYSTEM,
        fxSyncStatus = MockFxSyncStatus.CACHED_STALE,
        fxSyncLabel = "FX Cached · 26h",
        lastSyncedSubtitle = "Using bundled offline rates · Sync when online",
        cachedRatesCount = 20,
        rates = fxRatesSampleStale,
        hasDemoTrip = true,
    )

    val settingsSynced = MockSettingsState(
        defaultHomeCurrencyCode = "EUR",
        defaultHomeCurrencyName = "Euro (€)",
        selectedThemeMode = MockThemeMode.DARK,
        fxSyncStatus = MockFxSyncStatus.LIVE,
        fxSyncLabel = "FX Live · 4m",
        lastSyncedSubtitle = "ECB rates via Frankfurter · Updated Nov 19, 12:07",
        cachedRatesCount = 20,
        rates = fxRatesSampleLive,
        hasDemoTrip = true,
    )
}
