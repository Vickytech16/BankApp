package com.example.bankapp.ui.components.transactionitems

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import com.example.bankapp.R
import com.example.bankapp.ui.components.UserAvatar
import com.example.bankapp.ui.screens.toMonthAndDayOnlyDate
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.utilities.RUPEE_SYMBOL

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionListItem(
    counterPartyName: String,
    transactionDirection: String,
    amount: String,
    transactionDate: String,
    pfpURL: String? = null,
    onClickAction: () -> Unit = {}
){
    val formattedDate = transactionDate.toMonthAndDayOnlyDate()
    val isCredit = transactionDirection.equals("CREDIT", true)

    val amountColor =
        if(isCredit)
            Color.Green
        else
            MaterialTheme.colorScheme.error
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm)
            .clickable(
                enabled = true,
                onClick = onClickAction
            )
        ,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)

    ) {
        if(pfpURL==null)
            UserAvatar(counterPartyName, size = dimensionResource(R.dimen.user_avatar_transaction))
        else
            UserAvatar(
                counterPartyName,
                pfpURL,
                size = dimensionResource(R.dimen.user_avatar_transaction)
            )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
        ) {
            Text(
                text = counterPartyName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = "$RUPEE_SYMBOL$amount",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = amountColor
        )
    }

}