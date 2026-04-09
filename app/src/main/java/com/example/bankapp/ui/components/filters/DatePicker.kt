package com.example.bankapp.ui.components.filters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.DialogProperties
import com.example.bankapp.R
import com.example.bankapp.core.datecompatability.BankDateFactory
import com.example.bankapp.core.datecompatability.BankDateTime
import com.example.bankapp.ui.components.LargeSpacer
import com.example.bankapp.ui.theme.AppSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangeSection(
    startDate: Long?,
    endDate: Long?,
    onDatesSelected: (Long?, Long?) -> Unit
) {
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.date_range_label),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        LargeSpacer()

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
        ) {
            DateChip(
                label = stringResource(R.string.from),
                dateMillis = startDate,

                onClick = { showStartPicker = true }
            )

            DateChip(
                label = stringResource(R.string.to),
                dateMillis = endDate,

                onClick = { showEndPicker = true }
            )
        }
    }


    if (showStartPicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = startDate,
            initialDisplayMode = DisplayMode.Picker,
            selectableDates = PastOnlyConstraints
        )
        DatePickerDialog(
            onDismissRequest = { showStartPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    onDatesSelected(datePickerState.selectedDateMillis, endDate)
                    showStartPicker = false
                }) { Text(stringResource(R.string.done_label)) }
            },
            dismissButton = {
                TextButton(onClick = { showStartPicker = false }) {
                    Text(stringResource(R.string.cancel_label))
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false,
                title = {
                    Text(
                        text = stringResource(R.string.select_date),
                        modifier = Modifier.padding(AppSpacing.md),
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                headline = null
            )
        }
    }

    if (showEndPicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = endDate,
            initialDisplayMode = DisplayMode.Picker,
            selectableDates = PastOnlyConstraints
        )
        DatePickerDialog(
            onDismissRequest = { showEndPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    onDatesSelected(startDate, datePickerState.selectedDateMillis)
                    showEndPicker = false
                }) { Text(stringResource(R.string.done_label)) }
            },
            dismissButton = {
                TextButton(onClick = { showEndPicker = false }) {
                    Text(stringResource(R.string.cancel_label))
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false,
                title = {
                    Text(
                        text = stringResource(R.string.select_date),
                        modifier = Modifier.padding(AppSpacing.md),
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                headline = null
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateChip(
    label: String,
    dateMillis: Long?,
    onClick: () -> Unit,

) {
    val displayDate = dateMillis?.let {
        BankDateFactory.fromMillis(it, BankDateTime.UTC_ID).toMonthDayDisplay()
    } ?: stringResource(R.string.select_date)

    FilterChip(
        selected = dateMillis != null,
        onClick = onClick,
        label = { Text("$label: $displayDate") },

    )
}

@OptIn(ExperimentalMaterial3Api::class)
val PastOnlyConstraints = object : SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean =
        utcTimeMillis <= System.currentTimeMillis()
}


