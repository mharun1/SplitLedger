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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.CurrencyExchange
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harun.splitledger.ui.theme.SplitLedgerTheme
import com.harun.splitledger.ui.theme.tabular

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsMockupScreen(
    state: MockSettingsState,
    onBackClick: () -> Unit = {},
    onDefaultCurrencyClick: () -> Unit = {},
    onThemeModeSelected: (MockThemeMode) -> Unit = {},
    onSyncNowClick: () -> Unit = {},
    onDemoTripActionClick: () -> Unit = {},
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
                            contentDescription = "Back",
                        )
                    }
                },
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge,
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
                bottom = 32.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            // General Preferences
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Preferences",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(horizontal = 4.dp),
                    )
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerLowest,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(onClick = onDefaultCurrencyClick)
                                    .heightIn(min = 60.dp)
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.CurrencyExchange,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp),
                                    )
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(
                                            text = "Default home currency",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.SemiBold,
                                            ),
                                            color = MaterialTheme.colorScheme.onSurface,
                                        )
                                        Text(
                                            text = "Pre-selected when creating new trips",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = state.defaultHomeCurrencyCode,
                                            style = MaterialTheme.typography.titleSmall.tabular().copy(
                                                fontWeight = FontWeight.SemiBold,
                                            ),
                                            color = MaterialTheme.colorScheme.primary,
                                        )
                                        Text(
                                            text = state.defaultHomeCurrencyName,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Rounded.ChevronRight,
                                        contentDescription = "Change default currency",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(20.dp),
                                    )
                                }
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.outlineVariant,
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                Text(
                                    text = "Appearance",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                SingleChoiceSegmentedButtonRow(
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    MockThemeMode.entries.forEachIndexed { index, mode ->
                                        SegmentedButton(
                                            selected = state.selectedThemeMode == mode,
                                            onClick = { onThemeModeSelected(mode) },
                                            shape = SegmentedButtonDefaults.itemShape(
                                                index = index,
                                                count = MockThemeMode.entries.size,
                                            ),
                                            label = {
                                                Text(
                                                    text = mode.label,
                                                    style = MaterialTheme.typography.labelLarge,
                                                )
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Exchange Rate Cache & WorkManager Inspector
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Exchange rates",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        FxSyncPill(
                            status = state.fxSyncStatus,
                            label = state.fxSyncLabel,
                            onClick = onSyncNowClick,
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerLowest,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(2.dp),
                                ) {
                                    Text(
                                        text = "${state.cachedRatesCount} currencies cached offline",
                                        style = MaterialTheme.typography.titleSmall.tabular().copy(
                                            fontWeight = FontWeight.SemiBold,
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                    Text(
                                        text = state.lastSyncedSubtitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                FilledTonalButton(
                                    onClick = onSyncNowClick,
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp),
                                    modifier = Modifier.heightIn(min = 48.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Sync,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Sync now",
                                        style = MaterialTheme.typography.labelLarge,
                                    )
                                }
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                            // Table Column Header
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    text = "Currency",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Text(
                                    text = "1 EUR equals",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                            state.rates.forEachIndexed { index, rate ->
                                FxRateInspectorRow(rate = rate)
                                if (index < state.rates.lastIndex) {
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

            // Portfolio Demo Workspace Control
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Sample workspace",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(horizontal = 4.dp),
                    )
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerLowest,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f),
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Explore,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp),
                                )
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = "Tokyo Trip 2026 demo",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.SemiBold,
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                    Text(
                                        text = "Restore multi-currency sample expenses and transfers",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedButton(
                                onClick = onDemoTripActionClick,
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp),
                                modifier = Modifier.heightIn(min = 48.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.RestartAlt,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (state.hasDemoTrip) "Reset" else "Load",
                                    style = MaterialTheme.typography.labelLarge,
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
private fun FxRateInspectorRow(
    rate: MockFxRateRow,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 11.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = rate.currencyCode,
                style = MaterialTheme.typography.titleSmall.tabular().copy(
                    fontWeight = FontWeight.SemiBold,
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.width(40.dp),
            )
            Text(
                text = rate.currencyName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (rate.isBundledFallback) {
                RateProvenanceBadge(
                    provenance = MockRateProvenance.CACHED,
                    customLabel = "SEED",
                )
            }
            Text(
                text = rate.ratePerEurFormatted,
                style = MaterialTheme.typography.titleSmall.tabular().copy(
                    fontWeight = FontWeight.SemiBold,
                ),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Preview(
    name = "Screen 5A — Settings & FX Inspector Stale (Light)",
    showSystemUi = true,
    device = Devices.PIXEL_7,
)
@Composable
private fun SettingsStaleLightPreview() {
    SplitLedgerTheme(darkTheme = false) {
        SettingsMockupScreen(state = SplitLedgerMockData.settingsStale)
    }
}

@Preview(
    name = "Screen 5B — Settings & FX Inspector Live (Dark)",
    showSystemUi = true,
    device = Devices.PIXEL_7,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun SettingsLiveDarkPreview() {
    SplitLedgerTheme(darkTheme = true) {
        SettingsMockupScreen(state = SplitLedgerMockData.settingsSynced)
    }
}
