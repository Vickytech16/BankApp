package com.example.bankapp.ui.components.navigators

import AuthorizationViewModel
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.bankapp.di.viewmodelfactory.AddBeneficiaryViewModelFactory
import com.example.bankapp.di.viewmodelfactory.CashTransferViewModelFactory
import com.example.bankapp.di.viewmodelfactory.ChangePasswordViewModelFactory
import com.example.bankapp.di.viewmodelfactory.CurrencyConvertorViewModelFactory
import com.example.bankapp.di.viewmodelfactory.DepositViewModelFactory
import com.example.bankapp.di.viewmodelfactory.FilterViewModelFactory
import com.example.bankapp.di.viewmodelfactory.ForgotPasswordViewModelFactory
import com.example.bankapp.di.viewmodelfactory.HomeViewModelFactory
import com.example.bankapp.di.viewmodelfactory.NotificationViewModelFactory
import com.example.bankapp.di.viewmodelfactory.OtpViewModelFactory
import com.example.bankapp.di.viewmodelfactory.PasswordConfirmationViewModelFactory
import com.example.bankapp.di.viewmodelfactory.PayToBeneficiaryViewModelFactory
import com.example.bankapp.di.viewmodelfactory.ProfileViewModelFactory
import com.example.bankapp.di.viewmodelfactory.TransactionDetailsViewModelFactory
import com.example.bankapp.di.viewmodelfactory.TransactionResultViewModelFactory
import com.example.bankapp.di.viewmodelfactory.TransactionsViewModelFactory
import com.example.bankapp.entities.SessionState
import com.example.bankapp.repositories.AccountRepository
import com.example.bankapp.repositories.BeneficiaryRepository
import com.example.bankapp.repositories.TransactionRepository
import com.example.bankapp.repositories.UserRepository
import com.example.bankapp.di.viewmodelfactory.ManageBeneficiaryViewModelFactory
import com.example.bankapp.di.viewmodelfactory.RecoveryKeyViewModelFactory
import com.example.bankapp.repositories.CountryRepository
import com.example.bankapp.repositories.CurrencyExchangeRepository
import com.example.bankapp.services.TransactionExportService
import com.example.bankapp.ui.screens.beneficiaryscreens.AddBeneficiaryScreen
import com.example.bankapp.ui.screens.payscreens.CashTransferScreen
import com.example.bankapp.ui.screens.payscreens.DepositScreen
import com.example.bankapp.ui.screens.HomeScreen
import com.example.bankapp.ui.screens.beneficiaryscreens.ManageBeneficiaryScreen
import com.example.bankapp.ui.screens.PasswordConfirmationScreen
import com.example.bankapp.ui.screens.payscreens.PayScreen
import com.example.bankapp.ui.screens.payscreens.PayToBeneficiaryScreen
import com.example.bankapp.ui.screens.ProfileScreen
import com.example.bankapp.ui.screens.TransactionDetailsScreen
import com.example.bankapp.ui.screens.TransactionResultScreen
import com.example.bankapp.ui.screens.TransactionsScreen
import com.example.bankapp.ui.screens.authscreens.ChangePasswordScreen
import com.example.bankapp.ui.screens.authscreens.ForgetPasswordScreen
import com.example.bankapp.ui.screens.authscreens.OtpScreen
import com.example.bankapp.ui.screens.authscreens.RecoveryKeyVerificationScreen
import com.example.bankapp.usecases.ChangePasswordUseCase
import com.example.bankapp.viewmodels.LoggedInSessionViewModel
import com.example.bankapp.viewmodels.ThemeViewModel
import com.example.bankapp.viewmodels.TransactionsViewModel

