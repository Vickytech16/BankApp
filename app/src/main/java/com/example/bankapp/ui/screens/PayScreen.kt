package com.example.bankapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.More
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.bankapp.R
import com.example.bankapp.ui.components.appbar.Appbar
import com.example.bankapp.ui.components.bottomnavbar.BottomNavigationBar
import com.example.bankapp.ui.components.buttons.ActionButton
import com.example.bankapp.ui.components.buttons.ButtonDimensions
import com.example.bankapp.ui.components.navigators.HOME_ROUTE
import com.example.bankapp.ui.components.navigators.PAY_ROUTE
import com.example.bankapp.ui.components.navigators.ADD_BENEFICIARY_ROUTE
import com.example.bankapp.ui.components.navigators.CASH_TRANSFER_ROUTE
import com.example.bankapp.ui.components.navigators.DEPOSIT_ROUTE
import com.example.bankapp.ui.components.navigators.PAY_TO_BENEFICIARY_ROUTE
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.ui.theme.DeviceSpec
import com.example.bankapp.ui.theme.DeviceSpecProvider


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
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val actionButtonDimensions = ButtonDimensions(
        buttonSize = deviceSpec.qabButtonSize,
        iconSize = deviceSpec.qabButtonIconSize,
        spacing = deviceSpec.qabButtonSpacing,
        labelStyle =deviceSpec.qabButtonLabelSize(),
    )

    Scaffold(
        topBar = {
            Appbar(stringResource(R.string.pay_screen_title), scrollBehavior = scrollBehavior)
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
                PaySectionCard(
                    title = stringResource(R.string.pay_section_label),
                    deviceSpec = deviceSpec
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ActionButton(
                            onClickAction = { navController.navigate(PAY_TO_BENEFICIARY_ROUTE) },
                            icon = Icons.Outlined.AccountBalance,
                            label = stringResource(R.string.pay_to_friend),
                            modifier = Modifier.weight(1f),
                            dimensions = actionButtonDimensions
                        )
                        ActionButton(
                            onClickAction = { navController.navigate(CASH_TRANSFER_ROUTE) },
                            icon = Icons.Outlined.SwapHoriz,
                            label = stringResource(R.string.pay_anyone),
                            modifier = Modifier.weight(1f),
                            dimensions = actionButtonDimensions,
                        )
                        ActionButton(
                            onClickAction = { navController.navigate(DEPOSIT_ROUTE) },
                            icon = Icons.Outlined.AccountBalanceWallet,
                            label = stringResource(R.string.deposit_button),
                            modifier = Modifier.weight(1f),
                            dimensions = actionButtonDimensions,
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
                        ActionButton(
                            onClickAction = { navController.navigate(ADD_BENEFICIARY_ROUTE) },
                            icon = Icons.Outlined.AccountBalance,
                            label = stringResource(R.string.add_beneficiary),
                            modifier = Modifier.weight(1f),
                            dimensions = actionButtonDimensions,

                        )
                        ActionButton(
                            onClickAction = { navController.navigate(HOME_ROUTE) },
                            icon = Icons.AutoMirrored.Outlined.More,
                            label = stringResource(R.string.manage_beneficiary),
                            modifier = Modifier.weight(1f),
                            dimensions = actionButtonDimensions,
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
                        ActionButton(
                            onClickAction = { navController.navigate(HOME_ROUTE) },
                            icon = Icons.Outlined.AccountBalance,
                            label = stringResource(R.string.schedule_pay),
                            modifier = Modifier.weight(1f),
                            dimensions = actionButtonDimensions,
                        )
                        ActionButton(
                            onClickAction = { navController.navigate(HOME_ROUTE) },
                            icon = Icons.AutoMirrored.Outlined.More,
                            label = stringResource(R.string.manage_schedule),
                            modifier = Modifier.weight(1f),
                            dimensions = actionButtonDimensions,
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

