package com.example.bankapp.ui.components.navigators

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.bankapp.di.viewmodelfactory.AccountCreationViewModelFactory
import com.example.bankapp.entities.SessionState
import com.example.bankapp.repositories.AccountRepository
import com.example.bankapp.repositories.TransactionRepository
import com.example.bankapp.ui.screens.AccountCreationScreen
import com.example.bankapp.viewmodels.SessionViewModel

fun NavGraphBuilder.accountNavGraph(
    accountRepository: AccountRepository,
    transactionRepository: TransactionRepository,
    sessionViewModel: SessionViewModel,
    sessionState: SessionState
    ){

    if(sessionState is SessionState.Authenticated.AccountNotRegistered) {
        val accountCreationViewModelFactory: AccountCreationViewModelFactory =
            AccountCreationViewModelFactory(
                accountRepository = accountRepository,
                transactionRepository = transactionRepository,
                sessionViewModel = sessionViewModel,
                sessionState = sessionState
                )
        navigation(
            startDestination = ACCOUNT_CREATION_ROUTE,
            route = ACCOUNT_ROUTE,
        ) {
            composable(ACCOUNT_CREATION_ROUTE) {
                AccountCreationScreen(
                    accountCreationViewModelFactory = accountCreationViewModelFactory,
                    sessionViewModel = sessionViewModel
                )
            }
        }
    }
    }
