package com.example.bankapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bankapp.R
import com.example.bankapp.di.viewmodelfactory.ProfileViewModelFactory
import com.example.bankapp.ui.components.*
import com.example.bankapp.ui.components.appbar.RegularAppBar
import com.example.bankapp.ui.components.bottomnavbar.BottomNavigationBar
import com.example.bankapp.ui.components.navigators.HOME_ROUTE
import com.example.bankapp.ui.components.navigators.PROFILE_ROUTE
import com.example.bankapp.ui.components.navigators.RECOVERY_KEY_HOME_ROUTE
import com.example.bankapp.ui.components.profileitems.ProfileAccountCard
import com.example.bankapp.ui.components.profileitems.ProfileSettingsCard
import com.example.bankapp.ui.components.profileitems.UserInfoCard
import com.example.bankapp.ui.components.profileitems.UserPfpAndNameEditable
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.ui.theme.DeviceSpec
import com.example.bankapp.ui.theme.LocalDeviceSpec
import com.example.bankapp.viewmodels.ProfileViewModel
import com.example.bankapp.viewmodels.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    profileViewModelFactory: ProfileViewModelFactory,
    logoutAction: () -> Unit,
    themeViewModel: ThemeViewModel
) {
    val viewModel: ProfileViewModel = viewModel(factory = profileViewModelFactory)
    val scrollState = rememberScrollState()
    val deviceSpec = LocalDeviceSpec.current
    val account by viewModel.account.collectAsState()
    val user by viewModel.editableUser.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = { RegularAppBar(stringResource(R.string.profile_screen_title), scrollBehavior = scrollBehavior) },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        bottomBar = {
            BottomNavigationBar(
                currentRoute = PROFILE_ROUTE,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(HOME_ROUTE) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                deviceSpec = deviceSpec
            )
        },

    ) {
        contentPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(contentPadding), contentAlignment = Alignment.TopCenter) {
            Column(
                modifier = Modifier.fillMaxWidth(deviceSpec.profileScreenWidthFaction).verticalScroll(scrollState).padding(top = AppSpacing.md, bottom = AppSpacing.xxl),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(dimensionResource(deviceSpec.profileAvatarSize))
                            .clip(CircleShape)
                            .clickable { viewModel.onShowEnlargedImageChange(true) }
                    ) {
                        UserAvatar(
                            name = user.userName,
                            pfpUrl = user.pfpURL,
                            size = dimensionResource(deviceSpec.profileAvatarSize),
                            editable = false,
                            imageUpdateKey = viewModel.imageUpdateTrigger
                        )
                    }
                    MediumSpacer()
                    Text(text = user.userName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(
                        text = stringResource(R.string.tap_to_edit_profile),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { viewModel.onShowEditSheetChange(true) }.padding(AppSpacing.sm)
                    )
                }
                XLSpacer()
                UserInfoCard(title = stringResource(R.string.contact_info_label), deviceSpec = deviceSpec, user = user)
                LargeSpacer()
                ProfileAccountCard(title = stringResource(R.string.account_details_label), deviceSpec = deviceSpec, viewModel = viewModel, account = account, countryCode = user.countryCode)
                LargeSpacer()
                ProfileSettingsCard(title = stringResource(R.string.settings_support_label), deviceSpec = deviceSpec, themeViewModel = themeViewModel, forgotPasswordClickAction = { navController.navigate(RECOVERY_KEY_HOME_ROUTE) })
                XLSpacer()
                RedLogoutButton(logoutAction = logoutAction, showLogoutAction = viewModel.showLogoutDialog, onShowLogoutActionChange = viewModel::onShowLogoutDialogChange)
                XLSpacer()
                XLSpacer()
            }
        }

        BankAppBottomSheet(showSheet = viewModel.showEditSheet, onDismissRequest = { viewModel.onShowEditSheetChange(false) }, fullHeight = false) {
            UserPfpAndNameEditable(viewModel = viewModel, deviceSpec = deviceSpec)
        }

        if (viewModel.showEnlargedImage) {
            Dialog(onDismissRequest = { viewModel.onShowEnlargedImageChange(false) }) {
                Box(modifier = Modifier.size(300.dp).background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp)).padding(AppSpacing.md)) {
                    UserAvatar(name = user.userName, pfpUrl = user.pfpURL, size = 300.dp, editable = false, imageUpdateKey = viewModel.imageUpdateTrigger)
                    IconButton(
                        onClick = { viewModel.onShowEnlargedImageChange(false) },
                        modifier = Modifier.align(Alignment.TopEnd).background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
                    }
                }
            }
        }
    }
}