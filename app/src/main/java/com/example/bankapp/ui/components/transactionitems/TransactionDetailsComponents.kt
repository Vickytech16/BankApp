package com.example.bankapp.ui.components.transactionitems

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.bankapp.R
import com.example.bankapp.entities.types.TransactionStatus
import com.example.bankapp.ui.components.MediumSpacer
import com.example.bankapp.ui.components.XSSpacer
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.ui.theme.DeviceSpec

@Composable
fun StatusSection(transactionStatus: TransactionStatus?, deviceSpec: DeviceSpec) {

    val statusIcon: ImageVector
    val statusText: String
    val statusColor: Color
    val statusBackground: Color

    when (transactionStatus) {
            TransactionStatus.COMPLETED -> {
                statusIcon = Icons.Filled.Check
                statusText = stringResource(R.string.status_completed_successfully)
                statusColor = MaterialTheme.colorScheme.tertiary
                statusBackground = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
            }
            TransactionStatus.FAILED -> {
                statusIcon = Icons.Filled.Close
                statusText = stringResource(R.string.status_failed_insufficient_balance)
                statusColor = MaterialTheme.colorScheme.error
                statusBackground = MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
            }
            else -> {
                statusIcon = Icons.Filled.Schedule
                statusText = stringResource(R.string.status_pending)
                statusColor = MaterialTheme.colorScheme.tertiary
                statusBackground = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
            }
        }

    Box(
        modifier = Modifier
            .fillMaxWidth(deviceSpec.transactionDetailStatusItemWidth)
            .background(
                color = statusBackground,
                shape = RoundedCornerShape(dimensionResource(R.dimen.status_section_rounded_corner))
            )
            .padding(AppSpacing.md),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = statusIcon,
                contentDescription = statusText,
                tint = statusColor,
                modifier = Modifier.size(dimensionResource(R.dimen.status_icon_size))
            )

            XSSpacer()

            Text(
                text = statusText,
                style = MaterialTheme.typography.labelMedium,
                color = statusColor,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
 fun BankSection() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = R.drawable.bank_logo),
            contentDescription = stringResource(R.string.vangi),
            modifier = Modifier
                .size(dimensionResource(R.dimen.bank_logo_size_transaction_screen))
                .padding(end = AppSpacing.md)
        )
        Text(
            text = stringResource(R.string.vangi),
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
 fun TransactionField(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        XSSpacer()

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
 fun TransactionPartySection(
    label: String,
    name: String,
    accountNo: String
) {

    val accNoFontSize = 11.sp
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        XSSpacer()

        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )

        // XSSpacer()

        Text(
            text = accountNo,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            fontSize = accNoFontSize
        )

    }
}

@Composable
fun BalanceAfterSection(balanceAfter: String) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.balance_after_transaction),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        MediumSpacer()

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.rupee_symbol),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = balanceAfter,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

    }
}

