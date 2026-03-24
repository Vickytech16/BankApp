package com.example.bankapp.temp

/*

package com.example.bankapp.ui.components.navigators

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.bankapp.di.ViewModelContainer
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
    userRepository: UserRepository
) {
    val navController = rememberNavController()

    val sessionViewModel: LoggedInSessionViewModel =
        viewModel(factory = viewModelContainer.loggedInSessionViewModelFactory)

//    LaunchedEffect(Unit) {
//        sessionViewModel.restoreSession()
//    }

    val sessionState by sessionViewModel.sessionState.collectAsState()

    val startDestination = when(sessionState) {

        is SessionState.Loading ->
            LOADING_ROUTE

        is SessionState.UnAuthenticated ->
            AUTH_ROUTE

        is SessionState.Authenticated.AccountNotRegistered ->
            ACCOUNT_ROUTE

        is SessionState.Authenticated.AccountRegistered ->
            MAIN_ROUTE
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    )  {
            authNavGraph(
                navController = navController,
                windowSizeClass = windowSizeClass,
                loginViewModelFactory = viewModelContainer.loginViewModelFactory,
                registerViewModelFactory = viewModelContainer.registerViewModelFactory,
                forgotPasswordViewModelFactory = viewModelContainer.forgotPasswordViewModelFactory,
                changePasswordViewModelFactory = viewModelContainer.changePasswordViewModelFactory,
                loggedInSessionViewModel = sessionViewModel,
                otpViewModelFactory = viewModelContainer.otpViewModelFactory,
                notificationViewModelFactory = viewModelContainer.notificationViewModelFactory,
            )

            homeNavGraph(
                navController = navController,
                windowSizeClass = windowSizeClass,
                sessionState = sessionState,
                transactionRepository = transactionRepository,
                accountRepository = accountRepository,
                otpViewModelFactory = viewModelContainer.otpViewModelFactory,
                notificationViewModelFactory = viewModelContainer.notificationViewModelFactory,
                logoutAction = {
                    sessionViewModel.logout()
                },
                beneficiaryRepository = beneficiaryRepository,
                userRepository = userRepository

            )

            splashNavGraph()

            accountNavGraph(
                accountCreationViewModelFactory = viewModelContainer.accountCreationViewModelFactory,
                navController = navController,
                windowSizeClass = windowSizeClass, loggedInSessionViewModel = sessionViewModel
            )
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
 */

/*
from github

package com.example.bankapp.ui.components.navigators

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.bankapp.di.ViewModelContainer
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
    transactionSessionHolder: TransactionSessionHolder,
    beneficiaryRepository: BeneficiaryRepository,
    userRepository: UserRepository
) {
    val navController = rememberNavController()

    val sessionViewModel: LoggedInSessionViewModel =
        viewModel(factory = viewModelContainer.loggedInSessionViewModelFactory)

    val sessionState = sessionViewModel.sessionState

    LaunchedEffect(Unit) {
        sessionViewModel.restoreSession()
    }

    val startDestination = when(sessionState) {

        is SessionState.Loading ->
            LOADING_ROUTE

        is SessionState.UnAuthenticated ->
            AUTH_ROUTE

        is SessionState.Authenticated.AccountNotRegistered ->
            ACCOUNT_ROUTE

        is SessionState.Authenticated.AccountRegistered ->
            MAIN_ROUTE
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    )  {
            authNavGraph(
                navController = navController,
                windowSizeClass = windowSizeClass,
                loginViewModelFactory = viewModelContainer.loginViewModelFactory,
                registerViewModelFactory = viewModelContainer.registerViewModelFactory,
                forgotPasswordViewModelFactory = viewModelContainer.forgotPasswordViewModelFactory,
                changePasswordViewModelFactory = viewModelContainer.changePasswordViewModelFactory,
                loggedInSessionViewModel = sessionViewModel,
                otpViewModelFactory = viewModelContainer.otpViewModelFactory,
                notificationViewModelFactory = viewModelContainer.notificationViewModelFactory,
                otpVerificationViewModelFactory = viewModelContainer.otpVerificationViewModelFactory
            )

            homeNavGraph(
                navController = navController,
                windowSizeClass = windowSizeClass,
                sessionState = sessionState,
                transactionRepository = transactionRepository,
                accountRepository = accountRepository,
                otpViewModelFactory = viewModelContainer.otpViewModelFactory,
                notificationViewModelFactory = viewModelContainer.notificationViewModelFactory,
                logoutAction = {
                    sessionViewModel.logout()
                },
                transactionSessionHolder = transactionSessionHolder,
                beneficiaryRepository = beneficiaryRepository,
                userRepository = userRepository

            )
            splashNavGraph()

            accountNavGraph(
                accountCreationViewModelFactory = viewModelContainer.accountCreationViewModelFactory,
                navController = navController,
                windowSizeClass = windowSizeClass, loggedInSessionViewModel = sessionViewModel
            )
    }

}
 */

