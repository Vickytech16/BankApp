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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import com.example.bankapp.R
import com.example.bankapp.core.datecompatability.BankDateTime
import com.example.bankapp.entities.types.transaction.LedgerDirection
import com.example.bankapp.ui.components.UserAvatar
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.ui.theme.DeviceSpec
import com.example.bankapp.ui.theme.amountGreenColor
import com.example.bankapp.utilities.RUPEE_SYMBOL

@Composable
fun TransactionListItem(
    counterPartyName: String,
    transactionDirection: LedgerDirection?,
    amount: String,
    transactionDate: BankDateTime,
    pfpURL: String? = null,
    onClickAction: () -> Unit = {},
    deviceSpec: DeviceSpec
) {
    val formattedDate = transactionDate.toMonthDayDisplay()
    val isCredit = transactionDirection == LedgerDirection.CREDIT
    val avatarSize = dimensionResource(deviceSpec.transactionListItemAvatarSize)

    val amountColor = if (isCredit) amountGreenColor else MaterialTheme.colorScheme.error
    val amountPrefix = if (isCredit) "+" else "-"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = true, onClick = onClickAction)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(dimensionResource(R.dimen.transaction_list_item_Rounded_border))
            )
            .padding(deviceSpec.transactionListItemHorizontalPadding)

    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(deviceSpec.transactionListItemSpacing)
        ) {
            if (pfpURL == null)
                UserAvatar(counterPartyName, size = avatarSize)
            else
                UserAvatar(
                    counterPartyName,
                    pfpURL,
                    size = avatarSize
                )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
            ) {
                Text(
                    text = counterPartyName,
                    style = deviceSpec.transactionListItemCounterPartyNameStyle(),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = formattedDate,
                    style = deviceSpec.transactionListItemDateStyle(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "$amountPrefix$RUPEE_SYMBOL$amount",
                style = deviceSpec.transactionListItemMoneyStyle(),
                fontWeight = FontWeight.Bold,
                color = amountColor
            )
        }
    }
}