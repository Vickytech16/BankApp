package com.example.bankapp.ui.components.transactionitems

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import com.example.bankapp.R
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.bankapp.entities.dtos.TransactionHistoryItemDto
import com.example.bankapp.entities.types.TransactionStatus
import com.example.bankapp.ui.components.LargeSpacer
import com.example.bankapp.ui.components.MediumSpacer
import com.example.bankapp.ui.components.SmallSpacer
import com.example.bankapp.ui.components.UserAvatar
import com.example.bankapp.ui.components.XSSpacer
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.utilities.formatTransactionDateTime
import com.example.bankapp.utilities.uiAccNo

@Composable
fun CashTransferDetailBody(
    transactionItem: TransactionHistoryItemDto,
    paddingValues: PaddingValues,
    windowSizeClass: WindowSizeClass
) {


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingValues)
            .padding(AppSpacing.lg)
            ,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        UserAvatar(
            name = transactionItem.counterpartyName ?: stringResource(R.string.other_person_name_if_null),
            pfpUrl = transactionItem.counterpartyPfp,
            size = dimensionResource(R.dimen.transaction_detail_avatar_size)
        )

        MediumSpacer()

        Text(
            text = stringResource(R.string.transaction_to, transactionItem.counterpartyName ?: stringResource(R.string.other_person_name_if_null)),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        MediumSpacer()

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.rupee_symbol),
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = transactionItem.amount,
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        LargeSpacer()

        StatusSection(transactionItem.transactionStatus)

        LargeSpacer()

        HorizontalDivider(modifier = Modifier.fillMaxWidth())

        MediumSpacer()

        Text(
            text  = formatTransactionDateTime(transactionItem.transactionDate),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        LargeSpacer()

        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            TransactionDetailsCard(
                historyItem = transactionItem,
                windowSizeClass = windowSizeClass
            )
        }

        MediumSpacer()
    }
}



@Composable
private fun TransactionDetailsCard(
    historyItem: TransactionHistoryItemDto,
    windowSizeClass: WindowSizeClass
) {

    val cardWidthFraction = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 0.9f
        WindowWidthSizeClass.Medium -> 0.6f
        WindowWidthSizeClass.Expanded -> 0.5f
        else -> 0.9f
    }

    Card(
        modifier = Modifier.
                    fillMaxWidth(cardWidthFraction).
                    clip(RoundedCornerShape(dimensionResource(R.dimen.card_rounded_shape))),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimensionResource(R.dimen.card_elevation)
        ),
        shape = RoundedCornerShape(dimensionResource(R.dimen.card_rounded_shape))
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.md)
        ) {
            BankSection()

            LargeSpacer()

            TransactionField(
                label = stringResource(R.string.reference_id),
                value = historyItem.referenceNumber
            )

            LargeSpacer()

            TransactionPartySection(
                label = stringResource(R.string.from),
                name = historyItem.myUserName,
                accountNo = historyItem.myAccountNo.uiAccNo
            )

            LargeSpacer()

            TransactionPartySection(
                label = stringResource(R.string.to),
                name = historyItem.counterpartyName ?: stringResource(R.string.other_person_name_if_null),
                accountNo = historyItem.counterpartyAccountNo?.uiAccNo ?: "-"
            )

            LargeSpacer()

            BalanceAfterSection(balanceAfter = historyItem.balanceAfter)
        }
    }
}