/*

import android.util.Log
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.bankapp.di.ViewModelContainer
import com.example.bankapp.di.providers.SessionStateProvider
import com.example.bankapp.entities.SessionState
import com.example.bankapp.repositories.AccountRepository
import com.example.bankapp.repositories.BeneficiaryRepository
import com.example.bankapp.repositories.TransactionRepository
import com.example.bankapp.repositories.UserRepository
import com.example.bankapp.ui.components.navigators.ACCOUNT_ROUTE
import com.example.bankapp.ui.components.navigators.AUTH_ROUTE
import com.example.bankapp.ui.components.navigators.LOADING_ROUTE
import com.example.bankapp.ui.components.navigators.MAIN_ROUTE
import com.example.bankapp.ui.components.navigators.authNavGraph
import com.example.bankapp.ui.components.navigators.homeNavGraph
import com.example.bankapp.ui.components.navigators.splashNavGraph


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
        if (sessionState is SessionState.Loading) {
            sessionStateProvider.restoreSession()
            userRepository.ping()
        }

    }

    val startDestination = rememberSaveable(sessionState) {
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
        startDestination = startDestination,
    ) {
        splashNavGraph()

        authNavGraph(
            navController = navController,
            windowSizeClass = windowSizeClass,
            loginViewModelFactory = viewModelContainer.loginViewModelFactory,
            registerViewModelFactory = viewModelContainer.registerViewModelFactory,
            forgotPasswordViewModelFactory = viewModelContainer.forgotPasswordViewModelFactory,
            changePasswordViewModelFactory = viewModelContainer.changePasswordViewModelFactory,
            otpViewModelFactory = viewModelContainer.otpViewModelFactory,
            notificationViewModelFactory = viewModelContainer.notificationViewModelFactory,
            restoreSession = { sessionStateProvider.restoreSession() }
        )

        homeNavGraph(
            navController = navController,
            windowSizeClass = windowSizeClass,
            sessionState = sessionState,
            transactionRepository = transactionRepository,
            accountRepository = accountRepository,
            otpViewModelFactory = viewModelContainer.otpViewModelFactory,
            notificationViewModelFactory = viewModelContainer.notificationViewModelFactory,
            beneficiaryRepository = beneficiaryRepository,
            userRepository = userRepository,
            logoutAction = { sessionStateProvider.logout() },
            filterViewModelFactory = viewModelContainer.filterViewModelFactory,
            transactionDetailsViewModelFactory = viewModelContainer.transactionDetailsViewModelFactory
        )

        accountNavGraph(
            accountCreationViewModelFactory = viewModelContainer.accountCreationViewModelFactory,
            navController = navController,
            windowSizeClass = windowSizeClass,
            restoreSession = { sessionStateProvider.restoreSession() }
        )
    }
}

    /*

    catch (e: Exception){
            Log.e("NavHost", "Invalid navigation graph: ${e.message}")
            sessionStateProvider.restoreSession()
        }
     */


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


 */