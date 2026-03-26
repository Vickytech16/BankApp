package com.example.bankapp.ui.screens

import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
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
import com.example.bankapp.ui.components.bottomnavbar.BottomNavigationBar
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.ui.theme.DeviceSpecProvider
import com.example.bankapp.viewmodels.ProfileViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import com.example.bankapp.di.viewmodelfactory.ProfileViewModelFactory
import com.example.bankapp.entities.dbtables.Account
import com.example.bankapp.ui.components.AlertButtonConfig
import com.example.bankapp.ui.components.AlertDialogBox
import com.example.bankapp.ui.components.ButtonStyle
import com.example.bankapp.ui.components.LargeSpacer
import com.example.bankapp.ui.components.MediumSpacer
import com.example.bankapp.ui.components.RadioButtonSelector
import com.example.bankapp.ui.components.XLSpacer
import com.example.bankapp.ui.components.XSSpacer
import com.example.bankapp.ui.components.appbar.Appbar
import com.example.bankapp.ui.components.navigators.HOME_ROUTE
import com.example.bankapp.ui.components.navigators.PROFILE_ROUTE
import com.example.bankapp.ui.components.profileitems.ProfileAccountCard
import com.example.bankapp.ui.components.profileitems.ProfileSettingsCard
import com.example.bankapp.ui.components.profileitems.UserInfoCard
import com.example.bankapp.ui.components.profileitems.UserInfoRow
import com.example.bankapp.ui.theme.DeviceSpec
import com.example.bankapp.viewmodels.ThemeType
import com.example.bankapp.viewmodels.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    windowSizeClass: WindowSizeClass,
    navController: NavController,
    profileViewModelFactory: ProfileViewModelFactory,
    logoutAction: () -> Unit,
    themeViewModel: ThemeViewModel
) {

    val viewModel: ProfileViewModel = viewModel(factory = profileViewModelFactory)
    val scrollState = rememberScrollState()
    val deviceSpec = DeviceSpecProvider.getCurrentDeviceSpec(windowSizeClass)
    val account by viewModel.account.collectAsState()
    val currentRoute = remember { PROFILE_ROUTE }
    val user by viewModel.user.collectAsState()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                viewModel.updateProfileImage(bitmap, context)
                inputStream?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Scaffold(
        topBar = {
            Appbar(stringResource(R.string.profile_screen_title), scrollBehavior = scrollBehavior)
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
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(deviceSpec.profileScreenWidthFaction)
                    .verticalScroll(scrollState)
                    .padding(top = AppSpacing.md, bottom = AppSpacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                UserAvatar(
                    name = user.userName,
                    pfpUrl =user.pfpURL,
                    size = dimensionResource(deviceSpec.profileAvatarSize),
                    editable = true,
                    editAction = {
                            imagePickerLauncher.launch("image/*")
                    },
                    imageUpdateKey = viewModel.imageUpdateTrigger
                )

                MediumSpacer()

                Text(
                    text = user.userName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                XLSpacer()

                UserInfoCard(
                    title = stringResource(R.string.contact_info_label),
                    deviceSpec = deviceSpec
                ) {
                    UserInfoRow(
                        icon = Icons.Outlined.Email,
                        label = stringResource(R.string.email_label),
                        value = user.email,
                    )

                    ProfileDivider()

                    UserInfoRow(
                        icon = Icons.Outlined.Phone,
                        label = stringResource(R.string.phone_label),
                        value = user.phoneNumber,
                    )
                }

                LargeSpacer()

                ProfileAccountCard(
                    title = stringResource(R.string.account_details_label),
                    deviceSpec = deviceSpec,
                    viewModel = viewModel,
                    account = account
                )

                LargeSpacer()

                ProfileSettingsCard(
                    title = stringResource(R.string.settings_support_label),
                    deviceSpec = deviceSpec,
                    themeViewModel = themeViewModel
                )

                XLSpacer()

                LogoutButton(logoutAction = logoutAction, showLogoutAction = viewModel.showLogoutDialog, onShowLogoutActionChange = viewModel::onShowLogoutDialogChange)

                XLSpacer()
            }
        }
    }
}

@Composable
fun LogoutButton(showLogoutAction: Boolean, onShowLogoutActionChange: (Boolean) -> Unit, logoutAction: () -> Unit){
    Button(
        onClick = {
            onShowLogoutActionChange(true)
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.profile__screen_logout_button_height)),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError
        ),
        shape = RoundedCornerShape(dimensionResource(R.dimen.profile_screen_card_rounded_corners))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Logout,
                contentDescription = null,
                modifier = Modifier.size(dimensionResource(R.dimen.profile_screen_logout_icon_size))
            )
            Text(
                text = stringResource(R.string.logout_button),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }

    if(showLogoutAction){
        AlertDialogBox(
            onDismissRequest = { onShowLogoutActionChange(false) },
            title = stringResource(R.string.logout_confirmation_title),
            confirmButton = AlertButtonConfig(
                label = stringResource(R.string.logout_button),
                onClick = logoutAction,
                style = ButtonStyle.ERROR
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
}


@Composable
fun ProfileDivider() {
    Spacer(modifier = Modifier.height(AppSpacing.sm))
}