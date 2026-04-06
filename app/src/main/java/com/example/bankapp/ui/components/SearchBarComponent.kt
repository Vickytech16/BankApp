package com.example.bankapp.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.bankapp.R
import com.example.bankapp.ui.theme.AppSpacing

@Composable
fun SearchBarComponent(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    placeholderText: String = stringResource(R.string.search_transactions_hint),
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(placeholderText) },
        singleLine = true,
        leadingIcon = {
            leadingContent?.invoke() ?: IconButton(onClick = onSearch) {
                Icon(Icons.Outlined.Search, contentDescription = null)
            }
        },
        trailingIcon = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (query.isNotEmpty()) {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Outlined.Close, contentDescription = null)
                    }
                }
                trailingContent?.invoke()
            }
        },
        shape = RoundedCornerShape(dimensionResource(R.dimen.search_bar_round_shape)),
        modifier = modifier
            .padding(horizontal = AppSpacing.sm)
            .fillMaxWidth()
    )
}