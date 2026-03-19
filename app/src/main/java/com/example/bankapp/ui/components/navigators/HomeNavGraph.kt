package com.example.bankapp.ui.components.navigators

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.bankapp.di.viewmodelfactory.AddBeneficiaryResultViewModelFactory
import com.example.bankapp.di.viewmodelfactory.AddBeneficiaryViewModelFactory
import com.example.bankapp.di.viewmodelfactory.CashTransferViewModelFactory
import com.example.bankapp.di.viewmodelfactory.DepositViewModelFactory
import com.example.bankapp.di.viewmodelfactory.FilterViewModelFactory
import com.example.bankapp.di.viewmodelfactory.HomeViewModelFactory
import com.example.bankapp.di.viewmodelfactory.NotificationViewModelFactory
import com.example.bankapp.di.viewmodelfactory.OtpViewModelFactory
import com.example.bankapp.di.viewmodelfactory.PasswordConfirmationViewModelFactory
import com.example.bankapp.di.viewmodelfactory.TransactionDetailsViewModelFactory
import com.example.bankapp.di.viewmodelfactory.TransactionResultViewModelFactory
import com.example.bankapp.di.viewmodelfactory.TransactionsViewModelFactory
import com.example.bankapp.entities.SessionState
import com.example.bankapp.repositories.AccountRepository
import com.example.bankapp.repositories.BeneficiaryRepository
import com.example.bankapp.repositories.TransactionRepository
import com.example.bankapp.repositories.UserRepository
import com.example.bankapp.ui.screens.AddBeneficiaryResultScreen
import com.example.bankapp.ui.screens.AddBeneficiaryScreen
import com.example.bankapp.ui.screens.CashTransferScreen
import com.example.bankapp.ui.screens.DepositScreen
import com.example.bankapp.ui.screens.HomeScreen
import com.example.bankapp.ui.screens.PasswordConfirmationScreen
import com.example.bankapp.ui.screens.PayScreen
import com.example.bankapp.ui.screens.ProfileScreen
import com.example.bankapp.ui.screens.TransactionDetailsScreen
import com.example.bankapp.ui.screens.TransactionResultScreen
import com.example.bankapp.ui.screens.TransactionsScreen
import com.example.bankapp.ui.screens.authscreens.OtpScreen
import com.example.bankapp.usecases.CurrentSessionIntent
import com.example.bankapp.usecases.HomeSessionHandler
import com.example.bankapp.usecases.TransactionSessionHolder
import com.example.bankapp.viewmodels.TransactionsViewModel

sealed class TransactionScreen(val route: String) {
    object TransactionLogScreen : TransactionScreen(TRANSACTIONS_LOG_ROUTE)
    object CashTransferDetailScreen : TransactionScreen("$INDIVIDUAL_TRANSACTION_LOG_ROUTE/{transactionId}") {
        fun createRoute(transactionId: String): String = "$INDIVIDUAL_TRANSACTION_LOG_ROUTE/$transactionId"
    }
}

object TransactionSessionManager {
    private var _currentHandler: HomeSessionHandler? = null

    val currentHandler: HomeSessionHandler
        get() = _currentHandler ?: throw IllegalStateException(
            "Transaction handler not initialized. Call setHandler() first."
        )

    fun setHandler(handler: HomeSessionHandler) {
        _currentHandler = handler
    }

    fun setHandlerByIntent(intent: CurrentSessionIntent) {
        _currentHandler = HomeSessionHandler.getInstance(intent)
    }

    fun reset() {
        _currentHandler = null
    }

    fun isInitialized(): Boolean = _currentHandler != null
}

