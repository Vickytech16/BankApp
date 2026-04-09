package com.example.bankapp.ui.components.navigators

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
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
import com.example.bankapp.viewmodels.AuthorizationViewModel
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
    val animSpeed = 700

    navigation(
        startDestination = HOME_ROUTE,
        route = MAIN_ROUTE
    ) {
        if (sessionState is SessionState.Authenticated.AccountRegistered) {

            val transactionsViewModelFactory = TransactionsViewModelFactory(sessionState, transactionRepository, transactionExportService)
            val transactionDetailsViewModelFactory = TransactionDetailsViewModelFactory(transactionRepository, transactionExportService)
            val homeViewModelFactory = HomeViewModelFactory(sessionState, transactionRepository)
            val cashTransferViewModelFactory = CashTransferViewModelFactory(sessionState, transactionRepository, beneficiaryRepository, accountRepository, authorizationViewModel, userRepository, currencyExchangeRepository )
            val depositViewModelFactory = DepositViewModelFactory(sessionState, authorizationViewModel, currencyExchangeRepository)
            val passwordConfirmationViewModelFactory = PasswordConfirmationViewModelFactory(sessionState, sessionViewModel)
            val transactionResultViewModelFactory = TransactionResultViewModelFactory(transactionRepository, beneficiaryRepository, authorizationViewModel)
            val beneficiaryViewModelFactory = AddBeneficiaryViewModelFactory(userRepository, beneficiaryRepository, sessionState, authorizationViewModel, accountRepository)
            val payToBeneficiaryViewModelFactory = PayToBeneficiaryViewModelFactory(beneficiaryRepository, sessionState)
            val profileViewModelFactory = ProfileViewModelFactory(userRepository, sessionState, changePasswordState)
            val manageBeneficiaryViewModelFactory = ManageBeneficiaryViewModelFactory(sessionState, beneficiaryRepository)
            val currencyConvertorViewModelFactory = CurrencyConvertorViewModelFactory(sessionState =sessionState, currencyExchangeRepository =  currencyExchangeRepository, countryRepository =  countryRepository)

            composable(route = HOME_ROUTE,) {
                val transactionsViewModel: TransactionsViewModel = viewModel(factory = transactionsViewModelFactory)
                HomeScreen(
                    homeViewModelFactory = homeViewModelFactory,
                    navController = navController,
                    logoutAction = sessionViewModel::logout,
                    transactionsViewModel = transactionsViewModel
                )
            }

            composable(route = PAY_ROUTE) {
                PayScreen(
                    navController = navController,
                    currencyConvertorViewModelFactory = currencyConvertorViewModelFactory
                )
            }

            composable(route = PROFILE_ROUTE) {
                ProfileScreen(
                    navController = navController,
                    profileViewModelFactory = profileViewModelFactory,
                    logoutAction = sessionViewModel::logout,
                    themeViewModel = themeViewModel
                )
            }

            composable(TRANSACTIONS_LOG_ROUTE){
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
                ),
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

            composable(
                route = "$CASH_TRANSFER_ROUTE/{friendAccNo}?origin={origin}",
                arguments = listOf(
                    navArgument("friendAccNo") { type = NavType.StringType },
                    navArgument("origin") { type = NavType.StringType; defaultValue = HOME_ROUTE }
                ),
                enterTransition = {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(animSpeed))
                },
                exitTransition = {
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(animSpeed))
                },
                popExitTransition = {
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(animSpeed))
                }
            ) { backStackEntry ->
                val origin = backStackEntry.arguments?.getString("origin") ?: HOME_ROUTE
                CashTransferFlowHost(
                    navController = navController,
                    authorizationViewModel = authorizationViewModel,
                    cashTransferViewModelFactory = cashTransferViewModelFactory,
                    otpViewModelFactory = otpViewModelFactory,
                    passwordConfirmationViewModelFactory = passwordConfirmationViewModelFactory,
                    sessionViewModel = sessionViewModel,
                    origin = origin
                )
            }

            composable(
                route = "$CASH_TRANSFER_ROUTE?origin={origin}",
                arguments = listOf(
                    navArgument("origin") { type = NavType.StringType; defaultValue = HOME_ROUTE }
                ),
                enterTransition = {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(animSpeed))
                },
                exitTransition = {
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(animSpeed))
                },
                popExitTransition = {
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(animSpeed))
                }
            ) { backStackEntry ->
                val origin = backStackEntry.arguments?.getString("origin") ?: HOME_ROUTE
                CashTransferFlowHost(
                    navController = navController,
                    authorizationViewModel = authorizationViewModel,
                    cashTransferViewModelFactory = cashTransferViewModelFactory,
                    otpViewModelFactory = otpViewModelFactory,
                    passwordConfirmationViewModelFactory = passwordConfirmationViewModelFactory,
                    sessionViewModel = sessionViewModel,
                    origin = origin
                )
            }

            composable(
                    route = "$DEPOSIT_ROUTE?origin={origin}",
                    arguments = listOf(
                        navArgument("origin") { type = NavType.StringType; defaultValue = HOME_ROUTE }
                    ),
                enterTransition = {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(animSpeed))
                },
                exitTransition = {
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(animSpeed))
                },
                popEnterTransition = {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(animSpeed))
                },
                popExitTransition = {
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(animSpeed))
                }
            ) {backStackEntry ->
                val origin = backStackEntry.arguments?.getString("origin") ?: HOME_ROUTE
                DepositFlowHost(
                    navController = navController,
                    authorizationViewModel = authorizationViewModel,
                    depositViewModelFactory = depositViewModelFactory,
                    otpViewModelFactory = otpViewModelFactory,
                    passwordConfirmationViewModelFactory = passwordConfirmationViewModelFactory,
                    sessionViewModel = sessionViewModel,
                    origin = origin
                )
            }

            composable(
                route = "$TRANSACTION_RESULT_ROUTE?origin={origin}",
                arguments = listOf(
                    navArgument("origin") {
                        type = NavType.StringType
                        defaultValue = HOME_ROUTE
                    }
                ),
                enterTransition = {
                    EnterTransition.None
                },
                exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(animSpeed)
                    )
                }
            ) {backStackEntry ->
                val origin = backStackEntry.arguments?.getString("origin") ?: HOME_ROUTE
                TransactionResultScreen(transactionResultViewModelFactory, navController, authorizationViewModel,
                    onDone = {
                    navController.navigate(origin) {
                        popUpTo(MAIN_ROUTE) { inclusive = false }
                        launchSingleTop = true
                        restoreState = true
                    }
                })
            }


            composable(
                route = ADD_BENEFICIARY_ROUTE,
                enterTransition = {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(animSpeed))
                },
                exitTransition = {
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(animSpeed))
                },
                popEnterTransition = {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(animSpeed))
                },
                popExitTransition = {
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(animSpeed))
                }
            ){
                AddBeneficiaryFlowHost(
                    navController = navController,
                    authorizationViewModel = authorizationViewModel,
                    addBeneficiaryViewModelFactory = beneficiaryViewModelFactory,
                    otpViewModelFactory = otpViewModelFactory,
                    passwordConfirmationViewModelFactory = passwordConfirmationViewModelFactory,
                    sessionViewModel = sessionViewModel,

                )
            }

            composable(
                route = "$PAY_TO_BENEFICIARY_ROUTE?origin={origin}",
                arguments = listOf(
                    navArgument("origin") { type = NavType.StringType; defaultValue = PAY_ROUTE }
                )
            ){
                backStackEntry ->
                val origin = backStackEntry.arguments?.getString("origin") ?: PAY_ROUTE
                PayToBeneficiaryScreen(payToBeneficiaryViewModelFactory, navController, origin = origin)
            }

            composable(
                route = MANAGE_BENEFICIARY_ROUTE,
                enterTransition = {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(animSpeed))
                },
                exitTransition = {
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(animSpeed))
                },
                popEnterTransition = {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(animSpeed))
                },
                popExitTransition = {
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(animSpeed))
                }
            ){
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
                    onOtpSuccess = { authorizationViewModel.proceedAfterOtp(navController, HOME_ROUTE) }
                )
            }

            composable(
                route = CHANGE_PASSWORD_ROUTE_HOME,
                enterTransition = {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(animSpeed))
                },
                exitTransition = {
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(animSpeed))
                },
                popEnterTransition = {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(animSpeed))
                },
                popExitTransition = {
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(animSpeed))
                }
            ){
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