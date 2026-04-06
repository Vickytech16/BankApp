package com.example.bankapp.temp

/*
object AccountNumberFieldStrategy : FieldTypeStrategy {

    override fun getKeyboardOptions(): KeyboardOptions =
        KeyboardOptions(keyboardType = KeyboardType.NumberPassword)

    override fun getLetterSpacing(): Int = 2

    override fun validateValue(value: String): String {
        val digitsOnly = value.replace(" ", "")
        return if (digitsOnly.length <= ACCOUNT_NUMBER_SIZE) {
            formatAccountNumber(digitsOnly)
        } else {
            formatAccountNumber(digitsOnly.take(ACCOUNT_NUMBER_SIZE))
        }
    }

   fun formatAndPositionCursor(
        currentText: String,
        cursorPosition: Int
    ): Pair<String, Int> {
        val digitsOnly = currentText.replace(" ", "")

        if (digitsOnly.length <= ACCOUNT_NUMBER_SIZE) {
            val formatted = formatAccountNumber(digitsOnly)

            val digitsBeforeCursor = formatted
                .take(cursorPosition)
                .replace(" ", "")
                .length

            var digitCount = 0
            for (i in formatted.indices) {
                if (formatted[i].isDigit()) {
                    digitCount++
                    if (digitCount == digitsBeforeCursor + 1) {
                        return Pair(formatted, i + 1)
                    }
                }
            }

            return Pair(formatted, minOf(cursorPosition + 1, formatted.length))
        }

        val formatted = formatAccountNumber(digitsOnly.take(ACCOUNT_NUMBER_SIZE))
        return Pair(formatted, minOf(cursorPosition, formatted.length))
    }

    private fun formatAccountNumber(accountNumber: String): String {
        return accountNumber
            .take(ACCOUNT_NUMBER_SIZE)
            .chunked(4)
            .joinToString(" ")
    }

    override fun getMaxLength(): Int = ACCOUNT_NUMBER_SIZE + 2

    override fun getLeadingIcon(): ImageVector = Icons.Outlined.AccountBox
}
 */

/*

package com.example.bankapp.ui.components.homeitems

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.bankapp.R
import com.example.bankapp.entities.AccountVelocityStatus
import com.example.bankapp.ui.components.AutoResizeText
import com.example.bankapp.ui.components.SmallSpacer
import com.example.bankapp.ui.components.XSSpacer
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.ui.theme.DeviceSpec
import com.example.bankapp.utilities.CurrencyUtils
import java.math.BigDecimal

@Composable
fun HomeVelocityCard(
    velocityStatus: AccountVelocityStatus,
    isVelocityVisible: Boolean,
    onToggleVisibility: () -> Unit,
    deviceSpec: DeviceSpec,
    countryCode: String
) {
    val currencySymbol = CurrencyUtils.getCurrencySymbol(countryCode)

    // Calculate progress for the bar
    val spendProgress = (velocityStatus.moneySpentToday.toDouble() /
            velocityStatus.dailySpendLimit.toDouble()).coerceIn(0.0, 1.0).toFloat()

    val formattedSpent = remember(velocityStatus.moneySpentToday, isVelocityVisible) {
        if (isVelocityVisible)
            CurrencyUtils.formatCurrency(velocityStatus.moneySpentToday, countryCode)
        else "••••••"
    }

    Card(
        shape = RoundedCornerShape(size = dimensionResource(deviceSpec.homeScreenCardRoundedCorner)),
        colors = CardDefaults.cardColors(
            // Use SurfaceVariant to distinguish from the Primary Home Card
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(dimensionResource(deviceSpec.homeScreenCardElevation)),
        modifier = Modifier
            .fillMaxWidth(deviceSpec.homeScreenCardWidth)
            .padding(vertical = AppSpacing.md)
            .animateContentSize() // Smoothly expand/collapse when bar appears
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.lg)
        ) {
            // Header
            Text(
                text = stringResource(R.string.daily_velocity_label),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold
            )

            XSSpacer()

            // Main Amount
            AutoResizeText(
                text = "$currencySymbol $formattedSpent",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Progress Bar (Visible only when details are shown or always for UX)
            if (isVelocityVisible) {
                SmallSpacer()
                VelocityProgressBar(progress = spendProgress)
                XSSpacer()

                val spent = CurrencyUtils.formatCurrency(velocityStatus.moneySpentToday, countryCode)
                val limit = CurrencyUtils.formatCurrency(velocityStatus.dailySpendLimit, countryCode)

                Text(
                    text = stringResource(R.string.spent_today_out_of, "$currencySymbol $spent", "$currencySymbol $limit"),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            SmallSpacer()

            // Clickable Bottom Section
            Surface(
                onClick = onToggleVisibility,
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(dimensionResource(deviceSpec.homeScreenCardAccountInfoRoundedCorner)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(deviceSpec.homeScreenCardAccountInfoPadding),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isVelocityVisible) "Hide Details" else "Tap to View Details",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.Bold
                    )

                    // Transaction count
                    Text(
                        text = "${velocityStatus.transactionsToday}/${velocityStatus.maxTransactions} Txns",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun VelocityProgressBar(progress: Float) {
    // Dynamic color based on how close they are to the limit
    val barColor = when {
        progress > 0.9f -> MaterialTheme.colorScheme.error
        progress > 0.7f -> Color(0xFFFFA500) // Orange warning
        else -> MaterialTheme.colorScheme.primary
    }

    LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp) // Slightly thicker for visibility
            .clip(RoundedCornerShape(4.dp)),
        color = barColor,
        trackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
    )
}

@Composable
private fun VelocityInfoItem(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
    }
}

//@Composable
//private fun RuleRow(label: String, value: String) {
//    Row(
//        modifier = Modifier.fillMaxWidth().padding(vertical = AppSpacing.xs),
//        horizontalArrangement = Arrangement.SpaceBetween
//    ) {
//        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
//        Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
//    }
//}

it should share the SAME DIMENSIONS AS HOME CARD, WHETHER EXPANDED OR SHRUNK, the spent x today (limit 5k) it is not visible, make it visible, maybe use teritary colors for this card . Also the big text should be showing the spent money today, ensure that.


 */