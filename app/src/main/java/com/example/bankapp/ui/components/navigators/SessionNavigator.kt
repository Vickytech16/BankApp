package com.example.bankapp.ui.components.navigators

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.example.bankapp.entities.SessionState

@Composable
fun SessionNavigator(navController: NavController, sessionState: SessionState){

    LaunchedEffect(sessionState) {

        val currentRoute = navController.currentDestination?.route

        when(sessionState) {
            SessionState.Loading -> {
                if(currentRoute!=LOADING_ROUTE) {
                    navController.navigate(LOADING_ROUTE) {
                        popUpTo(ROOT_ROUTE)
                    }
                }
            }

            SessionState.UnAuthenticated -> {
                if(currentRoute!=AUTH_ROUTE){
                navController.navigate(AUTH_ROUTE) {
                    popUpTo(ROOT_ROUTE)
                    }
                }
            }

            is SessionState.Authenticated.AccountNotRegistered -> {
                if(currentRoute!=ACCOUNT_CREATION_ROUTE) {
                    navController.navigate(ACCOUNT_CREATION_ROUTE) {
                        popUpTo(ROOT_ROUTE)
                    }
                }
            }

            is SessionState.Authenticated.AccountRegistered -> {
                if(currentRoute!=MAIN_ROUTE){
                    navController.navigate(MAIN_ROUTE) {
                        popUpTo(ROOT_ROUTE)
                    }
                }
            }


        }
    }
}