fun NavGraphBuilder.homeNavGraph(
    navController: NavController,
    windowSizeClass: WindowSizeClass,
    sessionState: SessionState,
    transactionRepository: TransactionRepository,
    accountRepository: AccountRepository,
    otpViewModelFactory: OtpViewModelFactory,
    notificationViewModelFactory: NotificationViewModelFactory,
    beneficiaryRepository: BeneficiaryRepository,
    userRepository: UserRepository,
    filterViewModelFactory: FilterViewModelFactory,
    transactionDetailsViewModelFactory: TransactionDetailsViewModelFactory,
    forgotPasswordViewModelFactory: ForgotPasswordViewModelFactory,
    changePasswordViewModelFactory: ChangePasswordViewModelFactory,
    recoveryKeyViewModelFactory: RecoveryKeyViewModelFactory,
    themeViewModel: ThemeViewModel,
    authorizationViewModel: AuthorizationViewModel,
    currencyExchangeRepository: CurrencyExchangeRepository,
    sessionViewModel: LoggedInSessionViewModel,
    countryRepository: CountryRepository,
    changePasswordUseCase: ChangePasswordUseCase,
    transactionExportService: TransactionExportService
) {
    navigation(
        startDestination = HOME_ROUTE,
        route = MAIN_ROUTE
    ) {
        if (sessionState is SessionState.Authenticated.AccountRegistered) {

            val transactionsViewModelFactory =
                TransactionsViewModelFactory(sessionState, transactionRepository, transactionExportService)
            val homeViewModelFactory =
                HomeViewModelFactory(sessionState, accountRepository)
            val cashTransferViewModelFactory =
                CashTransferViewModelFactory(sessionState, transactionRepository, beneficiaryRepository, accountRepository, authorizationViewModel, userRepository, currencyExchangeRepository )
            val depositViewModelFactory =
                DepositViewModelFactory(sessionState, authorizationViewModel)
            val passwordConfirmationViewModelFactory =
                PasswordConfirmationViewModelFactory(sessionState, sessionViewModel)
            val transactionResultViewModelFactory =
                TransactionResultViewModelFactory(transactionRepository, beneficiaryRepository, authorizationViewModel)
            val beneficiaryViewModelFactory =
                AddBeneficiaryViewModelFactory(userRepository = userRepository, beneficiaryRepository = beneficiaryRepository, sessionState = sessionState, authorizationViewModel = authorizationViewModel, accountRepository = accountRepository)
            val payToBeneficiaryViewModelFactory =
                PayToBeneficiaryViewModelFactory(beneficiaryRepository = beneficiaryRepository, sessionState = sessionState)
            val profileViewModelFactory =
                ProfileViewModelFactory(userRepository = userRepository, sessionState = sessionState, changePasswordUseCase = changePasswordUseCase)
            val manageBeneficiaryViewModelFactory =
                ManageBeneficiaryViewModelFactory(sessionState = sessionState, beneficiaryRepository = beneficiaryRepository)
            val currencyConvertorViewModelFactory =
                CurrencyConvertorViewModelFactory(sessionState = sessionState, currencyExchangeRepository = currencyExchangeRepository, countryRepository = countryRepository)

            composable(HOME_ROUTE) {
                val transactionsViewModel: TransactionsViewModel =
                    viewModel(factory = transactionsViewModelFactory)
                HomeScreen(
                    homeViewModelFactory = homeViewModelFactory,
                    navController = navController,
                    logoutAction = sessionViewModel::logout,
                    transactionsViewModel = transactionsViewModel
                )
            }

            composable(PAY_ROUTE) {
                PayScreen(
                    navController = navController,
                    windowSizeClass = windowSizeClass,
                    currencyConvertorViewModelFactory = currencyConvertorViewModelFactory
                )
            }

            composable(PROFILE_ROUTE) {
                ProfileScreen(
                    navController = navController,
                    windowSizeClass =  windowSizeClass,
                    profileViewModelFactory = profileViewModelFactory,
                    logoutAction = sessionViewModel::logout,
                    themeViewModel = themeViewModel
                )
            }

            composable(TRANSACTIONS_LOG_ROUTE) {
                val transactionsViewModel: TransactionsViewModel =
                    viewModel(factory = transactionsViewModelFactory)
                TransactionsScreen(
                    transactionsViewModel = transactionsViewModel,
                    navController = navController,
                    filterViewModelFactory = filterViewModelFactory,
                    windowSizeClass = windowSizeClass,
                )
            }

            composable(
                route = "$INDIVIDUAL_TRANSACTION_LOG_ROUTE/{transactionId}?origin={origin}",
                arguments = listOf(
                    navArgument("transactionId") {
                        type = NavType.StringType
                    },
                    navArgument("origin") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = "default"
                    }
                )
            ) { backStackEntry ->
                val transactionId = backStackEntry.arguments?.getString("transactionId") ?: ""
                val backRoute = backStackEntry.arguments?.getString("origin") ?: HOME_ROUTE
                TransactionDetailsScreen(
                    navController = navController,
                    transactionDetailsViewModelFactory = transactionDetailsViewModelFactory,
                    transactionId = transactionId,
                    windowSizeClass = windowSizeClass,
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

            composable(CASH_TRANSFER_ROUTE){
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
                    navArgument("backRoute") {
                        type = NavType.StringType
                        defaultValue = MAIN_ROUTE
                    }
                )
            ) { backStackEntry ->
                val backRoute = backStackEntry.arguments?.getString("backRoute") ?: MAIN_ROUTE

                PasswordConfirmationScreen(
                    navController = navController,
                    passwordConfirmationViewModelFactory = passwordConfirmationViewModelFactory,
                    onDismissRoute = backRoute,
                    onPasswordVerificationSuccess = {
                          authorizationViewModel.proceedAfterPassword(navController)
                    },
                    sessionViewModel = sessionViewModel
                )
            }

            composable(TRANSACTION_RESULT_ROUTE) {
                TransactionResultScreen(
                    transactionResultViewModelFactory,
                    navController,
                    authorizationViewModel
                )
            }

            composable(ADD_BENEFICIARY_ROUTE) {
                AddBeneficiaryScreen(
                    windowSizeClass,
                    beneficiaryViewModelFactory = beneficiaryViewModelFactory,
                    navController = navController
                )
            }

            composable(PAY_TO_BENEFICIARY_ROUTE) {
                PayToBeneficiaryScreen(
                    payToBeneficiaryViewModelFactory = payToBeneficiaryViewModelFactory,
                    navController = navController,
                    windowSizeClass = windowSizeClass
                )
            }

            composable(MANAGE_BENEFICIARY_ROUTE) {
                ManageBeneficiaryScreen(
                    manageBeneficiaryViewModelFactory,
                    navController = navController,
                    windowSizeClass = windowSizeClass,
                )
            }

            composable(
                route = "$HOME_OTP/{backRoute}",
                arguments = listOf(
                    navArgument("backRoute") {
                        type = NavType.StringType
                        defaultValue = MAIN_ROUTE
                    }
                )
            ) { backStackEntry ->
                val backRoute = backStackEntry.arguments?.getString("backRoute") ?: MAIN_ROUTE
                OtpScreen(
                    navController = navController,
                    notificationViewModelFactory = notificationViewModelFactory,
                    otpViewModelFactory = otpViewModelFactory,
                    backRoute = backRoute,
                    onOtpSuccess = {
                       authorizationViewModel.proceedAfterOtp(navController)
                    },
                    onDismiss = {

                    }
                )
            }

            composable(FORGOT_PASSWORD_ROUTE_HOME) {
                ForgetPasswordScreen(
                    navController = navController,
                    forgotPasswordViewModelFactory = forgotPasswordViewModelFactory
                )
            }

            composable(CHANGE_PASSWORD_ROUTE_HOME) {
                ChangePasswordScreen(
                    windowSizeClass = windowSizeClass,
                    navController = navController,
                    viewModelFactory = changePasswordViewModelFactory
                )
            }

            composable(RECOVERY_KEY_HOME_ROUTE) {
                RecoveryKeyVerificationScreen(
                    recoveryKeyViewModelFactory,
                    navController,
                    {navController.navigate(CHANGE_PASSWORD_ROUTE_HOME)}
                )
            }
        }
    }
}