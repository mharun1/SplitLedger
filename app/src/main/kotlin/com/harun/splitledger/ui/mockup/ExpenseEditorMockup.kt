package com.harun.splitledger.ui.mockup

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CurrencyExchange
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
fun ExpenseEditorMockupScreen(
    state: MockExpenseEditorState,
    onCloseClick: () -> Unit = {},
    onCurrencyClick: () -> Unit = {},
    onEditRateClick: () -> Unit = {},
    onTitleChange: (String) -> Unit = {},
    onCategorySelected: (MockExpenseCategory) -> Unit = {},
    onPayerSelected: (String) -> Unit = {},
    onSplitModeSelected: (MockSplitMode) -> Unit = {},
    onParticipantToggled: (String) -> Unit = {},
    onSaveClick: () -> Unit = {},
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
                navigationIcon = {
                    IconButton(onClick = onCloseClick) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Close expense editor",
                        )
                    }
                },
                title = {
                    Text(
                        text = state.screenTitle,
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.background,
                tonalElevation = 2.dp,
            ) {
                Column(modifier = Modifier.navigationBarsPadding()) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Button(
                        onClick = onSaveClick,
                        enabled = state.isSplitBalanced,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .heightIn(min = 52.dp),
                    ) {
                        Text(
                            text = state.saveButtonLabel,
                            style = MaterialTheme.typography.labelLarge.tabular(),
                        )
                    }
                }
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
                top = 8.dp,
                bottom = 24.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // Currency-First Amount Stage + Live FX Strip
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLowest,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 18.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Text(
                                    text = state.currencySymbol,
                                    style = MaterialTheme.typography.headlineMedium.tabular(),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Text(
                                    text = state.amountDisplay,
                                    style = MaterialTheme.typography.displaySmall.tabular(),
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            }

                            Surface(
                                onClick = onCurrencyClick,
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.heightIn(min = 48.dp),
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    Text(
                                        text = state.currencyCode,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.SemiBold,
                                        ),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    )
                                    Icon(
                                        imageVector = Icons.Rounded.KeyboardArrowDown,
                                        contentDescription = "Select currency",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(18.dp),
                                    )
                                }
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                        // Live FX Conversion Preview + Provenance & Tap-to-Override
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 8.dp, top = 10.dp, bottom = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.CurrencyExchange,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp),
                                    )
                                    Text(
                                        text = "≈ ${state.convertedHomeFormatted} ${state.homeCurrencyCode}",
                                        style = MaterialTheme.typography.titleSmall.tabular().copy(
                                            fontWeight = FontWeight.SemiBold,
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                    RateProvenanceBadge(
                                        provenance = state.rateProvenance,
                                        customLabel = state.fxFreshnessLabel,
                                    )
                                }
                                Text(
                                    text = state.fxRateSummary,
                                    style = MaterialTheme.typography.bodySmall.tabular(),
                                    color = if (state.isRateStale) {
                                        semantic.warning
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                )
                            }

                            TextButton(
                                onClick = onEditRateClick,
                                modifier = Modifier.heightIn(min = 48.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp),
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Override",
                                    style = MaterialTheme.typography.labelLarge,
                                )
                            }
                        }
                    }
                }
            }

            // Title & Fixed 6 Categories
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = state.title,
                        onValueChange = onTitleChange,
                        label = { Text("Description") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        MockExpenseCategory.entries.forEach { category ->
                            val selected = state.selectedCategory == category
                            FilterChip(
                                selected = selected,
                                onClick = { onCategorySelected(category) },
                                label = {
                                    Text(
                                        text = category.label,
                                        style = MaterialTheme.typography.labelLarge,
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = category.icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                ),
                                modifier = Modifier.heightIn(min = 40.dp),
                            )
                        }
                    }
                }
            }

            // Single-Payer Selector
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Paid by",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(horizontal = 4.dp),
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        state.members.forEach { member ->
                            val selected = member.id == state.selectedPayerId
                            FilterChip(
                                selected = selected,
                                onClick = { onPayerSelected(member.id) },
                                label = {
                                    Text(
                                        text = member.name,
                                        style = MaterialTheme.typography.labelLarge,
                                    )
                                },
                                leadingIcon = {
                                    AvatarInitials(
                                        initials = member.initials,
                                        colorIndex = member.colorIndex,
                                        size = 22.dp,
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                ),
                                modifier = Modifier.heightIn(min = 44.dp),
                            )
                        }
                    }
                }
            }

            // Split Mode Switcher + Live Remainder Pill + Member Allocation Rows
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Split between",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(horizontal = 4.dp),
                    )

                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        MockSplitMode.entries.forEachIndexed { index, mode ->
                            SegmentedButton(
                                selected = state.splitMode == mode,
                                onClick = { onSplitModeSelected(mode) },
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = MockSplitMode.entries.size,
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

                    // Live Remainder Validation Pill
                    val pillBg = if (state.isSplitBalanced) {
                        semantic.creditContainer
                    } else {
                        semantic.debtContainer
                    }
                    val pillFg = if (state.isSplitBalanced) {
                        semantic.onCreditContainer
                    } else {
                        semantic.onDebtContainer
                    }
                    val pillIcon = if (state.isSplitBalanced) {
                        Icons.Rounded.CheckCircle
                    } else {
                        Icons.Rounded.ErrorOutline
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = pillBg,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(
                                imageVector = pillIcon,
                                contentDescription = null,
                                tint = pillFg,
                                modifier = Modifier.size(18.dp),
                            )
                            Text(
                                text = state.remainderMessage,
                                style = MaterialTheme.typography.labelLarge.tabular(),
                                color = pillFg,
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerLowest,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column {
                            state.splitRows.forEachIndexed { index, row ->
                                SplitParticipantRow(
                                    row = row,
                                    splitMode = state.splitMode,
                                    onToggle = { onParticipantToggled(row.member.id) },
                                )
                                if (index < state.splitRows.lastIndex) {
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

@Composable
private fun SplitParticipantRow(
    row: MockSplitParticipantRow,
    splitMode: MockSplitMode,
    onToggle: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 58.dp)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (splitMode == MockSplitMode.EQUAL) {
                Checkbox(
                    checked = row.included,
                    onCheckedChange = { onToggle() },
                )
            } else {
                Spacer(modifier = Modifier.width(4.dp))
            }
            AvatarInitials(
                initials = row.member.initials,
                colorIndex = row.member.colorIndex,
                size = 30.dp,
            )
            Text(
                text = row.member.name,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        if (splitMode == MockSplitMode.EQUAL) {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = row.computedOriginalFormatted,
                    style = MaterialTheme.typography.titleSmall.tabular().copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = row.computedHomeFormatted,
                    style = MaterialTheme.typography.bodySmall.tabular(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = row.computedOriginalFormatted,
                        style = MaterialTheme.typography.bodySmall.tabular(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = row.computedHomeFormatted,
                        style = MaterialTheme.typography.labelSmall.tabular(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                ) {
                    Text(
                        text = row.inputValue,
                        style = MaterialTheme.typography.titleSmall.tabular().copy(
                            fontWeight = FontWeight.SemiBold,
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    )
                }
            }
        }
    }
}

@Preview(
    name = "Screen 3A — Add Expense Equal + Cached FX (Light)",
    showSystemUi = true,
    device = Devices.PIXEL_7,
)
@Composable
private fun ExpenseEditorEqualLightPreview() {
    SplitLedgerTheme(darkTheme = false) {
        ExpenseEditorMockupScreen(state = SplitLedgerMockData.expenseEditorEqualCached)
    }
}

@Preview(
    name = "Screen 3B — Edit Expense Percent Warning (Light)",
    showSystemUi = true,
    device = Devices.PIXEL_7,
)
@Composable
private fun ExpenseEditorPercentWarningPreview() {
    SplitLedgerTheme(darkTheme = false) {
        ExpenseEditorMockupScreen(state = SplitLedgerMockData.expenseEditorPercentWarning)
    }
}

@Preview(
    name = "Screen 3C — Edit Expense Manual FX (Dark)",
    showSystemUi = true,
    device = Devices.PIXEL_7,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun ExpenseEditorDarkPreview() {
    SplitLedgerTheme(darkTheme = true) {
        ExpenseEditorMockupScreen(state = SplitLedgerMockData.expenseEditorPercentWarning)
    }
}
