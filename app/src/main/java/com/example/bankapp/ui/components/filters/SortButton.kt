package com.example.bankapp.ui.components.filters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.bankapp.R
import com.example.bankapp.entities.types.SortOptions
import com.example.bankapp.ui.components.LargeSpacer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortButton(
    selectedSort: SortOptions = SortOptions.NEWEST_FIRST,
    onSortChange: (SortOptions) -> Unit,
    labelText: String,
    showSheet: Boolean,
    onShowSheetChange: (Boolean) -> Unit,
    onReset: () -> Unit,
    onApply: () -> Unit
) {

        AssistChip(
            onClick = { onShowSheetChange(true) },
            label = {
                Text(labelText)
            },
            leadingIcon = {
                Icon(
                    Icons.Outlined.FilterList,
                    contentDescription = null,
                    modifier = Modifier.size(dimensionResource(R.dimen.filter_icon_size))
                )
            }
        )

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                onShowSheetChange(false)
            },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.filter_sheet_padding))
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding()
            ) {
                FilterSortRadioButton(
                    title = stringResource(R.string.sort_label),
                    options = SortOptions.entries.toList(),
                    selected = selectedSort,
                    onClick = { sortOption ->
                        onSortChange(sortOption)
                    },
                    labelFor = { sortOption ->
                        when (sortOption) {
                            SortOptions.NEWEST_FIRST -> stringResource(R.string.newest_first_label)
                            SortOptions.OLDEST_FIRST -> stringResource(R.string.oldest_first_label)
                            SortOptions.EXPENSIVE_FIRST -> stringResource(R.string.expensive_first_label)
                        }
                    }
                )
                LargeSpacer()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.filter_chip_spacing))
                ){
                    OutlinedButton(
                        onClick = {
                            onReset()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.reset_label))
                    }
                    Button(
                        onClick = {
                            onApply()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.apply_label))
                    }
                }
            }
            LargeSpacer()
        }
        }
    }
