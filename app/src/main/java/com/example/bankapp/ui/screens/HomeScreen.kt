package com.example.bankapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.bankapp.R
import com.example.bankapp.di.viewmodelfactory.HomeViewModelFactory
import com.example.bankapp.ui.components.HomeDrawer
import com.example.bankapp.ui.components.LargeSpacer
import com.example.bankapp.ui.components.appbar.HomeAppBar
import com.example.bankapp.ui.components.bottomnavbar.BottomNavigationBar
import com.example.bankapp.ui.components.buttons.ActionButton
import com.example.bankapp.ui.components.buttons.ButtonDimensions
import com.example.bankapp.ui.components.homeitems.HomeScreenCard
import com.example.bankapp.ui.components.homeitems.HomeTransactionSection
import com.example.bankapp.ui.components.navigators.CASH_TRANSFER_ROUTE
import com.example.bankapp.ui.components.navigators.DEPOSIT_ROUTE
import com.example.bankapp.ui.components.navigators.HOME_ROUTE
import com.example.bankapp.ui.components.navigators.PAY_TO_BENEFICIARY_ROUTE
import com.example.bankapp.ui.components.navigators.TRANSACTIONS_LOG_ROUTE
import com.example.bankapp.ui.components.screenModifier
import com.example.bankapp.ui.theme.LocalDeviceSpec
import com.example.bankapp.viewmodels.HomeViewModel
import com.example.bankapp.viewmodels.TransactionsViewModel




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModelFactory: HomeViewModelFactory,
    logoutAction: () -> Unit,
    transactionsViewModel: TransactionsViewModel
) {
    val homeViewModel: HomeViewModel = viewModel(factory = homeViewModelFactory)
    val account by homeViewModel.account.collectAsState()
    val user by homeViewModel.user.collectAsState()
    val transactions by transactionsViewModel.transactions.collectAsState()

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route ?: HOME_ROUTE

    val scrollState = rememberScrollState()
    val drawerState = rememberDrawerState(initialValue = homeViewModel.drawerState)

    val deviceSpec = LocalDeviceSpec.current
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val actionButtonDimensions = ButtonDimensions(
        buttonSize = deviceSpec.qabButtonSize,
        iconSize = deviceSpec.qabButtonIconSize,
        spacing = deviceSpec.qabButtonSpacing,
        labelStyle =deviceSpec.qabButtonLabelSize(),
    )

   HomeDrawer(
        drawerState = drawerState,
        logoutAction = logoutAction,
        username = user.userName,
        showLogoutDialog = homeViewModel.showLogoutDialog,
        onShowLogOutDialogChange = homeViewModel::onLogoutClickChange,
        modifier = Modifier.fillMaxWidth(deviceSpec.drawerWidth),
        content  = {
        Scaffold(
            topBar = {
                HomeAppBar(user.userName, drawerState, scrollBehavior, deviceSpec)
            },
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            bottomBar = {
                BottomNavigationBar(
                    currentRoute = currentRoute,
                    deviceSpec = deviceSpec,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(HOME_ROUTE) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            },
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { innerPadding ->
            Column(
                modifier = Modifier.screenModifier(contentPadding = innerPadding, scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    HomeScreenCard(
                        account = account,
                        isBalanceVisible = homeViewModel.isBalanceVisible,
                        onIsBalanceVisibleChange = homeViewModel::onIsBalanceVisibleChange,
                        deviceSpec = deviceSpec,
                        countryCode = user.countryCode
                    )
                }

                LargeSpacer()

                Row(
                    modifier = Modifier
                        .fillMaxWidth(deviceSpec.cardWidth)
                        .padding(horizontal = deviceSpec.HomeCardHorizontalPadding),
                    horizontalArrangement = Arrangement.spacedBy(deviceSpec.quickActionsSpacing)
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
                        dimensions = actionButtonDimensions
                    )
                   ActionButton(
                        onClickAction = { navController.navigate(DEPOSIT_ROUTE) },
                        icon = Icons.Outlined.AccountBalanceWallet,
                        label = stringResource(R.string.deposit_button),
                        modifier = Modifier.weight(1f),
                        dimensions = actionButtonDimensions
                    )
                }

                LargeSpacer()

                HomeTransactionSection(
                    transactions = transactions,
                    onSeeAllClickAction = {
                        navController.navigate(TRANSACTIONS_LOG_ROUTE)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = deviceSpec.HomeCardHorizontalPadding),
                    navController = navController,
                    deviceSpec = deviceSpec,
                    countryCode = user.countryCode
                )
            }
        }
    }
    )
}
