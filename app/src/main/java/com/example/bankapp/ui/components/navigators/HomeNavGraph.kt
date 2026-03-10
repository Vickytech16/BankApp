package com.example.bankapp.ui.components.navigators

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.bankapp.di.viewmodelfactory.CashTransferViewModelFactory
import com.example.bankapp.di.viewmodelfactory.HomeViewModelFactory
import com.example.bankapp.di.viewmodelfactory.TransactionsViewModelFactory
import com.example.bankapp.entities.SessionState
import com.example.bankapp.repositories.AccountRepository
import com.example.bankapp.repositories.TransactionRepository
import com.example.bankapp.ui.screens.CashTransferScreen
import com.example.bankapp.ui.screens.HomeScreen
import com.example.bankapp.ui.screens.TransactionsScreen
import com.example.bankapp.viewmodels.TransactionsViewModel



@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.homeNavGraph(
    navController: NavController,
    windowSizeClass: WindowSizeClass,
    sessionState: SessionState,
    transactionRepository: TransactionRepository,
    accountRepository: AccountRepository,
    logoutAction: ()->Unit
){
    navigation(
        startDestination = HOME_ROUTE,
        route = MAIN_ROUTE
    ){
        if(sessionState is SessionState.Authenticated.AccountRegistered) {

            val transactionsViewModelFactory =
                TransactionsViewModelFactory(sessionState, transactionRepository)
            val homeViewModelFactory = HomeViewModelFactory(sessionState, accountRepository)
            val cashTransferViewModelFactory =
                CashTransferViewModelFactory(sessionState, transactionRepository)


            composable(HOME_ROUTE) {
                val transactionsViewModel: TransactionsViewModel = viewModel(factory = transactionsViewModelFactory)
                HomeScreen(
                    windowSizeClass = windowSizeClass,
                    homeViewModelFactory = homeViewModelFactory,
                    navController = navController,
                    logoutAction = logoutAction,
                    transactionsViewModel = transactionsViewModel
                )
            }
            composable(CASH_TRANSFER_ROUTE) {
                CashTransferScreen(
                    windowSizeClass = windowSizeClass,
                    cashTransferViewModelFactory = cashTransferViewModelFactory,
                    navController = navController
                )
            }

            composable(TRANSACTIONS_LOG_ROUTE){
                val transactionsViewModel: TransactionsViewModel = viewModel(factory = transactionsViewModelFactory)
                TransactionsScreen(transactionsViewModel = transactionsViewModel, navController = navController)
            }
        }
    }
}