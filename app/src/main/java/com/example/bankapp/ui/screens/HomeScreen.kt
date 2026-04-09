package com.example.bankapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.bankapp.R
import com.example.bankapp.di.viewmodelfactory.HomeViewModelFactory
import com.example.bankapp.ui.components.homeitems.HomeDrawer
import com.example.bankapp.ui.components.LargeSpacer
import com.example.bankapp.ui.components.XLSpacer
import com.example.bankapp.ui.components.appbar.HomeAppBar
import com.example.bankapp.ui.components.bottomnavbar.BottomNavigationBar
import com.example.bankapp.ui.components.buttons.ActionButton
import com.example.bankapp.entities.uientities.uidata.QuickActionButtonDimensions
import com.example.bankapp.ui.components.homeitems.HomeCardCarousel
import com.example.bankapp.ui.components.homeitems.HomeTransactionSection
import com.example.bankapp.ui.components.navigators.CASH_TRANSFER_ROUTE
import com.example.bankapp.ui.components.navigators.DEPOSIT_ROUTE
import com.example.bankapp.ui.components.navigators.HOME_ROUTE
import com.example.bankapp.ui.components.navigators.PAY_TO_BENEFICIARY_ROUTE
import com.example.bankapp.ui.components.navigators.TRANSACTIONS_LOG_ROUTE
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

    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    // --- TRUE GLASS-TO-GLASS CALCULATION ---
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.roundToPx() }
    var containerXOffsetPx by remember { mutableStateOf(0f) }

    val carouselModifier = Modifier
        .fillMaxWidth()
        .onGloballyPositioned { coordinates ->
            // This now measures from the absolute Window root
            containerXOffsetPx = coordinates.positionInWindow().x
        }
        .layout { measurable, constraints ->
            val placeable = measurable.measure(
                constraints.copy(
                    minWidth = screenWidthPx,
                    maxWidth = screenWidthPx
                )
            )
            layout(constraints.maxWidth, placeable.height) {
                placeable.placeRelative(-containerXOffsetPx.toInt(), 0)
            }
        }

    HomeDrawer(
        drawerState = drawerState,
        logoutAction = logoutAction,
        username = user.userName,
        userPfpUrl = user.pfpURL,
        showLogoutDialog = homeViewModel.showLogoutDialog,
        onShowLogOutDialogChange = homeViewModel::onLogoutClickChange,
        modifier = Modifier.fillMaxWidth(deviceSpec.drawerWidth),
        content = {
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
                                popUpTo(HOME_ROUTE) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                },
                contentWindowInsets = WindowInsets(0, 0, 0, 0)
            ) { innerPadding ->

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(scrollState),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        // 2. Carousel: Placed directly in the Column.
                        // It will calculate its offset from the screen edge correctly here.
                        Box(
                            modifier = carouselModifier,
                            contentAlignment = Alignment.Center
                        ) {
                            HomeCardCarousel(
                                account = account,
                                velocityStatus = homeViewModel.velocityStatus.collectAsState().value,
                                isBalanceVisible = homeViewModel.isBalanceVisible,
                                isVelocityVisible = homeViewModel.isVelocityToggleVisible,
                                onBalanceToggle = homeViewModel::onIsBalanceVisibleChange,
                                onVelocityToggle = homeViewModel::onVelocityToggleVisibleChange,
                                deviceSpec = deviceSpec,
                                countryCode = user.countryCode
                            )
                        }

                        XLSpacer()

                        // 3. Everything ELSE is wrapped in a Column that respects Safe Insets
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .windowInsetsPadding(WindowInsets.safeContent.only(WindowInsetsSides.Horizontal))
                                .padding(horizontal = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val actionQuickActionButtonDimensions = QuickActionButtonDimensions(
                                buttonSize = deviceSpec.qabButtonSize,
                                iconSize = deviceSpec.qabButtonIconSize,
                                spacing = deviceSpec.qabButtonSpacing,
                                labelStyle = deviceSpec.qabButtonLabelSize(),
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(deviceSpec.cardWidth)
                                    .padding(horizontal = deviceSpec.HomeCardHorizontalPadding),
                                horizontalArrangement = Arrangement.spacedBy(deviceSpec.quickActionsSpacing)
                            ) {
                                ActionButton(
                                    onClickAction = { navController.navigate("$PAY_TO_BENEFICIARY_ROUTE?origin=$HOME_ROUTE") },
                                    icon = Icons.Outlined.AccountBalance,
                                    label = stringResource(R.string.pay_to_friend),
                                    modifier = Modifier.weight(1f),
                                    dimensions = actionQuickActionButtonDimensions
                                )
                                ActionButton(
                                    onClickAction = { navController.navigate("$CASH_TRANSFER_ROUTE?origin=$HOME_ROUTE") },
                                    icon = Icons.Outlined.SwapHoriz,
                                    label = stringResource(R.string.pay_anyone),
                                    modifier = Modifier.weight(1f),
                                    dimensions = actionQuickActionButtonDimensions
                                )
                                ActionButton(
                                    onClickAction = { navController.navigate("$DEPOSIT_ROUTE?origin=$HOME_ROUTE") },
                                    icon = Icons.Outlined.AccountBalanceWallet,
                                    label = stringResource(R.string.deposit_button),
                                    modifier = Modifier.weight(1f),
                                    dimensions = actionQuickActionButtonDimensions
                                )
                            }

                            LargeSpacer()

                            HomeTransactionSection(
                                transactions = transactions,
                                onSeeAllClickAction = { navController.navigate(TRANSACTIONS_LOG_ROUTE) },
                                modifier = Modifier.fillMaxWidth().padding(horizontal = deviceSpec.HomeCardHorizontalPadding),
                                navController = navController,
                                deviceSpec = deviceSpec,
                                countryCode = user.countryCode
                            )

                            LargeSpacer()
                        }
                    }
                }
            }
        }
    )
}