package com.example.bankapp.ui.components.navigators

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.bankapp.di.viewmodelfactory.AccountCreationViewModelFactory
import com.example.bankapp.ui.screens.AccountCreationScreen
import com.example.bankapp.viewmodels.LoggedInSessionViewModel

fun NavGraphBuilder.accountNavGraph(
        accountCreationViewModelFactory: AccountCreationViewModelFactory,
        loggedInSessionViewModel: LoggedInSessionViewModel
    ){
        navigation(
            startDestination = ACCOUNT_CREATION_ROUTE,
            route = ACCOUNT_ROUTE,
        ){
            composable(ACCOUNT_CREATION_ROUTE){
                AccountCreationScreen(
                    accountCreationViewModelFactory = accountCreationViewModelFactory,
                    loggedInSessionViewModel = loggedInSessionViewModel
                )
            }
        }
    }
