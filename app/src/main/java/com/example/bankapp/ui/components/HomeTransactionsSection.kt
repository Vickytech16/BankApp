package com.example.bankapp.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.bankapp.R
import com.example.bankapp.entities.dtos.TransactionHistoryItemDto
import com.example.bankapp.ui.screens.TransactionListItem
import com.example.bankapp.ui.screens.uiAmountDisplay

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeTransactionSection(
    transactions : List<TransactionHistoryItemDto>,
    onSeeAllClickAction: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.transaction_history_label),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            TextButton(onClick = onSeeAllClickAction) {
                Text(
                    text = stringResource(R.string.see_all_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        SmallSpacer()

        transactions.take(3).forEach {
            transaction ->
            TransactionListItem(
                counterPartyName = transaction.counterparty_name ?: "",
                transactionDirection = transaction.direction,
                amount = transaction.amount.toString().uiAmountDisplay(),
                transactionDate = transaction.transaction_date
            )
        }
        }
    }
