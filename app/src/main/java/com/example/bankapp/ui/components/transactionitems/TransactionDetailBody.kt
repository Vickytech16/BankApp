package com.example.bankapp.ui.components.transactionitems

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.bankapp.R
import com.example.bankapp.entities.dtos.TransactionHistoryItemDto
import com.example.bankapp.entities.types.transaction.LedgerDirection
import com.example.bankapp.entities.types.transaction.TransactionType
import com.example.bankapp.ui.components.LargeSpacer
import com.example.bankapp.ui.components.MediumSpacer
import com.example.bankapp.ui.components.UserAvatar
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.ui.theme.DeviceSpecProvider
import com.example.bankapp.ui.theme.amountGreenColor
import com.example.bankapp.utilities.formatTransactionDateTime

@Composable
fun TransactionDetailBody(
    transactionItem: TransactionHistoryItemDto,
    paddingValues: PaddingValues,
    windowSizeClass: WindowSizeClass
) {
    val transactionType = transactionItem.transactionType
    val deviceSpec = DeviceSpecProvider.getCurrentDeviceSpec(windowSizeClass)
    val isCredit = transactionItem.ledgerDirection == LedgerDirection.CREDIT
    val amountColor = if (isCredit) amountGreenColor else MaterialTheme.colorScheme.error
    val amountPrefix = if (isCredit) "+" else "-"

    val displayName = when (transactionType) {
        TransactionType.DEPOSIT -> transactionItem.myUserName
        else -> transactionItem.counterpartyName
            ?: stringResource(R.string.other_person_name_if_null)
    }

    val labelRes = when (transactionType) {
        TransactionType.DEPOSIT -> R.string.deposit_label
        else -> R.string.cash_transfer_label
    }

    val labelStyle = when (transactionType) {
        TransactionType.DEPOSIT -> MaterialTheme.typography.bodyMedium
        else -> MaterialTheme.typography.labelMedium
    }

    val pfpUrl = when (transactionType) {
        TransactionType.DEPOSIT -> transactionItem.myPfpUrl
        else -> transactionItem.counterpartyPfpUrl
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingValues)
            .padding(AppSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UserAvatar(
            name = displayName,
            pfpUrl = pfpUrl,
            size = dimensionResource(R.dimen.transaction_detail_avatar_size)
        )

        MediumSpacer()

        Text(
            text = stringResource(labelRes),
            style = labelStyle,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            fontWeight = if (transactionType == TransactionType.CASH_TRANSFER)
                FontWeight.Medium else FontWeight.Normal
        )

        MediumSpacer()

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = amountPrefix,
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = amountColor
            )
            Text(
                text = stringResource(R.string.rupee_symbol),
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = amountColor
            )
            Text(
                text = transactionItem.amount,
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = amountColor
            )
        }

        LargeSpacer()

        StatusSection(transactionItem.transactionStatus, deviceSpec = deviceSpec)

        LargeSpacer()

        HorizontalDivider(modifier = Modifier.fillMaxWidth())

        MediumSpacer()

        Text(
            text = formatTransactionDateTime(transactionItem.transactionDate),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        LargeSpacer()

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            TransactionDetailsCard(
                historyItem = transactionItem,
                windowSizeClass = windowSizeClass,
                isDeposit = transactionType == TransactionType.DEPOSIT
            )
        }

        MediumSpacer()
    }
}