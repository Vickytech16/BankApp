package com.example.bankapp.ui.components.navigators

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.bankapp.di.ViewModelContainer
import com.example.bankapp.entities.SessionState
import com.example.bankapp.repositories.AccountRepository
import com.example.bankapp.repositories.TransactionRepository
import com.example.bankapp.viewmodels.LoggedInSessionViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost(
    windowSizeClass: WindowSizeClass,
    viewModelContainer: ViewModelContainer,
    transactionRepository: TransactionRepository,
    accountRepository: AccountRepository
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
                notificationViewModelFactory = viewModelContainer.notificationViewModelFactory
            )

            homeNavGraph(
                navController = navController,
                windowSizeClass = windowSizeClass,
                sessionState = sessionState,
                transactionRepository = transactionRepository,
                accountRepository = accountRepository,
                logoutAction = { sessionViewModel.logout() }
            )
            splashNavGraph()

            accountNavGraph(accountCreationViewModelFactory = viewModelContainer.accountCreationViewModelFactory,
                navController = navController,
                windowSizeClass = windowSizeClass, loggedInSessionViewModel = sessionViewModel)

    }

}