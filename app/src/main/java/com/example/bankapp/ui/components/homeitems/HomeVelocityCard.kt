package com.example.bankapp.ui.components.homeitems

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.bankapp.R
import com.example.bankapp.entities.AccountVelocityStatus
import com.example.bankapp.ui.components.AutoResizeText
import com.example.bankapp.ui.components.XSSpacer
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.ui.theme.DeviceSpec
import com.example.bankapp.utilities.CurrencyUtils

@Composable
fun HomeVelocityCard(
    velocityStatus: AccountVelocityStatus,
    isVelocityVisible: Boolean,
    onToggleVisibility: () -> Unit,
    deviceSpec: DeviceSpec,
    countryCode: String
) {
    val currencySymbol = CurrencyUtils.getCurrencySymbol(countryCode)
    val cardBackgroundGradient = 0.12f

    val spendProgress = (velocityStatus.moneySpentToday.toDouble() /
            velocityStatus.dailySpendLimit.toDouble()).coerceIn(0.0, 1.0).toFloat()

    val formattedSpent = remember(velocityStatus.moneySpentToday, isVelocityVisible) {
        if (isVelocityVisible)
            CurrencyUtils.formatCurrency(velocityStatus.moneySpentToday, countryCode)
        else "••••••"
    }

    val timeRemaining = remember(velocityStatus.nextResetMillis) {
        val diff = velocityStatus.nextResetMillis - System.currentTimeMillis()
        val hours = (diff / (1000 * 60 * 60)).coerceAtLeast(0)
        val mins = ((diff / (1000 * 60)) % 60).coerceAtLeast(0)
        if (hours > 0) "$hours hr $mins min" else "$mins min"
    }

    val formattedLimit = remember(velocityStatus.dailySpendLimit) {
        CurrencyUtils.formatCurrency(velocityStatus.dailySpendLimit, countryCode)
    }

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
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(AppSpacing.lg)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // ── Top row: label (left) + logo (right) ──────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Today's Spending",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                    Image(
                        painter = painterResource(R.drawable.bank_logo),
                        contentDescription = null,
                        modifier = Modifier.size(dimensionResource(deviceSpec.homeScreenCardLogoSize))
                    )
                }

                XSSpacer()

                // ── Amount row: large amount (left) + eye toggle (right) ───────
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AutoResizeText(
                        text = "$currencySymbol $formattedSpent",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    XSSpacer()

                    IconButton(onClick = onToggleVisibility) {
                        Icon(
                            imageVector = if (isVelocityVisible)
                                Icons.Outlined.Visibility
                            else
                                Icons.Outlined.VisibilityOff,
                            contentDescription = if (isVelocityVisible)
                                "Hide velocity"
                            else
                                "Show velocity",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // ── Progress bar + limit/reset row (fixed height, always reserved) ──
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = deviceSpec.homeScreenCardAccountSectionSpacing)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Progress bar — invisible when hidden, but space always reserved
                        LinearProgressIndicator(
                            progress = { if (isVelocityVisible) spendProgress else 0f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            color = when {
                                !isVelocityVisible -> Color.Transparent
                                spendProgress > 0.9f -> MaterialTheme.colorScheme.error
                                spendProgress > 0.7f -> Color(0xFFFFA500)
                                else -> MaterialTheme.colorScheme.primary
                            },
                            trackColor = if (isVelocityVisible)
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                            else
                                Color.Transparent
                        )

                        XSSpacer()

                        // Limit + reset line — always takes up space, hidden text when not visible
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isVelocityVisible)
                                    "Limit: $currencySymbol $formattedLimit"
                                else
                                    "Limit: $currencySymbol ••••••",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Text(
                                text = if (isVelocityVisible)
                                    "Resets in $timeRemaining"
                                else
                                    "Resets in ••••••",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }
                }

                // ── Bottom tab: daily transaction count ───────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = cardBackgroundGradient),
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
                            text = "Daily Count Usage",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (isVelocityVisible)
                                "${velocityStatus.transactionsToday}/${velocityStatus.maxTransactions} Txns"
                            else
                                "••••/${velocityStatus.maxTransactions} Txns",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}