package com.example.bankapp.ui.components.filters

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.bankapp.R
import com.example.bankapp.entities.uientities.uitypes.SortOptions
import com.example.bankapp.ui.components.LargeSpacer
import com.example.bankapp.ui.components.RadioButtonSelector

@Composable
fun SortSection(
    selectedSort: SortOptions = SortOptions.NEWEST_FIRST,
    onSortChange: (SortOptions) -> Unit,
    onReset: () -> Unit,
    onApply: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(dimensionResource(R.dimen.filter_sheet_padding))
    ) {
        RadioButtonSelector(
            title = stringResource(R.string.sort_label),
            options = SortOptions.entries.toList(),
            selected = selectedSort,
            onSelectionChange = { sortOption ->
                onSortChange(sortOption)
            },
            labelFor = { sortOption ->
                when (sortOption) {
                    SortOptions.NEWEST_FIRST -> stringResource(R.string.newest_first_label)
                    SortOptions.OLDEST_FIRST -> stringResource(R.string.oldest_first_label)
                }
            }
        )

        LargeSpacer()

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.filter_chip_spacing))
        ) {
            OutlinedButton(
                onClick = onReset,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.reset_label))
            }
            Button(
                onClick = onApply,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.apply_label))
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}