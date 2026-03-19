package com.example.bankapp.ui.components.homeitems

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.bankapp.R
import com.example.bankapp.ui.components.MediumSpacer
import com.example.bankapp.ui.components.SmallSpacer
import com.example.bankapp.ui.components.XSSpacer
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.utilities.HIDDEN_BALANCE
import com.example.bankapp.utilities.RUPEE_SYMBOL

@Composable
fun HomeScreenCard(balance: String, accNo: String , isBalanceVisible: Boolean, onIsBalanceVisibleChange: ()-> Unit)
{
    Card(
        shape = RoundedCornerShape(size = dimensionResource(R.dimen.home_screen_card_rounded_corner)),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.home_screen_card_elevation)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                )
                .padding(AppSpacing.lg)
        ) {
            Column {

                Text(
                    text = stringResource(R.string.total_account_balance_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )

                XSSpacer()

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = RUPEE_SYMBOL,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    XSSpacer()

                    Text(
                        text = if (isBalanceVisible)
                                    balance
                                else
                                    HIDDEN_BALANCE,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    XSSpacer()

                    IconButton(onClick = {
                        onIsBalanceVisibleChange()
                    }) {
                        Icon(
                            imageVector = if (isBalanceVisible)
                                              Icons.Outlined.Visibility
                                          else
                                              Icons.Outlined.VisibilityOff,
                            contentDescription = if(isBalanceVisible)
                                                    stringResource(R.string.balance_visibility_on)
                                                 else
                                                     stringResource(R.string.balance_visibility_off),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                MediumSpacer()

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                SmallSpacer()

                Text(
                    text = accNo,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}