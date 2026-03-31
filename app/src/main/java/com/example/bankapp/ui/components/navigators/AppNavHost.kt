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
import com.example.bankapp.entities.SessionState
import com.example.bankapp.repositories.AccountRepository
import com.example.bankapp.repositories.BeneficiaryRepository
import com.example.bankapp.repositories.CountryRepository
import com.example.bankapp.repositories.CurrencyExchangeRepository
import com.example.bankapp.repositories.TransactionRepository
import com.example.bankapp.repositories.UserRepository
import com.example.bankapp.services.TransactionExportService
import com.example.bankapp.usecases.ChangePasswordUseCase
import com.example.bankapp.viewmodels.LoggedInSessionViewModel
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
    changePasswordUseCase: ChangePasswordUseCase,
    transactionExportService: TransactionExportService
) {
    val navController = rememberNavController()

    val sessionViewModel: LoggedInSessionViewModel =
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
            windowSizeClass = windowSizeClass,
            loginViewModelFactory = viewModelContainer.loginViewModelFactory,
            registerViewModelFactory = viewModelContainer.registerViewModelFactory,
            forgotPasswordViewModelFactory = viewModelContainer.forgotPasswordViewModelFactory,
            changePasswordViewModelFactory = viewModelContainer.changePasswordViewModelFactory,
            otpViewModelFactory = viewModelContainer.otpViewModelFactory,
            notificationViewModelFactory = viewModelContainer.notificationViewModelFactory,
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
            notificationViewModelFactory = viewModelContainer.notificationViewModelFactory,
            beneficiaryRepository = beneficiaryRepository,
            userRepository = userRepository,
            filterViewModelFactory = viewModelContainer.filterViewModelFactory,
            transactionDetailsViewModelFactory = viewModelContainer.transactionDetailsViewModelFactory,
            themeViewModel = themeViewModel,
            authorizationViewModel = authorizationViewModel,
            currencyExchangeRepository = currencyExchangeRepository,
            sessionViewModel = sessionViewModel,
            forgotPasswordViewModelFactory = viewModelContainer.forgotPasswordViewModelFactory,
            changePasswordViewModelFactory = viewModelContainer.changePasswordViewModelFactory,
            recoveryKeyViewModelFactory = viewModelContainer.recoveryKeyViewModelFactory,
            countryRepository = countryRepository,
            changePasswordUseCase = changePasswordUseCase,
            transactionExportService = transactionExportService
        )

        splashNavGraph()

        accountNavGraph(
            accountCreationViewModelFactory = viewModelContainer.accountCreationViewModelFactory,
            loggedInSessionViewModel = sessionViewModel
        )
    }

}