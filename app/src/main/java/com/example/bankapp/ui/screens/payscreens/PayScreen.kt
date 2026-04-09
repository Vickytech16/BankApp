package com.example.bankapp.ui.screens.payscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.More
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Handshake
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.bankapp.R
import com.example.bankapp.di.viewmodelfactory.CurrencyConvertorViewModelFactory
import com.example.bankapp.ui.components.CurrencyConverter
import com.example.bankapp.ui.components.XLSpacer
import com.example.bankapp.ui.components.appbar.RegularAppBar
import com.example.bankapp.ui.components.bottomnavbar.BottomNavigationBar
import com.example.bankapp.ui.components.buttons.ActionButton
import com.example.bankapp.entities.uientities.uidata.QuickActionButtonDimensions
import com.example.bankapp.ui.components.navigators.HOME_ROUTE
import com.example.bankapp.ui.components.navigators.PAY_ROUTE
import com.example.bankapp.ui.components.navigators.ADD_BENEFICIARY_ROUTE
import com.example.bankapp.ui.components.navigators.CASH_TRANSFER_ROUTE
import com.example.bankapp.ui.components.navigators.DEPOSIT_ROUTE
import com.example.bankapp.ui.components.navigators.MANAGE_BENEFICIARY_ROUTE
import com.example.bankapp.ui.components.navigators.PAY_TO_BENEFICIARY_ROUTE
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.ui.theme.DeviceSpec
import com.example.bankapp.ui.theme.LocalDeviceSpec
import com.example.bankapp.viewmodels.CurrencyConvertorViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayScreen(
    navController: NavController,
    currencyConvertorViewModelFactory: CurrencyConvertorViewModelFactory
) {
    val currencyConvertorViewModel: CurrencyConvertorViewModel = viewModel(factory = currencyConvertorViewModelFactory)
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route ?: PAY_ROUTE

    val scrollState = rememberScrollState()
    val deviceSpec = LocalDeviceSpec.current
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val actionQuickActionButtonDimensions =
        QuickActionButtonDimensions(
        buttonSize = deviceSpec.qabButtonSize,
        iconSize = deviceSpec.qabButtonIconSize,
        spacing = deviceSpec.qabButtonSpacing,
        labelStyle =deviceSpec.qabButtonLabelSize(),
    )

    Scaffold(
        topBar = {
            RegularAppBar(stringResource(R.string.pay_screen_title), scrollBehavior = scrollBehavior)
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        bottomBar = {
            BottomNavigationBar(
                currentRoute = currentRoute,
                onNavigate = {
                    route ->
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
    ) {
        contentPadding ->

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
                    .padding(top = AppSpacing.md, bottom = AppSpacing.xxl),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                XLSpacer()



                PaySectionCard(
                    title = stringResource(R.string.pay_section_label),
                    deviceSpec = deviceSpec
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ActionButton(
                            onClickAction = { navController.navigate("$PAY_TO_BENEFICIARY_ROUTE?origin=$PAY_ROUTE") },
                            icon = Icons.Outlined.AccountBalance,
                            label = stringResource(R.string.pay_to_friend),
                            modifier = Modifier.weight(1f),
                            dimensions = actionQuickActionButtonDimensions
                        )
                        ActionButton(
                            onClickAction = { navController.navigate("$CASH_TRANSFER_ROUTE?origin=$PAY_ROUTE") },
                            icon = Icons.Outlined.SwapHoriz,
                            label = stringResource(R.string.pay_anyone),
                            modifier = Modifier.weight(1f),
                            dimensions = actionQuickActionButtonDimensions,
                        )
                        ActionButton(
                            onClickAction = { navController.navigate("$DEPOSIT_ROUTE?origin=$PAY_ROUTE") },
                            icon = Icons.Outlined.AccountBalanceWallet,
                            label = stringResource(R.string.deposit_button),
                            modifier = Modifier.weight(1f),
                            dimensions = actionQuickActionButtonDimensions,
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
                            icon = Icons.Outlined.Handshake,
                            label = stringResource(R.string.add_beneficiary),
                            modifier = Modifier.weight(1f),
                            dimensions = actionQuickActionButtonDimensions,

                        )
                        ActionButton(
                            onClickAction = { navController.navigate(MANAGE_BENEFICIARY_ROUTE) },
                            icon = Icons.AutoMirrored.Outlined.More,
                            label = stringResource(R.string.manage_beneficiary),
                            modifier = Modifier.weight(1f),
                            dimensions = actionQuickActionButtonDimensions,
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }

                Spacer(modifier = Modifier.height(deviceSpec.payScreenSectionSpacing))

                CurrencyConverter(
                    viewModel = currencyConvertorViewModel,
                    deviceSpec = deviceSpec
                )
            }
        }
    }
}

@Composable
fun PaySectionCard(
    title: String,
    deviceSpec: DeviceSpec,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    outlineColor: Color = Color.Transparent,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = containerColor,
                shape = RoundedCornerShape(AppSpacing.xl)
            )
            .let {
                if (outlineColor != Color.Transparent) it.border(1.dp, outlineColor, RoundedCornerShape(AppSpacing.xl))
                else it
            }
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
