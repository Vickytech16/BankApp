package com.example.bankapp.ui.components.homeitems

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.bankapp.R
import com.example.bankapp.entities.uientities.uidata.AlertButtonConfig
import com.example.bankapp.ui.components.AlertDialogBox
import com.example.bankapp.entities.uientities.uitypes.AlertButtonStyle
import com.example.bankapp.ui.components.MediumSpacer
import com.example.bankapp.ui.components.UserAvatar
import com.example.bankapp.ui.components.XLSpacer
import com.example.bankapp.ui.theme.AppSpacing


@Composable
fun HomeDrawer(drawerState: DrawerState,
               logoutAction: () -> Unit,
               username: String,
               modifier: Modifier = Modifier,
               userPfpUrl: String? = null,
               content: @Composable (() -> Unit),
               showLogoutDialog: Boolean,
               onShowLogOutDialogChange: (Boolean) -> Unit
) {
    if (showLogoutDialog) {
        AlertDialogBox(
            onDismissRequest = { onShowLogOutDialogChange(false) },
            title = stringResource(R.string.logout_confirmation_title),
            confirmButton = AlertButtonConfig(
                label = stringResource(R.string.logout_button),
                onClick = logoutAction,
                style = AlertButtonStyle.ERROR
            ),
            dismissButton = AlertButtonConfig(
                label = stringResource(R.string.cancel_label),
                onClick = { }
            ),
            content = {
                Text(stringResource(R.string.logout_confirmation_message))
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                modifier = modifier
            ) {
                Spacer(Modifier.height(AppSpacing.xxl))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppSpacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    UserAvatar(username, userPfpUrl, )

                    Spacer(Modifier.height(AppSpacing.md))
                    Text(
                        text = username,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = stringResource(R.string.welcome_back),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                XLSpacer()

                HorizontalDivider()

                MediumSpacer()

                NavigationDrawerItem(
                    icon = {
                        Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = null)
                    },
                    label = {
                        Text(stringResource(R.string.logout_button))
                    },
                    selected = false,
                    onClick = { onShowLogOutDialogChange(true) },
                    modifier = Modifier.padding(horizontal = AppSpacing.md)
                )
            }
        },
        content = content,
    )
}