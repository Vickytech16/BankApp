package com.example.bankapp.ui.components.transactionitems

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource

import androidx.compose.ui.res.stringResource
import com.example.bankapp.R


@Composable
fun TransactionSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(stringResource(R.string.search_transactions_hint))
                      },
        singleLine = true,
        leadingIcon = {
            IconButton(
                onClick = onSearch
            ) {
                Icon(Icons.Outlined.Search, contentDescription = null)
            }
        },
        trailingIcon = {
            IconButton(
                onClick = onCancel
            ) {
                Icon(Icons.Outlined.Close, contentDescription = null)
            }
        },
        shape = RoundedCornerShape(dimensionResource(R.dimen.search_bar_round_shape)),
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(R.dimen.filter_sheet_padding),
                vertical = dimensionResource(R.dimen.search_bar_padding)
            )
    )

}