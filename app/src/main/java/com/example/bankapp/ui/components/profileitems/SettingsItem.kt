package com.example.bankapp.ui.components.profileitems

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.bankapp.R
import com.example.bankapp.ui.components.RadioButtonSelector

import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.ui.theme.DeviceSpec
import com.example.bankapp.entities.types.ThemeType
import com.example.bankapp.ui.components.SmallSpacer
import com.example.bankapp.viewmodels.ThemeViewModel


@Composable
fun ProfileSettingsCard(
    title: String,
    deviceSpec: DeviceSpec,
    themeViewModel: ThemeViewModel,
    forgotPasswordClickAction: ()->Unit
) {
    val currentTheme by themeViewModel.currentTheme.collectAsState()

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

            ProfileActionRow(
                icon = Icons.AutoMirrored.Outlined.Help,
                label = stringResource(R.string.forgot_password_label),
                isClickable = true,
                onValueClick = { forgotPasswordClickAction() }
            )

            SmallSpacer()

            ProfileActionRow(
                icon = Icons.Outlined.Palette,
                label = stringResource(R.string.appearance_theme_label),
                value = currentTheme.getDisplayName(),
                isClickable = true,
                onValueClick = { themeViewModel.onThemeDialogChange(true) }
            )
        }
    }

    if (themeViewModel.showThemeDialog) {
        ThemeSelectionDialog(
            currentTheme = currentTheme,
            onThemeSelected = themeViewModel::onThemeSelected,
            onDismiss = { themeViewModel.onThemeDialogChange(false)}
        )
    }
}

@Composable
private fun ThemeSelectionDialog(
    currentTheme: ThemeType,
    onThemeSelected: (ThemeType) -> Unit,
    onDismiss: () -> Unit
) {
    val themes = listOf(ThemeType.SYSTEM_DEFAULT, ThemeType.LIGHT, ThemeType.DARK)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.appearance_theme_label),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                RadioButtonSelector(
                    title = stringResource(R.string.select_theme),
                    options = themes,
                    selected = currentTheme,
                    onSelectionChange = { theme ->
                        onThemeSelected(theme)
                        onDismiss()
                    },
                    labelFor = { theme -> theme.getDisplayName() }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close_button))
            }
        }
    )
}

@Composable
private fun ProfileActionRow(
    icon: ImageVector,
    label: String,

    value: String? = null,
    isClickable: Boolean = false,
    onValueClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AppSpacing.sm)
            .then(
                if (isClickable && onValueClick != null) {
                    Modifier.clickable(onClick = onValueClick)
                } else {
                    Modifier
                }
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
        ) {
            Box(
                modifier = Modifier
                    .size(dimensionResource(R.dimen.profile_icon_wrap_size))
                    .background(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(AppSpacing.md)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    modifier = Modifier.size(dimensionResource(R.dimen.profile_icon_size)),
                    tint = MaterialTheme.colorScheme.onSecondary
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        if (value != null) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
