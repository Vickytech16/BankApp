package com.example.bankapp.ui.components

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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.bankapp.R
import com.example.bankapp.ui.theme.AppSpacing

@Composable
fun BalanceStatusCard(
    modifier: Modifier = Modifier,
    balanceValue: String,
    exchangeInfo: ExchangeDisplayData? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AppSpacing.xl),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = AppSpacing.sm)
    ) {
        Column(
            modifier = Modifier
                .padding(AppSpacing.lg)
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.available_balance),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
            )
            Text(
                text = balanceValue,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onPrimary
            )

            exchangeInfo?.let { data ->
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = AppSpacing.lg),
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Recipient Receives",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                        )
                        Text(
                            text = data.convertedAmount,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(AppSpacing.sm)
                    ) {
                        Column(modifier = Modifier.padding(horizontal = AppSpacing.sm ,vertical = AppSpacing.xs)) {
                            Text(
                                text = stringResource(R.string.rates_updated),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text(
                                text = data.lastUpdated,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

data class ExchangeDisplayData(
    val convertedAmount: String,
    val lastUpdated: String
)