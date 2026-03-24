package com.example.bankapp.ui.components.filters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.bankapp.R
import com.example.bankapp.entities.types.ui.SortOptions
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
        modifier = Modifier.fillMaxWidth()
    ) {
        RadioButtonSelector(
            title = stringResource(R.string.sort_label),
            options = SortOptions.entries.toList(),
            selected = selectedSort,
            onSelectionChange = {
                sortOption ->
                onSortChange(sortOption)
            },
            labelFor = {
                sortOption ->
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
    }
}