package com.example.bankapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bankapp.R
import com.example.bankapp.entities.dtos.TransactionHistoryItemDto
import com.example.bankapp.ui.components.transactionitems.TransactionDetailBody
import com.example.bankapp.ui.theme.AppSpacing

@Composable
fun TransactionSharableReceipt(
    transactionItem: TransactionHistoryItemDto,
    countryCode: String,
    timezone: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(dimensionResource(R.dimen.transaction_detail_image_width))
            .background(MaterialTheme.colorScheme.surface)
            .padding(AppSpacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.app_name).uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 2.sp
        )

        MediumSpacer()
        HorizontalDivider(Modifier.alpha(0.5f) )
        LargeSpacer()

        TransactionDetailBody(
            transactionItem = transactionItem,
            paddingValues = PaddingValues(0.dp),
            countryCode = countryCode,
            showTimeZOne = true,
            timezone = timezone
        )

        LargeSpacer()
        Text(
            text = stringResource(R.string.official_receipt_footer),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
}