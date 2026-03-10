package com.example.bankapp.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.bankapp.ui.components.navigators.LOGIN_ROUTE

@Composable
fun BackButtonHandler(navController: NavController, route: String)
{
    BackHandler {
        navController.navigate(LOGIN_ROUTE) {
            popUpTo(LOGIN_ROUTE) {
                inclusive = true
            }
        }
    }
}