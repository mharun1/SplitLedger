package com.harun.splitledger.ui.mockup

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harun.splitledger.ui.theme.SplitLedgerTheme
import com.harun.splitledger.ui.theme.tabular

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripsHomeMockupScreen(
    state: MockTripsHomeState,
    onFilterSelected: (MockTripFilter) -> Unit = {},
    onSyncClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onTripClick: (String) -> Unit = {},
    onCreateTripClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val semantic = SplitLedgerTheme.semanticColors

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                ),
                title = {
                    Text(
                        text = "SplitLedger",
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                actions = {
                    FxSyncPill(
                        status = state.fxSyncStatus,
                        label = state.fxSyncLabel,
                        onClick = onSyncClick,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "Settings and exchange rates",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCreateTripClick,
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
                        text = "New trip",
                        style = MaterialTheme.typography.labelLarge,
                    )
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 8.dp,
                bottom = 96.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // Architectural Ledger Summary Strip (clean, unboxed, tabular)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = state.totalActiveSpendFormatted,
                                style = MaterialTheme.typography.displaySmall.tabular(),
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                            Text(
                                text = "Active group spend across ${state.activeCount} trips",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }

                        val netBg = if (state.isOverallPositive) {
                            semantic.creditContainer
                        } else {
                            semantic.debtContainer
                        }
                        val netFg = if (state.isOverallPositive) {
                            semantic.onCreditContainer
                        } else {
                            semantic.onDebtContainer
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = netBg,
                        ) {
                            Text(
                                text = if (state.isOverallPositive) {
                                    "Owed ${state.overallNetFormatted}"
                                } else {
                                    "Owe ${state.overallNetFormatted}"
                                },
                                style = MaterialTheme.typography.labelLarge.tabular(),
                                color = netFg,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }

            // Native Material 3 Single-Choice Segmented Filter
            item {
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    val filters = listOf(
                        MockTripFilter.ACTIVE to "Active (${state.activeCount})",
                        MockTripFilter.SETTLED to "Settled (${state.settledCount})",
                    )
                    filters.forEachIndexed { index, (filter, label) ->
                        SegmentedButton(
                            selected = state.selectedFilter == filter,
                            onClick = { onFilterSelected(filter) },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = filters.size,
                            ),
                            label = {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelLarge.tabular(),
                                )
                            },
                        )
                    }
                }
            }

            // Grouped Trip Ledger Surface (zero nested cards; crisp hairline separation)
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLowest,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        state.trips.forEachIndexed { index, trip ->
                            TripLedgerRow(
                                trip = trip,
                                onClick = { onTripClick(trip.id) },
                            )
                            if (index < state.trips.lastIndex) {
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

@Composable
private fun TripLedgerRow(
    trip: MockTripSummary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val semantic = SplitLedgerTheme.semanticColors
    val surfaceColor = MaterialTheme.colorScheme.surfaceContainerLowest

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = trip.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (trip.isDemo) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer,
                        ) {
                            Text(
                                text = "Demo",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            )
                        }
                    }
                }
                Text(
                    text = "${trip.dateRange} · ${trip.spentCurrencies.joinToString(", ")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = trip.totalSpentFormatted,
                        style = MaterialTheme.typography.titleMedium.tabular(),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = trip.homeCurrency,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Icon(
                    imageVector = Icons.Rounded.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                val stackWidth = (28 + (trip.members.take(4).size - 1) * 20).dp
                MemberAvatarStack(
                    members = trip.members,
                    borderColor = surfaceColor,
                    modifier = Modifier.width(stackWidth),
                )
                Text(
                    text = "${trip.members.size} people · ${trip.expenseCount} expenses",
                    style = MaterialTheme.typography.bodySmall.tabular(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (trip.hasStaleFx) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Schedule,
                            contentDescription = "Stale exchange rate",
                            tint = semantic.warning,
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            text = "Stale FX",
                            style = MaterialTheme.typography.labelSmall,
                            color = semantic.warning,
                        )
                    }
                }
                val balanceColor = if (trip.isUserOwed) semantic.credit else semantic.debt
                Text(
                    text = trip.netBalanceFormatted,
                    style = MaterialTheme.typography.labelLarge.tabular().copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color = balanceColor,
                )
            }
        }
    }
}

@Preview(
    name = "Screen 1 — Trips Home (Light)",
    showSystemUi = true,
    device = Devices.PIXEL_7,
)
@Composable
private fun TripsHomeLightPreview() {
    SplitLedgerTheme(darkTheme = false) {
        TripsHomeMockupScreen(state = SplitLedgerMockData.tripsHomeActive)
    }
}

@Preview(
    name = "Screen 1 — Trips Home (Dark)",
    showSystemUi = true,
    device = Devices.PIXEL_7,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun TripsHomeDarkPreview() {
    SplitLedgerTheme(darkTheme = true) {
        TripsHomeMockupScreen(state = SplitLedgerMockData.tripsHomeActive)
    }
}
