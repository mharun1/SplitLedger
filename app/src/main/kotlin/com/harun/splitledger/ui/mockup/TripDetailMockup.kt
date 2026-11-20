package com.harun.splitledger.ui.mockup

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harun.splitledger.ui.theme.SplitLedgerTheme
import com.harun.splitledger.ui.theme.tabular
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailMockupScreen(
    state: MockTripDetailState,
    onBackClick: () -> Unit = {},
    onEditTripClick: () -> Unit = {},
    onTabSelected: (MockTripDetailTab) -> Unit = {},
    onExpenseClick: (String) -> Unit = {},
    onAddExpenseClick: () -> Unit = {},
    onMarkPaidClick: (String) -> Unit = {},
    onShareSummaryClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                ),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back to trips",
                        )
                    }
                },
                title = {
                    Text(
                        text = state.tripName,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                actions = {
                    IconButton(onClick = onEditTripClick) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = "Edit trip and members",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            if (state.selectedTab == MockTripDetailTab.EXPENSES) {
                ExtendedFloatingActionButton(
                    onClick = onAddExpenseClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(16.dp),
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = null,
                        )
                    },
                    text = {
                        Text(
                            text = "Add expense",
                            style = MaterialTheme.typography.labelLarge,
                        )
                    },
                )
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 4.dp,
                bottom = 96.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            // Compact Architectural Trip Ledger Header
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Text(
                                    text = state.totalSpentFormatted,
                                    style = MaterialTheme.typography.displaySmall.tabular(),
                                    color = MaterialTheme.colorScheme.onBackground,
                                )
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceContainer,
                                ) {
                                    Text(
                                        text = state.homeCurrency,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    )
                                }
                            }
                            Text(
                                text = "${state.memberCount} members · ${state.expenseCount} expenses logged",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }

                        val stackWidth = (30 + (state.members.take(4).size - 1) * 22).dp
                        MemberAvatarStack(
                            members = state.members,
                            borderColor = MaterialTheme.colorScheme.background,
                            avatarSize = 30.dp,
                            modifier = Modifier.width(stackWidth),
                        )
                    }

                    // 3-Tab Material 3 Segmented Control
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        MockTripDetailTab.entries.forEachIndexed { index, tab ->
                            SegmentedButton(
                                selected = state.selectedTab == tab,
                                onClick = { onTabSelected(tab) },
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = MockTripDetailTab.entries.size,
                                ),
                                label = {
                                    Text(
                                        text = tab.label,
                                        style = MaterialTheme.typography.labelLarge,
                                    )
                                },
                            )
                        }
                    }
                }
            }

            when (state.selectedTab) {
                MockTripDetailTab.EXPENSES -> {
                    val grouped = state.expenses.groupBy { it.dateGroup }
                    grouped.forEach { (dateLabel, items) ->
                        item(key = "group_$dateLabel") {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = dateLabel,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 4.dp),
                                )
                                Surface(
                                    shape = RoundedCornerShape(18.dp),
                                    color = MaterialTheme.colorScheme.surfaceContainerLowest,
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Column {
                                        items.forEachIndexed { idx, expense ->
                                            ExpenseLedgerRow(
                                                expense = expense,
                                                onClick = { onExpenseClick(expense.id) },
                                            )
                                            if (idx < items.lastIndex) {
                                                HorizontalDivider(
                                                    modifier = Modifier.padding(horizontal = 16.dp),
                                                    color = MaterialTheme.colorScheme.outlineVariant,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                MockTripDetailTab.BALANCES -> {
                    item(key = "balances_surface") {
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerLowest,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text(
                                        text = "Net balances (${state.homeCurrency})",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                    Text(
                                        text = "Owes ← 0 → Owed",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                state.balances.forEachIndexed { index, balance ->
                                    DivergingBalanceRow(balance = balance)
                                    if (index < state.balances.lastIndex) {
                                        HorizontalDivider(
                                            modifier = Modifier.padding(horizontal = 16.dp),
                                            color = MaterialTheme.colorScheme.outlineVariant,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                MockTripDetailTab.SETTLE -> {
                    item(key = "settle_plan") {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "Suggested transfers",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onBackground,
                                )
                                Text(
                                    text = "${state.transfers.size} moves settle all ${state.memberCount}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Column {
                                    state.transfers.forEachIndexed { index, transfer ->
                                        TransferPlanRow(
                                            transfer = transfer,
                                            onMarkPaid = { onMarkPaidClick(transfer.id) },
                                        )
                                        if (index < state.transfers.lastIndex) {
                                            HorizontalDivider(
                                                modifier = Modifier.padding(horizontal = 16.dp),
                                                color = MaterialTheme.colorScheme.outlineVariant,
                                            )
                                        }
                                    }
                                }
                            }

                            OutlinedButton(
                                onClick = onShareSummaryClick,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 48.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Share,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Share settlement summary",
                                    style = MaterialTheme.typography.labelLarge,
                                )
                            }
                        }
                    }

                    if (state.recordedPayments.isNotEmpty()) {
                        item(key = "recorded_payments") {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "Recorded payments",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 4.dp),
                                )
                                Surface(
                                    shape = RoundedCornerShape(18.dp),
                                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Column {
                                        state.recordedPayments.forEachIndexed { index, payment ->
                                            RecordedPaymentRow(payment = payment)
                                            if (index < state.recordedPayments.lastIndex) {
                                                HorizontalDivider(
                                                    modifier = Modifier.padding(horizontal = 16.dp),
                                                    color = MaterialTheme.colorScheme.outlineVariant,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpenseLedgerRow(
    expense: MockExpenseItem,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CategoryIconBadge(category = expense.category)

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Text(
                text = expense.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "Paid by ${expense.payerName} · ${expense.splitSummary}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Text(
                text = expense.originalAmountFormatted,
                style = MaterialTheme.typography.titleSmall.tabular().copy(
                    fontWeight = FontWeight.SemiBold,
                ),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                if (expense.rateProvenance != MockRateProvenance.HOME) {
                    Text(
                        text = expense.homeAmountFormatted,
                        style = MaterialTheme.typography.bodySmall.tabular(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    RateProvenanceBadge(provenance = expense.rateProvenance)
                } else {
                    Text(
                        text = "Home currency",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun DivergingBalanceRow(
    balance: MockMemberBalance,
) {
    val semantic = SplitLedgerTheme.semanticColors
    val isPositive = balance.netFraction >= 0f
    val accent = if (isPositive) semantic.credit else semantic.debt
    val magnitude = abs(balance.netFraction).coerceIn(0.04f, 1f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                AvatarInitials(
                    initials = balance.member.initials,
                    colorIndex = balance.member.colorIndex,
                    size = 34.dp,
                )
                Column {
                    Text(
                        text = balance.member.name,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = "Paid ${balance.paidFormatted} · Share ${balance.shareFormatted}",
                        style = MaterialTheme.typography.bodySmall.tabular(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Text(
                text = balance.netFormatted,
                style = MaterialTheme.typography.titleMedium.tabular(),
                color = accent,
            )
        }

        // Signature Diverging Balance Track anchored at exact center zero-axis
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLow),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Left half: debt (grows right-to-left from center)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.CenterEnd,
            ) {
                if (!isPositive) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(magnitude)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(topStart = 999.dp, bottomStart = 999.dp))
                            .background(semantic.debt),
                    )
                }
            }

            // Center zero axis rule
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.outline),
            )

            // Right half: credit (grows left-to-right from center)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (isPositive) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(magnitude)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(topEnd = 999.dp, bottomEnd = 999.dp))
                            .background(semantic.credit),
                    )
                }
            }
        }
    }
}

@Composable
private fun TransferPlanRow(
    transfer: MockTransferItem,
    onMarkPaid: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AvatarInitials(
                    initials = transfer.from.initials,
                    colorIndex = transfer.from.colorIndex,
                    size = 28.dp,
                )
                Text(
                    text = transfer.from.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = "pays",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp),
                )
                AvatarInitials(
                    initials = transfer.to.initials,
                    colorIndex = transfer.to.colorIndex,
                    size = 28.dp,
                )
                Text(
                    text = transfer.to.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Text(
                text = transfer.amountFormatted,
                style = MaterialTheme.typography.titleLarge.tabular(),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        FilledTonalButton(
            onClick = onMarkPaid,
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
            modifier = Modifier.heightIn(min = 48.dp),
        ) {
            Text(
                text = "Mark paid",
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Composable
private fun RecordedPaymentRow(
    payment: MockRecordedPayment,
) {
    val semantic = SplitLedgerTheme.semanticColors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Rounded.CheckCircle,
            contentDescription = "Settled",
            tint = semantic.credit,
            modifier = Modifier.size(20.dp),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = "${payment.fromName} paid ${payment.toName}",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "${payment.note} · ${payment.dateLabel}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = payment.amountFormatted,
            style = MaterialTheme.typography.titleSmall.tabular().copy(fontWeight = FontWeight.SemiBold),
            color = semantic.credit,
        )
    }
}

@Preview(
    name = "Screen 2A — Trip Detail Expenses (Light)",
    showSystemUi = true,
    device = Devices.PIXEL_7,
)
@Composable
private fun TripDetailExpensesLightPreview() {
    SplitLedgerTheme(darkTheme = false) {
        TripDetailMockupScreen(
            state = SplitLedgerMockData.tokyoDetail(MockTripDetailTab.EXPENSES),
        )
    }
}

@Preview(
    name = "Screen 2B — Trip Detail Balances (Light)",
    showSystemUi = true,
    device = Devices.PIXEL_7,
)
@Composable
private fun TripDetailBalancesLightPreview() {
    SplitLedgerTheme(darkTheme = false) {
        TripDetailMockupScreen(
            state = SplitLedgerMockData.tokyoDetail(MockTripDetailTab.BALANCES),
        )
    }
}

@Preview(
    name = "Screen 2C — Trip Detail Settle (Light)",
    showSystemUi = true,
    device = Devices.PIXEL_7,
)
@Composable
private fun TripDetailSettleLightPreview() {
    SplitLedgerTheme(darkTheme = false) {
        TripDetailMockupScreen(
            state = SplitLedgerMockData.tokyoDetail(MockTripDetailTab.SETTLE),
        )
    }
}

@Preview(
    name = "Screen 2D — Trip Detail Balances (Dark)",
    showSystemUi = true,
    device = Devices.PIXEL_7,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun TripDetailBalancesDarkPreview() {
    SplitLedgerTheme(darkTheme = true) {
        TripDetailMockupScreen(
            state = SplitLedgerMockData.tokyoDetail(MockTripDetailTab.BALANCES),
        )
    }
}
