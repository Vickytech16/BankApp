package com.example.bankapp.ui.components.navigators

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.bankapp.ui.screens.authscreens.SplashScreen

fun NavGraphBuilder.splashNavGraph(){
    navigation(
        startDestination = SPLASH_ROUTE,
        route = LOADING_ROUTE
    ){
        composable(SPLASH_ROUTE){
            SplashScreen()
        }
    }
}