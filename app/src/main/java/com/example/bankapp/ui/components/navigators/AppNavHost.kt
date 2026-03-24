package com.example.bankapp.ui.components.navigators

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.bankapp.di.ViewModelContainer
import com.example.bankapp.di.providers.SessionStateProvider
import com.example.bankapp.entities.SessionState
import com.example.bankapp.repositories.AccountRepository
import com.example.bankapp.repositories.BeneficiaryRepository
import com.example.bankapp.repositories.TransactionRepository
import com.example.bankapp.repositories.UserRepository
import com.example.bankapp.usecases.TransactionSessionHolder
import com.example.bankapp.viewmodels.LoggedInSessionViewModel

@Composable
fun AppNavHost(
    windowSizeClass: WindowSizeClass,
    viewModelContainer: ViewModelContainer,
    transactionRepository: TransactionRepository,
    accountRepository: AccountRepository,
    beneficiaryRepository: BeneficiaryRepository,
    userRepository: UserRepository,
    sessionStateProvider: SessionStateProvider
) {
    val navController = rememberNavController()
    
    val sessionState by sessionStateProvider.sessionState.collectAsState()

    LaunchedEffect(Unit) {
            sessionStateProvider.restoreSession()
    }

    val startDestination = {
        when (sessionState) {
            is SessionState.Loading ->
                LOADING_ROUTE

            is SessionState.UnAuthenticated ->
                AUTH_ROUTE

            is SessionState.Authenticated.AccountNotRegistered ->
                ACCOUNT_ROUTE

            is SessionState.Authenticated.AccountRegistered ->
                MAIN_ROUTE
        }
    }


    NavHost(
        navController = navController,
        startDestination = LOADING_ROUTE
    )  {
        authNavGraph(
            navController = navController,
            windowSizeClass = windowSizeClass,
            loginViewModelFactory = viewModelContainer.loginViewModelFactory,
            registerViewModelFactory = viewModelContainer.registerViewModelFactory,
            forgotPasswordViewModelFactory = viewModelContainer.forgotPasswordViewModelFactory,
            changePasswordViewModelFactory = viewModelContainer.changePasswordViewModelFactory,
            otpViewModelFactory = viewModelContainer.otpViewModelFactory,
            notificationViewModelFactory = viewModelContainer.notificationViewModelFactory,
            restoreSession = { sessionStateProvider.restoreSession() },
        )

        homeNavGraph(
            navController = navController,
            windowSizeClass = windowSizeClass,
            sessionState = sessionState as SessionState.Authenticated.AccountRegistered,
            transactionRepository = transactionRepository,
            accountRepository = accountRepository,
            otpViewModelFactory = viewModelContainer.otpViewModelFactory,
            notificationViewModelFactory = viewModelContainer.notificationViewModelFactory,
            logoutAction = { sessionStateProvider.restoreSession() },
            beneficiaryRepository = beneficiaryRepository,
            userRepository = userRepository,
            filterViewModelFactory = viewModelContainer.filterViewModelFactory,
            transactionDetailsViewModelFactory = viewModelContainer.transactionDetailsViewModelFactory
        )

        splashNavGraph()

        accountNavGraph(
            accountCreationViewModelFactory = viewModelContainer.accountCreationViewModelFactory,
            navController = navController,
            windowSizeClass = windowSizeClass,
            restoreSession = { sessionStateProvider.restoreSession() },
        )
    }

    LaunchedEffect(sessionState) {
        when(sessionState) {

            is SessionState.Loading -> {
                // Do nothing
            }

            is SessionState.UnAuthenticated -> {
                navController.navigate(AUTH_ROUTE) {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }

            is SessionState.Authenticated.AccountNotRegistered -> {
                navController.navigate(ACCOUNT_ROUTE) {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }

            is SessionState.Authenticated.AccountRegistered -> {
                navController.navigate(MAIN_ROUTE) {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

//    LaunchedEffect(startDestination) {
//        if (sessionState !is SessionState.Loading &&
//            navController.currentDestination?.route != startDestination) {
//            navController.navigate(startDestination) {
//                popUpTo(navController.graph.startDestinationId) {
//                    saveState = false
//                }
//                launchSingleTop = true
//                restoreState = false
//            }
//        }
//    }

}