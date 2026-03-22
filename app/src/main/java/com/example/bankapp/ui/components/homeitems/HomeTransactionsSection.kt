package com.example.bankapp.ui.components.homeitems

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.bankapp.R
import com.example.bankapp.entities.dtos.TransactionHistoryItemDto
import com.example.bankapp.entities.types.TransactionType
import com.example.bankapp.ui.components.SmallSpacer
import com.example.bankapp.ui.components.navigators.TransactionScreen
import com.example.bankapp.ui.components.transactionitems.TransactionListItem
import com.example.bankapp.ui.screens.uiAmountDisplay
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.ui.theme.DeviceSpec

@Composable
fun HomeTransactionSection(
    transactions: List<TransactionHistoryItemDto>,
    onSeeAllClickAction: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavController,
    deviceSpec: DeviceSpec
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = true, onClick = onSeeAllClickAction)
                .padding(AppSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.transaction_history_label),
                style = deviceSpec.homeTransactionHistoryLabel(),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = stringResource(R.string.see_all_label),
                style = deviceSpec.homeSeeAllLabel(),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }

        SmallSpacer()

        transactions.take(5).forEach { transaction ->
            val counterPartyName =
                if (transaction.transactionType == TransactionType.DEPOSIT)
                    transaction.myUserName
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
    }
}