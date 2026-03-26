package com.example.bankapp.ui.components.profileitems

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.bankapp.R
import com.example.bankapp.entities.dbtables.Account
import com.example.bankapp.entities.uimodels.AccountUiModel
import com.example.bankapp.ui.components.XSSpacer
import com.example.bankapp.ui.screens.ProfileDivider
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.ui.theme.DeviceSpec
import com.example.bankapp.utilities.RUPEE_SYMBOL
import com.example.bankapp.viewmodels.ProfileViewModel

@Composable
fun ProfileAccountCard(
    title: String,
    deviceSpec: DeviceSpec,
    viewModel: ProfileViewModel,
    account: AccountUiModel
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        shape = RoundedCornerShape(dimensionResource(R.dimen.profile_screen_card_rounded_corners))
    ) {
        Column(
            modifier = Modifier.padding(deviceSpec.profileCardPadding)
        ) {
            Text(
                text = title,
                style = deviceSpec.profileSectionTitleStyle(),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = AppSpacing.md)
            )

            ProfileAccountRow(
                label = stringResource(R.string.account_number_label),
                value = viewModel.getMaskedAccountNo(),
                showVisibilityToggle = true,
                isVisible = viewModel.isAccNoVisible,
                onVisibilityChange = viewModel::onAccNoVisibilityChange
            )

            ProfileDivider()

            ProfileAccountRow(
                label = stringResource(R.string.account_type_label),
                value = account.accountType.toString()
            )

            ProfileDivider()

            ProfileAccountRow(
                label = stringResource(R.string.balance_label),
                value = "$RUPEE_SYMBOL ${account.balance}"
            )
        }
    }
}

@Composable
private fun ProfileAccountRow(
    label: String,
    value: String,
    showVisibilityToggle: Boolean = false,
    isVisible: Boolean = false,
    onVisibilityChange: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AppSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            XSSpacer()
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (showVisibilityToggle) {
                    IconButton(
                        onClick = onVisibilityChange,
                        modifier = Modifier.size(dimensionResource(R.dimen.profile_screen_visibility_icon__button_size))
                    ) {
                        Icon(
                            imageVector = if (isVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(dimensionResource(R.dimen.profile_screen_visibility_icon_size))
                        )
                    }
                }
            }
        }
    }
}