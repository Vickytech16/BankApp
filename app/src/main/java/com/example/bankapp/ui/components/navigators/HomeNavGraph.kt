package com.example.bankapp.ui.components.navigators

import AuthorizationViewModel
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.bankapp.di.viewmodelfactory.*
import com.example.bankapp.entities.SessionState
import com.example.bankapp.repositories.*
import com.example.bankapp.services.TransactionExportService
import com.example.bankapp.ui.screens.*
import com.example.bankapp.ui.screens.beneficiaryscreens.*
import com.example.bankapp.ui.screens.payscreens.*
import com.example.bankapp.ui.screens.authscreens.*
import com.example.bankapp.entities.ChangePasswordState
import com.example.bankapp.viewmodels.SessionViewModel
import com.example.bankapp.viewmodels.ThemeViewModel
import com.example.bankapp.viewmodels.TransactionsViewModel

fun NavGraphBuilder.homeNavGraph(
    navController: NavController,
    windowSizeClass: WindowSizeClass,
    sessionState: SessionState,
    transactionRepository: TransactionRepository,
    accountRepository: AccountRepository,
    otpViewModelFactory: OtpViewModelFactory,
    beneficiaryRepository: BeneficiaryRepository,
    userRepository: UserRepository,
    filterViewModelFactory: FilterViewModelFactory,

    changePasswordViewModelFactory: ChangePasswordViewModelFactory,
    recoveryKeyViewModelFactory: RecoveryKeyViewModelFactory,
    themeViewModel: ThemeViewModel,
    authorizationViewModel: AuthorizationViewModel,
    currencyExchangeRepository: CurrencyExchangeRepository,
    sessionViewModel: SessionViewModel,
    countryRepository: CountryRepository,
    changePasswordState: ChangePasswordState,
    transactionExportService: TransactionExportService
) {
    val routeOrder = listOf(HOME_ROUTE, PAY_ROUTE, PROFILE_ROUTE)
    val animSpeed = 500 // Slower animation

    navigation(
        startDestination = HOME_ROUTE,
        route = MAIN_ROUTE
    ) {
        if (sessionState is SessionState.Authenticated.AccountRegistered) {

            val transactionsViewModelFactory = TransactionsViewModelFactory(sessionState, transactionRepository, transactionExportService)
            val transactionDetailsViewModelFactory = TransactionDetailsViewModelFactory(transactionRepository, transactionExportService)
            val homeViewModelFactory = HomeViewModelFactory(sessionState, transactionRepository)
            val cashTransferViewModelFactory = CashTransferViewModelFactory(sessionState, transactionRepository, beneficiaryRepository, accountRepository, authorizationViewModel, userRepository, currencyExchangeRepository )
            val depositViewModelFactory = DepositViewModelFactory(sessionState, authorizationViewModel)
            val passwordConfirmationViewModelFactory = PasswordConfirmationViewModelFactory(sessionState, sessionViewModel)
            val transactionResultViewModelFactory = TransactionResultViewModelFactory(transactionRepository, beneficiaryRepository, authorizationViewModel)
            val beneficiaryViewModelFactory = AddBeneficiaryViewModelFactory(userRepository, beneficiaryRepository, sessionState, authorizationViewModel, accountRepository)
            val payToBeneficiaryViewModelFactory = PayToBeneficiaryViewModelFactory(beneficiaryRepository, sessionState)
            val profileViewModelFactory = ProfileViewModelFactory(userRepository, sessionState, changePasswordState)
            val manageBeneficiaryViewModelFactory = ManageBeneficiaryViewModelFactory(sessionState, beneficiaryRepository)
            val currencyConvertorViewModelFactory = CurrencyConvertorViewModelFactory(sessionState =sessionState, currencyExchangeRepository =  currencyExchangeRepository, countryRepository =  countryRepository)

            composable(
                route = HOME_ROUTE,
                enterTransition = {
                    val initialRoute = initialState.destination.route
                    if (initialRoute in routeOrder) {
                        val initialIndex = routeOrder.indexOf(initialRoute)
                        val targetIndex = routeOrder.indexOf(HOME_ROUTE)
                        if (targetIndex > initialIndex) slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(animSpeed))
                        else slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(animSpeed))
                    } else null
                },
                exitTransition = {
                    val targetRoute = targetState.destination.route
                    if (targetRoute in routeOrder) {
                        val initialIndex = routeOrder.indexOf(HOME_ROUTE)
                        val targetIndex = routeOrder.indexOf(targetRoute)
                        if (targetIndex > initialIndex) slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(animSpeed))
                        else slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(animSpeed))
                    } else null
                }
            ) {
                val transactionsViewModel: TransactionsViewModel = viewModel(factory = transactionsViewModelFactory)
                HomeScreen(
                    homeViewModelFactory = homeViewModelFactory,
                    navController = navController,
                    logoutAction = sessionViewModel::logout,
                    transactionsViewModel = transactionsViewModel
                )
            }

            composable(
                route = PAY_ROUTE,
                enterTransition = {
                    val initialRoute = initialState.destination.route
                    if (initialRoute in routeOrder) {
                        val initialIndex = routeOrder.indexOf(initialRoute)
                        val targetIndex = routeOrder.indexOf(PAY_ROUTE)
                        if (targetIndex > initialIndex) slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(animSpeed))
                        else slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(animSpeed))
                    } else null
                },
                exitTransition = {
                    val targetRoute = targetState.destination.route
                    if (targetRoute in routeOrder) {
                        val initialIndex = routeOrder.indexOf(PAY_ROUTE)
                        val targetIndex = routeOrder.indexOf(targetRoute)
                        if (targetIndex > initialIndex) slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(animSpeed))
                        else slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(animSpeed))
                    } else null
                }
            ) {
                PayScreen(
                    navController = navController,
                    currencyConvertorViewModelFactory = currencyConvertorViewModelFactory
                )
            }

            composable(
                route = PROFILE_ROUTE,
                enterTransition = {
                    val initialRoute = initialState.destination.route
                    if (initialRoute in routeOrder) {
                        val initialIndex = routeOrder.indexOf(initialRoute)
                        val targetIndex = routeOrder.indexOf(PROFILE_ROUTE)
                        if (targetIndex > initialIndex) slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(animSpeed))
                        else slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(animSpeed))
                    } else null
                },
                exitTransition = {
                    val targetRoute = targetState.destination.route
                    if (targetRoute in routeOrder) {
                        val initialIndex = routeOrder.indexOf(PROFILE_ROUTE)
                        val targetIndex = routeOrder.indexOf(targetRoute)
                        if (targetIndex > initialIndex) slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(animSpeed))
                        else slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(animSpeed))
                    } else null
                }
            ) {
                ProfileScreen(
                    navController = navController,
                    profileViewModelFactory = profileViewModelFactory,
                    logoutAction = sessionViewModel::logout,
                    themeViewModel = themeViewModel
                )
            }

            composable(TRANSACTIONS_LOG_ROUTE) {
                val transactionsViewModel: TransactionsViewModel = viewModel(factory = transactionsViewModelFactory)
                TransactionsScreen(
                    transactionsViewModel = transactionsViewModel,
                    navController = navController,
                    filterViewModelFactory = filterViewModelFactory,
                )
            }

            composable(
                route = "$INDIVIDUAL_TRANSACTION_LOG_ROUTE/{transactionId}?origin={origin}",
                arguments = listOf(
                    navArgument("transactionId") { type = NavType.StringType },
                    navArgument("origin") { type = NavType.StringType; nullable = true; defaultValue = "default" }
                )
            ) { backStackEntry ->
                val transactionId = backStackEntry.arguments?.getString("transactionId") ?: ""
                val backRoute = backStackEntry.arguments?.getString("origin") ?: HOME_ROUTE
                TransactionDetailsScreen(
                    navController = navController,
                    transactionDetailsViewModelFactory = transactionDetailsViewModelFactory,
                    transactionId = transactionId,
                    backRoute = backRoute,
                    sessionState = sessionState
                )
            }

            composable("$CASH_TRANSFER_ROUTE/{friendAccNo}") { backStackEntry ->
                CashTransferScreen(
                    windowSizeClass = windowSizeClass,
                    cashTransferViewModelFactory = cashTransferViewModelFactory,
                    navController = navController,
                    friendAccNo = backStackEntry.arguments?.getString("friendAccNo")
                )
            }

            composable(CASH_TRANSFER_ROUTE) {
                CashTransferScreen(
                    windowSizeClass = windowSizeClass,
                    cashTransferViewModelFactory = cashTransferViewModelFactory,
                    navController = navController
                )
            }

            composable(DEPOSIT_ROUTE) {
                DepositScreen(
                    depositViewModelFactory = depositViewModelFactory,
                    navController = navController
                )
            }

            composable(
                route = "$PASSWORD_CONFIRMATION_ROUTE/{backRoute}",
                arguments = listOf(
                    navArgument("backRoute") { type = NavType.StringType; defaultValue = MAIN_ROUTE }
                )
            ) { backStackEntry ->
                val backRoute = backStackEntry.arguments?.getString("backRoute") ?: MAIN_ROUTE
                PasswordConfirmationScreen(
                    navController = navController,
                    passwordConfirmationViewModelFactory = passwordConfirmationViewModelFactory,
                    onDismissRoute = backRoute,
                    onPasswordVerificationSuccess = { authorizationViewModel.proceedAfterPassword(navController) },
                    sessionViewModel = sessionViewModel
                )
            }

            composable(TRANSACTION_RESULT_ROUTE) {
                TransactionResultScreen(transactionResultViewModelFactory, navController, authorizationViewModel)
            }

            composable(ADD_BENEFICIARY_ROUTE) {
                AddBeneficiaryScreen(beneficiaryViewModelFactory = beneficiaryViewModelFactory, navController =  navController)
            }

            composable(PAY_TO_BENEFICIARY_ROUTE) {
                PayToBeneficiaryScreen(payToBeneficiaryViewModelFactory, navController)
            }

            composable(MANAGE_BENEFICIARY_ROUTE) {
                ManageBeneficiaryScreen(manageBeneficiaryViewModelFactory, navController)
            }

            composable(
                route = "$HOME_OTP/{backRoute}",
                arguments = listOf(
                    navArgument("backRoute") { type = NavType.StringType; defaultValue = MAIN_ROUTE }
                )
            ) { backStackEntry ->
                val backRoute = backStackEntry.arguments?.getString("backRoute") ?: MAIN_ROUTE
                OtpScreen(
                    navController = navController,
                    otpViewModelFactory = otpViewModelFactory,
                    backRoute = backRoute,
                    popUpRoute = HOME_ROUTE,
                    onOtpSuccess = { authorizationViewModel.proceedAfterOtp(navController) }
                )
            }

            composable(CHANGE_PASSWORD_ROUTE_HOME) {
                ChangePasswordScreen(
                    navController = navController,
                    viewModelFactory = changePasswordViewModelFactory,
                    PROFILE_ROUTE,
                    popUpRoute = HOME_ROUTE,
                    {
                        navController.navigate(PROFILE_ROUTE) {
                            popUpTo(HOME_ROUTE) { inclusive = false }
                        }
                    }
                )
            }

            composable(RECOVERY_KEY_HOME_ROUTE) {
                RecoveryKeyVerificationScreen(
                    recoveryKeyViewModelFactory,
                    navController,
                    backRoute = PROFILE_ROUTE,
                    popUpRoute = HOME_ROUTE,
                    onSuccess = { navController.navigate(CHANGE_PASSWORD_ROUTE_HOME) }
                )
            }
        }
    }
}