package com.example.bankapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Help
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bankapp.R
import com.example.bankapp.ui.components.UserAvatar
import com.example.bankapp.ui.components.appbar.NameOnlyAppBar
import com.example.bankapp.ui.components.bottomnavbar.BottomNavigationBar
import com.example.bankapp.ui.components.buttons.SubmitButton
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.ui.theme.DeviceSpecProvider
import com.example.bankapp.utilities.uiAccNo
import com.example.bankapp.viewmodels.ProfileViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.example.bankapp.di.viewmodelfactory.ProfileViewModelFactory
import com.example.bankapp.ui.components.navigators.HOME_ROUTE
import com.example.bankapp.ui.components.navigators.PAY_ROUTE
import com.example.bankapp.ui.components.navigators.PROFILE_ROUTE
import com.example.bankapp.ui.theme.DeviceSpec

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    windowSizeClass: WindowSizeClass,
    navController: NavController,
    profileViewModelFactory: ProfileViewModelFactory
) {

    val viewModel: ProfileViewModel = viewModel(factory = profileViewModelFactory)
    val scrollState = rememberScrollState()
    val deviceSpec = DeviceSpecProvider.getCurrentDeviceSpec(windowSizeClass)
    val account by viewModel.account.collectAsState()
    val currentRoute = remember { PROFILE_ROUTE }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            NameOnlyAppBar(stringResource(R.string.profile_screen_title), scrollBehavior = scrollBehavior)
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        bottomBar = {
            BottomNavigationBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(HOME_ROUTE) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                deviceSpec = deviceSpec
            )
        },
        contentWindowInsets = WindowInsets.systemBars
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .verticalScroll(scrollState)
                    .padding(top = AppSpacing.md, bottom = AppSpacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                UserAvatar(
                    name = viewModel.user.userName,
                    pfpUrl = viewModel.user.pfpURL,
                    size = dimensionResource(deviceSpec.profileAvatarSize)
                )

                Spacer(modifier = Modifier.height(AppSpacing.md))

                Text(
                    text = viewModel.user.userName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(AppSpacing.xl))

                ProfileInfoCard(
                    title = stringResource(R.string.contact_info_label),
                    deviceSpec = deviceSpec
                ) {
                    ProfileInfoRow(
                        icon = Icons.Outlined.Mail,
                        label = stringResource(R.string.email_label),
                        value = viewModel.user.email,
                        deviceSpec = deviceSpec
                    )

                    ProfileDivider()

                    ProfileInfoRow(
                        icon = Icons.Outlined.Phone,
                        label = stringResource(R.string.phone_label),
                        value = viewModel.user.phoneNumber,
                        deviceSpec = deviceSpec
                    )
                }

                Spacer(modifier = Modifier.height(deviceSpec.profileSectionSpacing))

                ProfileAccountCard(
                    title = stringResource(R.string.account_details_label),
                    deviceSpec = deviceSpec,
                    viewModel = viewModel,
                    account = account
                )

                Spacer(modifier = Modifier.height(deviceSpec.profileSectionSpacing))

                ProfileSettingsCard(
                    title = stringResource(R.string.settings_support_label),
                    deviceSpec = deviceSpec
                )

                Spacer(modifier = Modifier.height(AppSpacing.xl))

                Button(
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.Logout,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = stringResource(R.string.logout_button),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(AppSpacing.xl))
            }
        }
    }
}

@Composable
private fun ProfileInfoCard(
    title: String,
    deviceSpec: DeviceSpec,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        shape = RoundedCornerShape(20.dp)
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
            content()
        }
    }
}

@Composable
private fun ProfileInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    deviceSpec: DeviceSpec
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AppSpacing.sm),
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
                        shape = RoundedCornerShape(12.dp)
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
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ProfileAccountCard(
    title: String,
    deviceSpec: DeviceSpec,
    viewModel: ProfileViewModel,
    account: com.example.bankapp.entities.dbtables.Account
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        shape = RoundedCornerShape(20.dp)
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
                value = "$${account.balance}"
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
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
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
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileSettingsCard(
    title: String,
    deviceSpec: DeviceSpec
) {
    val showThemeDialog = remember { mutableStateOf(false) }
    val selectedTheme = remember { mutableStateOf("Dark") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        shape = RoundedCornerShape(20.dp)
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
                icon = Icons.Outlined.Lock,
                label = stringResource(R.string.change_password_label),
                deviceSpec = deviceSpec
            )

            ProfileDivider()

            ProfileActionRow(
                icon = Icons.AutoMirrored.Outlined.Help,
                label = stringResource(R.string.forgot_password_label),
                deviceSpec = deviceSpec
            )

            ProfileDivider()

            ProfileActionRow(
                icon = Icons.Outlined.Palette,
                label = stringResource(R.string.appearance_theme_label),
                value = selectedTheme.value,
                deviceSpec = deviceSpec,
                isClickable = true,
                onValueClick = { showThemeDialog.value = true }
            )
        }
    }

    if (showThemeDialog.value) {
        ThemeSelectionDialog(
            currentTheme = selectedTheme.value,
            onThemeSelected = { theme ->
                selectedTheme.value = theme
                showThemeDialog.value = false
            },
            onDismiss = { showThemeDialog.value = false }
        )
    }
}

@Composable
private fun ThemeSelectionDialog(
    currentTheme: String,
    onThemeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val themes = listOf("System Default", "Light", "Dark")

    androidx.compose.material3.AlertDialog(
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
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
            ) {
                themes.forEach { theme ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onThemeSelected(theme) }
                            .padding(vertical = AppSpacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
                    ) {
                        androidx.compose.material3.RadioButton(
                            selected = currentTheme == theme,
                            onClick = { onThemeSelected(theme) }
                        )
                        Text(
                            text = theme,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        },
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close_button))
            }
        }
    )
}
@Composable
private fun ProfileActionRow(
    icon: ImageVector,
    label: String,
    deviceSpec: DeviceSpec,
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
                        shape = RoundedCornerShape(12.dp)
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

@Composable
private fun ProfileDivider() {
    Spacer(modifier = Modifier.height(AppSpacing.sm))
}