package com.example.bankapp.ui.components.transactionitems

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bankapp.R
import com.example.bankapp.entities.dtos.TransactionHistoryItemDto
import com.example.bankapp.entities.types.TransactionType
import com.example.bankapp.ui.components.LargeSpacer
import com.example.bankapp.ui.components.MediumSpacer
import com.example.bankapp.ui.components.SmallSpacer
import com.example.bankapp.ui.components.navigators.TransactionScreen
import com.example.bankapp.ui.screens.uiAmountDisplay
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.ui.theme.DeviceSpec
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale



@Composable
fun TransactionLazyList(
    transactions: List<TransactionHistoryItemDto>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    state: LazyListState = rememberLazyListState(),
    navController: NavController,
    deviceSpec: DeviceSpec
) {
    val groupedTransactions = transactions.groupBy { transaction ->
        val transactionDate = LocalDate.parse(
            transaction.transactionDate.substringBefore("T"),
            DateTimeFormatter.ISO_LOCAL_DATE
        )
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)

        when {
            transactionDate == today -> stringResource(R.string.today)
            transactionDate == yesterday -> stringResource(R.string.yesterday)
            else -> transactionDate.format(DateTimeFormatter.ofPattern("dd MMMM", Locale.getDefault()))
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
                val counterPartyName =
                    if (transaction.transactionType == TransactionType.DEPOSIT)
                        "${transaction.myUserName}(Deposit)"
                    else
                        transaction.counterpartyName

                val counterPartyPfp =
                    if (transaction.transactionType == TransactionType.DEPOSIT)
                        transaction.myPfpUrl
                    else
                        transaction.counterpartyPfp

                TransactionListItem(
                    counterPartyName = counterPartyName ?: "?",
                    transactionDirection = transaction.direction,
                    amount = transaction.amount.uiAmountDisplay(),
                    transactionDate = transaction.transactionDate,
                    pfpURL = counterPartyPfp,
                    onClickAction = {
                        navController.navigate(
                            TransactionScreen.CashTransferDetailScreen.createRoute(transaction.transactionId)
                        )
                    },
                    deviceSpec = deviceSpec
                )

                SmallSpacer()
            }

            item {
                LargeSpacer()
            }
        }
    }
}