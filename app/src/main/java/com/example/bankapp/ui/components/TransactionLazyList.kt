package com.example.bankapp.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.bankapp.entities.dtos.TransactionHistoryItemDto
import com.example.bankapp.ui.screens.TransactionListItem
import com.example.bankapp.ui.screens.uiAmountDisplay

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionLazyList(
    transactions: List<TransactionHistoryItemDto>,
    modifier: Modifier = Modifier,
  ) {

    LazyColumn(
        modifier = modifier

    ) {
        items(transactions) { transaction ->
            TransactionListItem(
                counterPartyName = transaction.counterparty_name ?: "?",
                transactionDirection = transaction.direction,
                amount = transaction.amount.toString().uiAmountDisplay(),
                transactionDate = transaction.transaction_date
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        }
    }
}