package com.example.bankapp.ui.components.navigators

import AuthorizationViewModel
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.bankapp.di.ViewModelContainer
import com.example.bankapp.di.viewmodelfactory.AccountCreationViewModelFactory
import com.example.bankapp.entities.SessionState
import com.example.bankapp.repositories.AccountRepository
import com.example.bankapp.repositories.BeneficiaryRepository
import com.example.bankapp.repositories.CountryRepository
import com.example.bankapp.repositories.CurrencyExchangeRepository
import com.example.bankapp.repositories.TransactionRepository
import com.example.bankapp.repositories.UserRepository
import com.example.bankapp.services.TransactionExportService
import com.example.bankapp.entities.ChangePasswordState
import com.example.bankapp.viewmodels.SessionViewModel
import com.example.bankapp.viewmodels.ThemeViewModel

@Composable
fun AppNavHost(
    windowSizeClass: WindowSizeClass,
    viewModelContainer: ViewModelContainer,
    transactionRepository: TransactionRepository,
    accountRepository: AccountRepository,
    beneficiaryRepository: BeneficiaryRepository,
    userRepository: UserRepository,
    themeViewModel: ThemeViewModel,
    currencyExchangeRepository: CurrencyExchangeRepository,
    countryRepository: CountryRepository,
    changePasswordState: ChangePasswordState,
    transactionExportService: TransactionExportService
) {
    val navController = rememberNavController()

    val sessionViewModel: SessionViewModel =
        viewModel(factory = viewModelContainer.loggedInSessionViewModelFactory)

    val authorizationViewModel: AuthorizationViewModel = viewModel(factory = viewModelContainer.authorizationViewModelFactory)

    LaunchedEffect(sessionViewModel.sessionState) {
        sessionViewModel.restoreSession()
    }

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
            loginViewModelFactory = viewModelContainer.loginViewModelFactory,
            registerViewModelFactory = viewModelContainer.registerViewModelFactory,
            forgotPasswordViewModelFactory = viewModelContainer.forgotPasswordViewModelFactory,
            changePasswordViewModelFactory = viewModelContainer.changePasswordViewModelFactory,
            otpViewModelFactory = viewModelContainer.otpViewModelFactory,
            recoveryKeyViewModelFactory = viewModelContainer.recoveryKeyViewModelFactory ,
            restoreSession = { sessionViewModel.restoreSession() }
        )

        homeNavGraph(
            navController = navController,
            windowSizeClass = windowSizeClass,
            sessionState = sessionState,
            transactionRepository = transactionRepository,
            accountRepository = accountRepository,
            otpViewModelFactory = viewModelContainer.otpViewModelFactory,
            beneficiaryRepository = beneficiaryRepository,
            userRepository = userRepository,
            filterViewModelFactory = viewModelContainer.filterViewModelFactory,
            themeViewModel = themeViewModel,
            authorizationViewModel = authorizationViewModel,
            currencyExchangeRepository = currencyExchangeRepository,
            sessionViewModel = sessionViewModel,
            changePasswordViewModelFactory = viewModelContainer.changePasswordViewModelFactory,
            recoveryKeyViewModelFactory = viewModelContainer.recoveryKeyViewModelFactory,
            countryRepository = countryRepository,
            changePasswordState = changePasswordState,
            transactionExportService = transactionExportService
        )

        splashNavGraph()

        accountNavGraph(
            sessionViewModel = sessionViewModel,
            transactionRepository = transactionRepository,
            accountRepository = accountRepository,
            sessionState = sessionState
        )
    }
}