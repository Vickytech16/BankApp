package com.example.bankapp.ui.components.navigators

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.bankapp.di.providers.SessionStateProvider
import com.example.bankapp.di.viewmodelfactory.AccountCreationViewModelFactory
import com.example.bankapp.ui.screens.AccountCreationScreen

    fun NavGraphBuilder.accountNavGraph(
        accountCreationViewModelFactory: AccountCreationViewModelFactory,
        windowSizeClass: WindowSizeClass,
        restoreSession: () -> Unit
    ){
        navigation(
            startDestination = ACCOUNT_CREATION_ROUTE,
            route = ACCOUNT_ROUTE,
        ){
            composable(ACCOUNT_CREATION_ROUTE){
                AccountCreationScreen(
                    accountCreationViewModelFactory = accountCreationViewModelFactory,
                    windowSizeClass = windowSizeClass,
                    restoreSession = restoreSession
                )
            }
        }
    }
