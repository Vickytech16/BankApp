package com.example.bankapp.ui.components.transactionitems

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bankapp.entities.dtos.TransactionHistoryItemDto
import com.example.bankapp.entities.types.TransactionType
import com.example.bankapp.ui.components.navigators.TransactionScreen
import com.example.bankapp.ui.screens.uiAmountDisplay

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionLazyList(
    transactions: List<TransactionHistoryItemDto>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    state: LazyListState = rememberLazyListState(),
    navController: NavController
  ) {

    LazyColumn(
        modifier = modifier,
        state = state,
        contentPadding = contentPadding
    ) {
        items(transactions) {
            transaction ->

            val counterPartyName =
                if(transaction.transactionType == TransactionType.DEPOSIT)
                    "${transaction.myUserName}(Deposit)"
                else
                    transaction.counterpartyName

            val counterPartyPfp =
                if(transaction.transactionType == TransactionType.DEPOSIT)
                    transaction.myPfpUrl
                else
                    transaction.counterpartyPfp

            TransactionListItem(
                counterPartyName = counterPartyName ?: "?",
                transactionDirection = transaction.direction,
                amount = transaction.amount.uiAmountDisplay(),
                transactionDate = transaction.transactionDate,
                counterPartyPfp,
                onClickAction = {
                    navController.navigate(
                        TransactionScreen.CashTransferDetailScreen.createRoute(transaction.transactionId)
                    )
                }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        }
    }
}