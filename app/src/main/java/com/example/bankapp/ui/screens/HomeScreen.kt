package com.example.bankapp.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.More
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bankapp.R
import com.example.bankapp.di.viewmodelfactory.HomeViewModelFactory
import com.example.bankapp.ui.components.HomeAppBar
import com.example.bankapp.ui.components.HomeDrawer
import com.example.bankapp.ui.components.HomeTransactionSection
import com.example.bankapp.ui.components.items.HomeScreenCard
import com.example.bankapp.ui.components.XLSpacer
import com.example.bankapp.ui.components.buttons.QuickActionsButton
import com.example.bankapp.ui.components.navigators.CASH_TRANSFER_ROUTE
import com.example.bankapp.ui.components.navigators.TRANSACTIONS_LOG_ROUTE
import com.example.bankapp.ui.theme.AppPadding
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.ui.theme.screenPadding
import com.example.bankapp.utilities.uiAccNo
import com.example.bankapp.viewmodels.HomeViewModel
import com.example.bankapp.viewmodels.TransactionsViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    windowSizeClass: WindowSizeClass,
    navController: NavController,
    homeViewModelFactory: HomeViewModelFactory,
    logoutAction: () -> Unit,
    transactionsViewModel: TransactionsViewModel
) {
    val homeViewModel: HomeViewModel = viewModel(factory = homeViewModelFactory)
    val account by homeViewModel.account.collectAsState()
    val transactions by transactionsViewModel.transactions.collectAsState()

    val scrollState = rememberScrollState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)


    val detailsCardColumnWidth = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 1f
        WindowWidthSizeClass.Medium -> 0.75f
        WindowWidthSizeClass.Expanded -> 0.6f
        else -> 0.9f
    }

    val drawerWidth = when(windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 0.75f
        WindowWidthSizeClass.Medium -> 0.60f
        WindowWidthSizeClass.Expanded -> 0.5f
        else -> 0.75f
    }

    HomeDrawer(drawerState = drawerState, logoutAction = logoutAction, username = homeViewModel.username, modifier = Modifier.fillMaxWidth(drawerWidth))
     {
        Scaffold(
            topBar = {
                HomeAppBar(homeViewModel.username, drawerState)
            }
        ) {
            contentPadding ->

            Column(
                modifier = AppPadding.padding(contentPadding)
                    .padding(screenPadding)
                    .padding(top = AppSpacing.md)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(detailsCardColumnWidth)
                ) {
                    HomeScreenCard(account.balance.toString(),account.accNo.uiAccNo, homeViewModel.isBalanceVisible, homeViewModel::onIsBalanceVisibleChange)
                }

                XLSpacer()

                Text(
                    text = stringResource(R.string.quick_actions_label),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth(detailsCardColumnWidth)
                        .padding(bottom = AppSpacing.sm)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    QuickActionsButton(
                        onClickAction = {},
                        icon = Icons.Outlined.AccountBalanceWallet,
                        label = stringResource(R.string.deposit_button),
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionsButton(
                        onClickAction = { navController.navigate(CASH_TRANSFER_ROUTE) },
                        icon = Icons.Outlined.SwapHoriz,
                        label = stringResource(R.string.cash_transfer_button),
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionsButton(
                        onClickAction = {},
                        icon = Icons.AutoMirrored.Outlined.More,
                        label = stringResource(R.string.more),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(AppSpacing.lg))

                HomeTransactionSection(transactions, {
                    navController.navigate(TRANSACTIONS_LOG_ROUTE)
                })
            }
        }
    }
}