package com.example.bankapp.ui.components.transactionitems

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.bankapp.R
import com.example.bankapp.entities.dtos.TransactionHistoryItemDto
import com.example.bankapp.entities.types.transaction.TransactionType
import com.example.bankapp.ui.components.LargeSpacer
import com.example.bankapp.ui.components.MediumSpacer
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.ui.theme.DeviceSpecProvider
import com.example.bankapp.utilities.uiAccNo
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import com.example.bankapp.ui.components.SmallSpacer
import com.example.bankapp.utilities.getCurrencySymbol
import java.util.Locale

@Composable
fun TransactionDetailsCard(
    historyItem: TransactionHistoryItemDto,
    windowSizeClass: WindowSizeClass,
    isDeposit: Boolean = false,
    countryCode: String
) {

    val deviceSpec = DeviceSpecProvider.getCurrentDeviceSpec(windowSizeClass)
    val dividerGradient = 0.3f

    Card(
        modifier = Modifier
            .fillMaxWidth(deviceSpec.transactionDetailCardWidth)
            .clip(RoundedCornerShape(dimensionResource(R.dimen.card_rounded_shape))),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimensionResource(R.dimen.card_elevation)
        ),
        shape = RoundedCornerShape(dimensionResource(R.dimen.card_rounded_shape))
    ) {
        Column(
            modifier = Modifier.padding(deviceSpec.transactionDetailCardPadding)
        ) {
            BankSection()

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = deviceSpec.transactionDetailDividerPaddingVertical),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = dividerGradient)
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                DetailRow(
                    label = stringResource(R.string.reference_id),
                    value = historyItem.referenceNumber
                )

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = deviceSpec.transactionDetailDividerPaddingVertical),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = dividerGradient)
                )

                DetailRowWithSubtext(
                    label = if (isDeposit) stringResource(R.string.deposit_label) else stringResource(R.string.from),
                    name = historyItem.myUserName,
                    accountNo = historyItem.myAccountNo.uiAccNo
                )

                if (!isDeposit) {
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = deviceSpec.transactionDetailDividerPaddingVertical),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = dividerGradient)
                    )

                    DetailRowWithSubtext(
                        label = stringResource(R.string.to),
                        name = historyItem.counterpartyName ?: stringResource(R.string.other_person_name_if_null),
                        accountNo = historyItem.counterpartyAccountNo?.uiAccNo ?: "-"
                    )
                }
            }

            LargeSpacer()

            BalanceAfterSectionCard(balanceAfter = historyItem.balanceAfter, countryCode)
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AppSpacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun DetailRowWithSubtext(
    label: String,
    name: String,
    accountNo: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AppSpacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            SmallSpacer()
            val maskedAccNo = "**** ${accountNo.takeLast(4)}"
            Text(
                text = "Acc. $maskedAccNo",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun BalanceAfterSectionCard(balanceAfter: String, countryCode: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(dimensionResource(R.dimen.balance_after_card_rounded_shape))),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiary
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.md),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.balance_after_transaction),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.8f)
            )
            MediumSpacer()
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = getCurrencySymbol(countryCode = countryCode),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondary
                )
                Text(
                    text = balanceAfter,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    }
}