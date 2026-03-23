package com.example.bankapp.ui.components.navigators

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.bankapp.di.viewmodelfactory.AccountCreationViewModelFactory
import com.example.bankapp.ui.screens.AccountCreationScreen
import com.example.bankapp.viewmodels.LoggedInSessionViewModel
    fun NavGraphBuilder.accountNavGraph(
        accountCreationViewModelFactory: AccountCreationViewModelFactory,
        navController: NavController,
        windowSizeClass: WindowSizeClass,
        loggedInSessionViewModel: LoggedInSessionViewModel
    ){
        navigation(
            startDestination = ACCOUNT_CREATION_ROUTE,
            route = ACCOUNT_ROUTE,
        ){
            composable(ACCOUNT_CREATION_ROUTE){
                AccountCreationScreen(accountCreationViewModelFactory = accountCreationViewModelFactory, navController =  navController, windowSizeClass = windowSizeClass, restoreSession = { loggedInSessionViewModel.restoreSession() })
            }
        }
    }
