package com.example.bankapp.ui.components.homeitems

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
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.bankapp.R
import com.example.bankapp.entities.dbtables.Account
import com.example.bankapp.entities.types.AccountType
import com.example.bankapp.ui.components.SmallSpacer
import com.example.bankapp.ui.components.XSSpacer
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.ui.theme.DeviceSpec
import com.example.bankapp.utilities.RUPEE_SYMBOL
import com.example.bankapp.utilities.uiAccNo

@Composable
fun HomeScreenCard(
    account: Account,
    isBalanceVisible: Boolean,
    onIsBalanceVisibleChange: () -> Unit,
    deviceSpec: DeviceSpec
) {
    val maskedAccNo = "**** ${account.accNo.uiAccNo.takeLast(4)}"

    Card(
        shape = RoundedCornerShape(size = dimensionResource(deviceSpec.homeScreenCardRoundedCorner)),
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimensionResource(deviceSpec.homeScreenCardElevation)
        ),
        modifier = Modifier
            .fillMaxWidth(deviceSpec.homeScreenCardWidth)
            .padding(vertical = AppSpacing.md)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(AppSpacing.lg)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.total_account_balance_label),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Medium
                    )

                    Image(
                        painter = painterResource(R.drawable.bank_logo),
                        contentDescription = stringResource(R.string.app_name),
                        modifier = Modifier.size(dimensionResource(deviceSpec.homeScreenCardLogoSize))
                    )
                }

                XSSpacer()

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = RUPEE_SYMBOL,
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                    XSSpacer()

                    Text(
                        text = if (isBalanceVisible)
                            account.balance.toString()
                        else
                            "••••••",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                    XSSpacer()

                    IconButton(onClick = onIsBalanceVisibleChange) {
                        Icon(
                            imageVector =
                                if (isBalanceVisible)
                                    Icons.Outlined.Visibility
                                else
                                    Icons.Outlined.VisibilityOff,
                            contentDescription =
                                if (isBalanceVisible)
                                    stringResource(R.string.balance_visibility_on)
                                else
                                    stringResource(R.string.balance_visibility_off),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = deviceSpec.homeScreenCardAccountSectionSpacing)
                        .background(
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(dimensionResource(deviceSpec.homeScreenCardAccountInfoRoundedCorner))
                        )
                        .padding(deviceSpec.homeScreenCardAccountInfoPadding)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = maskedAccNo,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text =
                                when (account.accountType) {
                                    AccountType.SAVINGS -> stringResource(R.string.savings_account)
                                    AccountType.CURRENT -> stringResource(R.string.current_account)
                                },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}