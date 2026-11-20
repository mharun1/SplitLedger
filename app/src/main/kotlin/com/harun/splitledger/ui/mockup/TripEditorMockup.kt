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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CurrencyExchange
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
fun TripEditorMockupScreen(
    state: MockTripEditorState,
    onCloseClick: () -> Unit = {},
    onTripNameChange: (String) -> Unit = {},
    onCurrencyClick: () -> Unit = {},
    onNewMemberDraftChange: (String) -> Unit = {},
    onAddMemberClick: () -> Unit = {},
    onRemoveMemberClick: (String) -> Unit = {},
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
                            contentDescription = "Close trip editor",
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
                        enabled = state.canSave,
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
                            style = MaterialTheme.typography.labelLarge,
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
            verticalArrangement = Arrangement.spacedBy(22.dp),
        ) {
            // Trip Identity & Home Currency
            item {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    OutlinedTextField(
                        value = state.tripName,
                        onValueChange = onTripNameChange,
                        label = { Text("Trip name") },
                        placeholder = { Text("e.g. Kyoto Autumn 2026") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth(),
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
                                    .clickable(onClick = onCurrencyClick)
                                    .heightIn(min = 64.dp)
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        modifier = Modifier.size(40.dp),
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxSize(),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.CurrencyExchange,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.size(20.dp),
                                            )
                                        }
                                    }
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(
                                            text = "Home currency",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.SemiBold,
                                            ),
                                            color = MaterialTheme.colorScheme.onSurface,
                                        )
                                        Text(
                                            text = "Balances and settlements settle in ${state.homeCurrencyCode}",
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
                                            text = state.homeCurrencyCode,
                                            style = MaterialTheme.typography.titleSmall.tabular().copy(
                                                fontWeight = FontWeight.SemiBold,
                                            ),
                                            color = MaterialTheme.colorScheme.primary,
                                        )
                                        Text(
                                            text = state.homeCurrencyLabel,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Rounded.ChevronRight,
                                        contentDescription = "Choose home currency",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(20.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Participants & Member Integrity Guard
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Participants",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        Text(
                            text = "${state.members.size} people",
                            style = MaterialTheme.typography.labelMedium.tabular(),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    // Inline Add Participant Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        OutlinedTextField(
                            value = state.newMemberDraft,
                            onValueChange = onNewMemberDraftChange,
                            placeholder = { Text("Add participant name") },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f),
                        )
                        FilledTonalButton(
                            onClick = onAddMemberClick,
                            enabled = state.newMemberDraft.isNotBlank(),
                            shape = RoundedCornerShape(14.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            modifier = Modifier.heightIn(min = 54.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Add",
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                    }

                    // Ledger Integrity Notice Banner (when guarded delete attempted)
                    if (state.integrityNotice != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = semantic.warningContainer,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Info,
                                    contentDescription = null,
                                    tint = semantic.onWarningContainer,
                                    modifier = Modifier.size(18.dp),
                                )
                                Text(
                                    text = state.integrityNotice,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Medium,
                                    ),
                                    color = semantic.onWarningContainer,
                                )
                            }
                        }
                    }

                    // Hairline-Separated Member Ledger Surface
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerLowest,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column {
                            state.members.forEachIndexed { index, row ->
                                TripEditorMemberListRow(
                                    row = row,
                                    onRemoveClick = { onRemoveMemberClick(row.member.id) },
                                )
                                if (index < state.members.lastIndex) {
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
private fun TripEditorMemberListRow(
    row: MockTripEditorMemberRow,
    onRemoveClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 60.dp)
            .padding(start = 16.dp, end = 6.dp, top = 8.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f),
        ) {
            AvatarInitials(
                initials = row.member.initials,
                colorIndex = row.member.colorIndex,
                size = 36.dp,
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = row.member.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = row.usageSummary,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        if (row.canDelete) {
            IconButton(onClick = onRemoveClick) {
                Icon(
                    imageVector = Icons.Rounded.DeleteOutline,
                    contentDescription = "Remove ${row.member.name}",
                    tint = SplitLedgerTheme.semanticColors.debt,
                )
            }
        } else {
            IconButton(onClick = onRemoveClick) {
                Icon(
                    imageVector = Icons.Rounded.Lock,
                    contentDescription = "${row.member.name} has linked expenses and cannot be deleted",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f),
                )
            }
        }
    }
}

@Preview(
    name = "Screen 4A — Edit Trip + Integrity Guard (Light)",
    showSystemUi = true,
    device = Devices.PIXEL_7,
)
@Composable
private fun TripEditorEditLightPreview() {
    SplitLedgerTheme(darkTheme = false) {
        TripEditorMockupScreen(state = SplitLedgerMockData.tripEditorEditTokyo)
    }
}

@Preview(
    name = "Screen 4B — Create New Trip (Light)",
    showSystemUi = true,
    device = Devices.PIXEL_7,
)
@Composable
private fun TripEditorCreateLightPreview() {
    SplitLedgerTheme(darkTheme = false) {
        TripEditorMockupScreen(state = SplitLedgerMockData.tripEditorCreateNew)
    }
}

@Preview(
    name = "Screen 4C — Edit Trip (Dark)",
    showSystemUi = true,
    device = Devices.PIXEL_7,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun TripEditorDarkPreview() {
    SplitLedgerTheme(darkTheme = true) {
        TripEditorMockupScreen(state = SplitLedgerMockData.tripEditorEditTokyo)
    }
}
