package com.example.bankapp.ui.components.filters

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.example.bankapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterButton(
    labelText: String,
    activeCount: Int,
    modifier: Modifier = Modifier,
    sheetContent: @Composable () -> Unit,
    showSheet: Boolean,
    onShowSheetChange: (Boolean) -> Unit
) {
    BadgedBox(
        badge = {
            if(activeCount > 0){
                Badge {
                    Text(activeCount.toString())
                }
            }
        },
        modifier = modifier
    ) {
        AssistChip(
            onClick = {
                onShowSheetChange(true)
            },
            label = {
                Text(labelText,
                    style = MaterialTheme.typography.labelLarge)
            },
            leadingIcon = {
                Icon(
                    Icons.Outlined.FilterList,
                    contentDescription = null,
                    modifier = Modifier.size(dimensionResource(R.dimen.filter_icon_size))
                )
            },
            modifier = Modifier.size(
                width = dimensionResource(R.dimen.filter_button_width),
                height = dimensionResource(R.dimen.filter_button_height)
            )
        )
    }

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
                    .verticalScroll(rememberScrollState())
                    .padding(dimensionResource(R.dimen.filter_sheet_padding))
                    .navigationBarsPadding()
            ) {
                sheetContent()
            }
        }
    }
}