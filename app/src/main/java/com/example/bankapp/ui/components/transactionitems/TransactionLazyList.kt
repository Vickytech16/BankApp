package com.example.bankapp.ui.components.transactionitems

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bankapp.R
import com.example.bankapp.core.datecompatability.BankDateFactory
import com.example.bankapp.entities.dtos.TransactionHistoryItemDto
import com.example.bankapp.entities.types.transaction.TransactionType
import com.example.bankapp.ui.components.LargeSpacer
import com.example.bankapp.ui.components.SmallSpacer
import com.example.bankapp.ui.components.navigators.INDIVIDUAL_TRANSACTION_LOG_ROUTE
import com.example.bankapp.ui.components.navigators.TRANSACTIONS_LOG_ROUTE
import com.example.bankapp.ui.screens.uiAmountDisplay
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.ui.theme.DeviceSpec
import com.example.bankapp.utilities.isSameDay


@Composable
fun TransactionLazyList(
    transactions: List<TransactionHistoryItemDto>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    state: LazyListState = rememberLazyListState(),
    navController: NavController,
    deviceSpec: DeviceSpec,
    countryCode: String
) {
    val todayLabel = stringResource(R.string.today)
    val yesterdayLabel = stringResource(R.string.yesterday)
    val groupedTransactions = remember(transactions) {
        transactions.groupBy { transaction ->
            val transactionDate = transaction.transactionDate
            val today = BankDateFactory.now()
            val yesterday = today.minusDays(1)

            when {
                transactionDate.isSameDay(today) -> todayLabel
                transactionDate.isSameDay(yesterday) -> yesterdayLabel
                else -> transactionDate.toMonthDayDisplay()
            }
        }
    }

    LazyColumn(
        modifier = modifier,
        state = state,
        contentPadding = contentPadding
    ) {
        groupedTransactions.forEach { (dateLabel, transactionsForDate) ->
            item {
                Text(
                    text = dateLabel,
                    style = deviceSpec.transactionDateHeaderStyle(),
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(
                        horizontal = deviceSpec.transactionDateHeaderPadding,
                        vertical = AppSpacing.sm
                    )
                )
            }

            items(transactionsForDate) { transaction ->
                val displayName = when {
                    transaction.transactionType == TransactionType.DEPOSIT -> "You (Deposit)"
                    !transaction.counterpartyNickname.isNullOrEmpty() ->
                        "${transaction.counterpartyNickname} (${transaction.counterpartyName})"
                    else -> transaction.counterpartyName ?: "Bank"
                }
                val counterPartyPfp =
                    if (transaction.transactionType == TransactionType.DEPOSIT)
                        transaction.myPfpUrl
                    else
                        transaction.counterpartyPfpUrl

                TransactionListItem(
                    counterPartyName = displayName,
                    transactionDirection = transaction.ledgerDirection,
                    amount = transaction.amount.uiAmountDisplay(),
                    transactionDate = transaction.transactionDate,
                    pfpURL = counterPartyPfp,
                    onClickAction = {
                        navController.navigate(
                            "$INDIVIDUAL_TRANSACTION_LOG_ROUTE/${transaction.transactionId}?origin=$TRANSACTIONS_LOG_ROUTE"
                        )
                    },
                    deviceSpec = deviceSpec,
                    countryCode = countryCode
                )

                SmallSpacer()
            }
            item {
                LargeSpacer()
            }
        }
    }
}