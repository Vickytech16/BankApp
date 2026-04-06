package com.example.bankapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.bankapp.R
import com.example.bankapp.ui.components.textfields.PickerOutlinedBox
import com.example.bankapp.ui.components.textfields.UnifiedOutlinedTextField
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
    modifier: Modifier = Modifier,
    itemSecondaryLabel: (T) -> String = { "" },
    itemLeadingIcon: @Composable ((T) -> Unit)? = null,
    isError: Boolean = false,
    isValid: Boolean = false,
    showSheet: Boolean,
    onShowSheetChange: (Boolean) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    errorBehaviour: @Composable () -> Unit = {},
    skipPartiallyExpanded: Boolean = true,
    leadingIcon: @Composable (() -> Unit),
    searchPlaceholder: String,
    tickCondition: Boolean = true
) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Column(modifier = modifier.fillMaxWidth()) {
        PickerOutlinedBox(
            label = label,
            selectedValue = selectedValue,
            placeholder = placeholder,
            onClick = { onShowSheetChange(true) },
            leadingEmoji = leadingIcon,
            isError = isError,
            isValid = isValid,
            supportingText = { if (isError) errorBehaviour() },
            tickCondition = tickCondition
        )

        if (showSheet) {
            BankAppBottomSheet(
                showSheet = showSheet,
                onDismissRequest = { onShowSheetChange(false) },
                skipPartiallyExpanded = true
            ) {
                SearchBarComponent(
                    query = searchQuery,
                    onQueryChange = { onSearchQueryChange(it) },
                    onSearch = {
                        keyboardController?.hide()
                        scope.launch { lazyListState.animateScrollToItem(0) }
                    },
                    onCancel = {
                        onSearchQueryChange("")
                        keyboardController?.hide()
                    },
                    placeholderText = stringResource(R.string.search_for, searchPlaceholder)
                )

                val filteredItems = items.filter {
                    itemLabel(it).contains(searchQuery, ignoreCase = true) ||
                            itemSecondaryLabel(it).contains(searchQuery, ignoreCase = true)
                }

                if (filteredItems.isEmpty()) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(AppSpacing.xl),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No ${label.lowercase()} found",
                            style = MaterialTheme.typography.bodyLarge,
                       //     color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Try a different search term",
                            style = MaterialTheme.typography.bodySmall,
                  //          color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(filteredItems) {
                            item ->
                            ListItem(
                                headlineContent = {
                                    Text(itemLabel(item), style = MaterialTheme.typography.bodyLarge)
                                },
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
                                },
                                colors = ListItemDefaults.colors(
                                    containerColor = BottomSheetDefaults.ContainerColor
                                )
                            )
                        }

                        item {
                            androidx.compose.foundation.layout.Spacer(
                                modifier = Modifier.height(32.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}