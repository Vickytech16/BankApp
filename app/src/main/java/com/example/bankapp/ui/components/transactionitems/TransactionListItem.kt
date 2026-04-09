package com.example.bankapp.ui.components.transactionitems

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import com.example.bankapp.R
import com.example.bankapp.core.datecompatability.BankDateTime
import com.example.bankapp.entities.types.transaction.LedgerDirection
import com.example.bankapp.ui.components.AutoResizeText
import com.example.bankapp.ui.components.UserAvatar
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.ui.theme.DeviceSpec
import com.example.bankapp.ui.theme.amountGreenColor
import com.example.bankapp.utilities.CurrencyUtils
import java.math.BigDecimal

@Composable
fun TransactionListItem(
    counterPartyName: String,
    transactionDirection: LedgerDirection?,
    amount: String,
    transactionDate: BankDateTime,
    isFailed: Boolean = false,
    pfpURL: String? = null,
    onClickAction: () -> Unit = {},
    deviceSpec: DeviceSpec,
    countryCode: String,
    avatarName: String = counterPartyName,
    forcedFontSize: TextUnit,
    onFontSizeChange: (TextUnit) -> Unit
) {
    val formattedDate = transactionDate.toMonthDayDisplay()
    val isCredit = transactionDirection == LedgerDirection.CREDIT
    val avatarSize = dimensionResource(deviceSpec.transactionListItemAvatarSize)

    val amountColor = when {
        isFailed -> MaterialTheme.colorScheme.outline
        isCredit -> amountGreenColor
        else -> MaterialTheme.colorScheme.error
    }

    val amountPrefix = if (isCredit) "+" else "-"
    val itemShape = RoundedCornerShape(dimensionResource(R.dimen.transaction_list_item_Rounded_border))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(itemShape)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = itemShape
            )
            .clickable(onClick = onClickAction)
            .padding(deviceSpec.transactionListItemHorizontalPadding)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(deviceSpec.transactionListItemSpacing)
        ) {
            UserAvatar(avatarName, pfpURL, size = avatarSize)

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
            ) {
                Text(
                    text = counterPartyName,
                    style = deviceSpec.transactionListItemCounterPartyNameStyle().copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = forcedFontSize
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    softWrap = false
                )

                Text(
                    text = formattedDate,
                    style = deviceSpec.transactionListItemDateStyle(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                AutoResizeText(
                    text = "$amountPrefix${CurrencyUtils.formatCurrency(amount.toBigDecimalOrNull() ?: BigDecimal.ZERO, countryCode)} ${CurrencyUtils.getCurrencySymbol(countryCode)}",
                    style = deviceSpec.transactionListItemMoneyStyle().copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = amountColor,
                    maxLines = 1,
                    softWrap = false
                )

                if (isFailed) {
                    Text(
                        text = "✕ Failed", // Replace with stringResource(R.string.failed)
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}