fun NavGraphBuilder.homeNavGraph(
    navController: NavController,
    windowSizeClass: WindowSizeClass,
    sessionState: SessionState,
    transactionRepository: TransactionRepository,
    accountRepository: AccountRepository,
    logoutAction: () -> Unit,
    otpViewModelFactory: OtpViewModelFactory,
    notificationViewModelFactory: NotificationViewModelFactory,
    transactionSessionHolder: TransactionSessionHolder,
    beneficiaryRepository: BeneficiaryRepository,
    userRepository: UserRepository
) {
    navigation(
        startDestination = HOME_ROUTE,
        route = MAIN_ROUTE
    ) {
        if (sessionState is SessionState.Authenticated.AccountRegistered) {

            val transactionsViewModelFactory =
                TransactionsViewModelFactory(sessionState, transactionRepository)
            val homeViewModelFactory =
                HomeViewModelFactory(sessionState, accountRepository)
            val cashTransferViewModelFactory =
                CashTransferViewModelFactory(sessionState, transactionRepository,
                    beneficiaryRepository,
                    accountRepository )
            val depositViewModelFactory =
                DepositViewModelFactory(sessionState, transactionRepository)
            val filterViewModelFactory =
                FilterViewModelFactory()
            val transactionDetailsViewModelFactory =
                TransactionDetailsViewModelFactory(transactionRepository)
            val passwordConfirmationViewModelFactory =
                PasswordConfirmationViewModelFactory(sessionState, transactionSessionHolder)
            val transactionResultViewModelFactory =
                TransactionResultViewModelFactory(sessionState,transactionRepository)
            val beneficiaryViewModelFactory =
                AddBeneficiaryViewModelFactory(
                    userRepository = userRepository,
                    transactionSessionHolder = transactionSessionHolder,
                    beneficiaryRepository = beneficiaryRepository,
                    sessionState = sessionState
                )
            val addBeneficiaryResultViewModelFactory = AddBeneficiaryResultViewModelFactory(
                transactionSessionHolder,
                beneficiaryRepository
            )




            composable(HOME_ROUTE) {
                val transactionsViewModel: TransactionsViewModel =
                    viewModel(factory = transactionsViewModelFactory)
                HomeScreen(
                    windowSizeClass = windowSizeClass,
                    homeViewModelFactory = homeViewModelFactory,
                    navController = navController,
                    logoutAction = logoutAction,
                    transactionsViewModel = transactionsViewModel
                )
            }

            composable(PAY_ROUTE) {
                PayScreen(navController = navController, windowSizeClass = windowSizeClass)
            }

            composable(PROFILE_ROUTE) {
                ProfileScreen(navController)
            }

            composable(TRANSACTIONS_LOG_ROUTE) {
                val transactionsViewModel: TransactionsViewModel =
                    viewModel(factory = transactionsViewModelFactory)
                TransactionsScreen(
                    transactionsViewModel = transactionsViewModel,
                    navController = navController,
                    filterViewModelFactory = filterViewModelFactory,
                )
            }

            composable(
                route = "$INDIVIDUAL_TRANSACTION_LOG_ROUTE/{transactionId}",
                arguments = listOf(
                    navArgument("transactionId") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                val transactionId = backStackEntry.arguments?.getString("transactionId") ?: ""
                TransactionDetailsScreen(
                    navController = navController,
                    transactionDetailsViewModelFactory = transactionDetailsViewModelFactory,
                    transactionId = transactionId,
                    windowSizeClass = windowSizeClass
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
                    windowSizeClass = windowSizeClass,
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
                        val homeSessionHandler = TransactionSessionManager.currentHandler
                        homeSessionHandler.onPasswordSuccess()
                    }
                )
            }

            composable(TRANSACTION_RESULT_ROUTE) {
                TransactionResultScreen(
                    transactionResultViewModelFactory,
                    navController
                )
            }

            composable(ADD_BENEFICIARY_ROUTE) {
                AddBeneficiaryScreen(
                    windowSizeClass,
                    beneficiaryViewModelFactory = beneficiaryViewModelFactory,
                    navController = navController,
                    transactionSessionHolder = transactionSessionHolder
                )
            }

            composable("ADD_BENEFICIARY_RESULT_ROUTE") {
                AddBeneficiaryResultScreen(
                    navController = navController,
                    transactionSessionHolder = transactionSessionHolder,
                    addBeneficiaryResultViewModelFactory = addBeneficiaryResultViewModelFactory
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
                val homeSessionHandler = TransactionSessionManager.currentHandler
                OtpScreen(
                    navController = navController,
                    notificationViewModelFactory = notificationViewModelFactory,
                    otpViewModelFactory = otpViewModelFactory,
                    backRoute = backRoute,
                    onOtpSuccess = {
                        homeSessionHandler.onOtpSuccess()
                    },
                    onDismiss = {
                        homeSessionHandler.onOtpDismiss()
                    }
                )
            }
        }
    }
}