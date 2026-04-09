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
    transaction: TransactionHistoryItemDto,
    paddingValues: PaddingValues,
    countryCode: String,
    showTimeZOne: Boolean = false,
    timezone: String? = null,
    onEnlargeImage: Boolean,
    onEnlargeImageChange: (Boolean) -> Unit

) {
    val transactionType = transaction.transactionType
    val deviceSpec = LocalDeviceSpec.current

    val isCredit = transaction.ledgerDirection == LedgerDirection.CREDIT
    val amountColor = if (isCredit) amountGreenColor else MaterialTheme.colorScheme.error
    val amountPrefix = if (isCredit) "+" else "-"

    val labelRes = when (transactionType) {
        TransactionType.DEPOSIT -> R.string.deposit_label
        TransactionType.CASH_TRANSFER -> R.string.domestic_transfer
        TransactionType.INTERNATIONAL_TRANSFER -> R.string.international_transfer
        TransactionType.INTEREST_ADDITION -> R.string.interest_addition_label
        else -> R.string.cash_transfer_label
    }

    val displayPfp = when (transactionType) {
        TransactionType.DEPOSIT -> transaction.myPfpUrl
        TransactionType.INTEREST_ADDITION -> "res://bank_logo"
        else -> transaction.counterpartyPfpUrl
    }

    val avatarName = when (transactionType) {
        TransactionType.DEPOSIT -> transaction.myUserName
        TransactionType.INTEREST_ADDITION -> stringResource(R.string.vangi)
        else -> transaction.counterpartyName ?: stringResource(R.string.other_person_name_if_null)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingValues)
            .padding(AppSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UserAvatar(
            name = avatarName,
            pfpUrl = displayPfp,
            size = dimensionResource(R.dimen.transaction_detail_avatar_size),
            showEnlargeOnClick = onEnlargeImage,
            onEnlargeToggle = onEnlargeImageChange
        )

        MediumSpacer()

        Text(
            text = stringResource(labelRes),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Normal
        )

        MediumSpacer()

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            AutoResizeText(
                text = "$amountPrefix${CurrencyUtils.formatCurrency(transaction.amount.toBigDecimalOrNull() ?: BigDecimal.ZERO, countryCode)} ${
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

        StatusSection(transaction.transactionStatus, deviceSpec = deviceSpec)

        LargeSpacer()

        HorizontalDivider(modifier = Modifier.fillMaxWidth())

        MediumSpacer()

        Text(
            text = transaction.transactionDate.toFullDateTimeDisplay(),
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
                historyItem = transaction,
                isDeposit = transactionType == TransactionType.DEPOSIT,
                countryCode = countryCode
            )
        }
        MediumSpacer()
    }
}