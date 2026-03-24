package com.example.bankapp.ui.components.homeitems

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.bankapp.R
import com.example.bankapp.entities.dtos.TransactionHistoryItemDto
import com.example.bankapp.entities.types.transaction.TransactionType
import com.example.bankapp.ui.components.SmallSpacer
import com.example.bankapp.ui.components.navigators.INDIVIDUAL_TRANSACTION_LOG_ROUTE
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensionResource( R.dimen.home_screen_see_all_spacing))
            ) {
                Text(
                    text = stringResource(R.string.see_all_label),
                    style = deviceSpec.homeSeeAllLabel(),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(dimensionResource(R.dimen.home_screen_see_all_icon_size)),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        SmallSpacer()

        transactions.take(5).forEach { transaction ->
            val displayName =
                if (transaction.transactionType == TransactionType.DEPOSIT)
                    transaction.myUserName
                else
                    transaction.counterpartyName

            val displayPfp =
                if (transaction.transactionType == TransactionType.DEPOSIT)
                    transaction.myPfpUrl
                else
                    transaction.counterpartyPfpUrl

            TransactionListItem(
                counterPartyName = displayName ?: "?",
                transactionDirection = transaction.ledgerDirection,
                amount = transaction.amount.uiAmountDisplay(),
                transactionDate = transaction.transactionDate,
                pfpURL = displayPfp,
                onClickAction = {
                    navController.navigate(
                        "$INDIVIDUAL_TRANSACTION_LOG_ROUTE/${transaction.transactionId}"
                    )
                },
                deviceSpec = deviceSpec
            )
            SmallSpacer()
        }
    }
}