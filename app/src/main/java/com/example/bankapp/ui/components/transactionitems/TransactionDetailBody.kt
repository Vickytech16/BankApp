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
import com.example.bankapp.ui.components.AutoResizeText
import com.example.bankapp.ui.components.LargeSpacer
import com.example.bankapp.ui.components.MediumSpacer
import com.example.bankapp.ui.components.UserAvatar
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.ui.theme.LocalDeviceSpec
import com.example.bankapp.ui.theme.amountGreenColor
import com.example.bankapp.utilities.CurrencyUtils
import java.math.BigDecimal

@Composable
fun TransactionDetailBody(
    transactionItem: TransactionHistoryItemDto,
    paddingValues: PaddingValues,
    countryCode: String,
    showTimeZOne: Boolean = false,
    timezone: String? = null
) {
    val transactionType = transactionItem.transactionType
    val deviceSpec = LocalDeviceSpec.current

    val isCredit = transactionItem.ledgerDirection == LedgerDirection.CREDIT
    val amountColor = if (isCredit) amountGreenColor else MaterialTheme.colorScheme.error
    val amountPrefix = if (isCredit) "+" else "-"

    val displayName = when {
        transactionItem.transactionType == TransactionType.DEPOSIT -> "You (Deposit)"
        !transactionItem.counterpartyNickname.isNullOrEmpty() ->
            "${transactionItem.counterpartyNickname} (${transactionItem.counterpartyName})"
        else -> transactionItem.counterpartyName ?: "Bank"
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
            AutoResizeText(
                text = "$amountPrefix${CurrencyUtils.formatCurrency(transactionItem.amount.toBigDecimalOrNull() ?: BigDecimal.ZERO, countryCode)} ${
                    CurrencyUtils.getCurrencySymbol(
                        countryCode
                    )
                }",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = amountColor
            )
        }

        LargeSpacer()

        StatusSection(transactionItem.transactionStatus, deviceSpec = deviceSpec)

        LargeSpacer()

        HorizontalDivider(modifier = Modifier.fillMaxWidth())

        MediumSpacer()

        Text(
            text = transactionItem.transactionDate.toFullDateTimeDisplay(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        if(showTimeZOne && timezone!=null){
            Text(
                text = "Timezone: $timezone",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }

        LargeSpacer()

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            TransactionDetailsCard(
                historyItem = transactionItem,
                isDeposit = transactionType == TransactionType.DEPOSIT,
                countryCode = countryCode
            )
        }
        MediumSpacer()
    }
}