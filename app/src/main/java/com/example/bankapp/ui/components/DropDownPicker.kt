package com.example.bankapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.bankapp.R
import com.example.bankapp.ui.theme.AppSpacing
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DropDownPickerField(
    label: String,
    selectedValue: String,
    placeholder: String,
    items: List<T>,
    onItemSelected: (T) -> Unit,
    itemLabel: (T) -> String,
    itemSecondaryLabel: (T) -> String = { "" },
    itemLeadingIcon: @Composable ((T) -> Unit)? = null,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    showSheet: Boolean,
    onShowSheetChange: (Boolean) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    errorBehaviour: @Composable () -> Unit = {}
) {


    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val keyboardController = LocalSoftwareKeyboardController.current
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium
        )

        Surface(
            onClick = { onShowSheetChange(true) },
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.text_field_height)),
            shape = RoundedCornerShape(dimensionResource(R.dimen.text_field_corner_radius)),
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = BorderStroke(
                width = 1.dp,
                color = if (isError) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.outlineVariant
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = AppSpacing.md),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = selectedValue.ifEmpty { placeholder },
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (selectedValue.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (isError) {
            errorBehaviour()
        }


        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { onShowSheetChange(false) },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface,
                dragHandle = { BottomSheetDefaults.DragHandle() }
            ) {
                Column(modifier = Modifier.fillMaxHeight(0.8f)) {
                    SearchBarComponent(
                        query = searchQuery,
                        onQueryChange = { onSearchQueryChange(it) },
                        onSearch = {
                            keyboardController?.hide()
                            scope.launch {
                                lazyListState.animateScrollToItem(0)
                            }
                        },
                        onCancel = {
                            onSearchQueryChange("")
                            keyboardController?.hide()
                        },
                        placeholderText = "Search $label..."
                    )

                    val filteredItems = items.filter {
                        itemLabel(it).contains(searchQuery, ignoreCase = true) ||
                                itemSecondaryLabel(it).contains(searchQuery, ignoreCase = true)
                    }

                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(filteredItems) { item ->
                            ListItem(
                                headlineContent = { Text(itemLabel(item)) },
                                supportingContent = {
                                    if (itemSecondaryLabel(item).isNotEmpty()) {
                                        Text(itemSecondaryLabel(item))
                                    }
                                },
                                leadingContent = itemLeadingIcon?.let { { it(item) } },
                                modifier = Modifier.clickable {
                                    onItemSelected(item)
                                    onShowSheetChange(false)
                                    onSearchQueryChange("")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}