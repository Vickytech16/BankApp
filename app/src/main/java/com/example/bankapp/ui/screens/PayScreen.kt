package com.example.bankapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.More
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.bankapp.R
import com.example.bankapp.ui.components.appbar.Appbar
import com.example.bankapp.ui.components.bottomnavbar.BottomNavigationBar
import com.example.bankapp.ui.components.buttons.QuickActionsButton
import com.example.bankapp.ui.components.navigators.HOME_ROUTE
import com.example.bankapp.ui.components.navigators.PAY_ROUTE
import com.example.bankapp.ui.components.XLSpacer
import com.example.bankapp.ui.components.navigators.ADD_BENEFICIARY_ROUTE
import com.example.bankapp.ui.theme.AppPadding
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.ui.theme.DeviceSpec
import com.example.bankapp.ui.theme.DeviceSpecProvider
import com.example.bankapp.ui.theme.screenPadding
import com.example.bankapp.usecases.HomeSessionHandler


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayScreen(
    windowSizeClass: WindowSizeClass,
    navController: NavController,
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route ?: PAY_ROUTE

    val scrollState = rememberScrollState()
    val deviceSpec = DeviceSpecProvider.getCurrentDeviceSpec(windowSizeClass)

    Scaffold(
        topBar = {
            Appbar(
                title = stringResource(R.string.pay_screen_title),
                navBehaviour = { navController.navigate(HOME_ROUTE) },
                scrollBehavior = null
            )
        },
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
                .padding(contentPadding)
                .padding(top = 24.dp),
            contentAlignment = Alignment.TopCenter
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .verticalScroll(scrollState)
                    .padding(top = AppSpacing.md, bottom = AppSpacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PaySectionCard(
                    title = stringResource(R.string.pay_section_label),
                    deviceSpec = deviceSpec
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        PayScreenButton(
                            onClickAction = { navController.navigate(HOME_ROUTE) },
                            icon = Icons.Outlined.AccountBalance,
                            label = stringResource(R.string.pay_to_friend),
                            deviceSpec = deviceSpec,
                            modifier = Modifier.weight(1f)
                        )
                        PayScreenButton(
                            onClickAction = { navController.navigate(HOME_ROUTE) },
                            icon = Icons.Outlined.SwapHoriz,
                            label = stringResource(R.string.pay_anyone),
                            deviceSpec = deviceSpec,
                            modifier = Modifier.weight(1f)
                        )
                        PayScreenButton(
                            onClickAction = { navController.navigate(HOME_ROUTE) },
                            icon = Icons.Outlined.AccountBalanceWallet,
                            label = stringResource(R.string.deposit_button),
                            deviceSpec = deviceSpec,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(deviceSpec.payScreenSectionSpacing))

                PaySectionCard(
                    title = stringResource(R.string.beneficiary_section_label),
                    deviceSpec = deviceSpec
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        PayScreenButton(
                            onClickAction = { navController.navigate(ADD_BENEFICIARY_ROUTE) },
                            icon = Icons.Outlined.AccountBalance,
                            label = stringResource(R.string.add_beneficiary),
                            deviceSpec = deviceSpec,
                            modifier = Modifier.weight(1f)
                        )
                        PayScreenButton(
                            onClickAction = { navController.navigate(HOME_ROUTE) },
                            icon = Icons.AutoMirrored.Outlined.More,
                            label = stringResource(R.string.manage_beneficiary),
                            deviceSpec = deviceSpec,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }

                Spacer(modifier = Modifier.height(deviceSpec.payScreenSectionSpacing))

                PaySectionCard(
                    title = stringResource(R.string.schedule_pay_section_label),
                    deviceSpec = deviceSpec
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        PayScreenButton(
                            onClickAction = { navController.navigate(HOME_ROUTE) },
                            icon = Icons.Outlined.AccountBalance,
                            label = stringResource(R.string.schedule_pay),
                            deviceSpec = deviceSpec,
                            modifier = Modifier.weight(1f)
                        )
                        PayScreenButton(
                            onClickAction = { navController.navigate(HOME_ROUTE) },
                            icon = Icons.AutoMirrored.Outlined.More,
                            label = stringResource(R.string.manage_schedule),
                            deviceSpec = deviceSpec,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun PaySectionCard(
    title: String,
    deviceSpec: DeviceSpec,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(deviceSpec.payScreenCardPadding)
    ) {
        Text(
            text = title,
            style = deviceSpec.payScreenSectionTitleStyle(),
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = AppSpacing.md)
        )
        content()
    }
}

@Composable
private fun PayScreenButton(
    onClickAction: () -> Unit,
    icon: ImageVector,
    label: String,
    deviceSpec: DeviceSpec,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
    ) {
        FilledIconButton(
            onClick = onClickAction,
            modifier = Modifier.size(dimensionResource(deviceSpec.payScreenButtonSize)),
            shape = CircleShape,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary
            )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(dimensionResource(deviceSpec.payScreenButtonIconSize)),
            )
        }
        Text(
            text = label,
            style = deviceSpec.qabButtonLabelSize(),
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}