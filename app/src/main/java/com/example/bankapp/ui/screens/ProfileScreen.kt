package com.example.bankapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.bankapp.ui.components.appbar.Appbar
import com.example.bankapp.ui.components.bottomnavbar.BottomNavigationBar
import com.example.bankapp.ui.components.navigators.HOME_ROUTE
import com.example.bankapp.ui.components.navigators.PAY_ROUTE
import com.example.bankapp.ui.components.navigators.PROFILE_ROUTE
import com.example.bankapp.ui.theme.DeviceSpecProvider

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    windowSizeClass: WindowSizeClass)
{
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route ?: PROFILE_ROUTE

    val deviceSpec = DeviceSpecProvider.getCurrentDeviceSpec(windowSizeClass)

    Scaffold(
        topBar = {
            Appbar("Profile screen", {navController.navigate(HOME_ROUTE)}, null)
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = currentRoute,
                onNavigate = {
                        route ->
                    navController.navigate(route) {
                        popUpTo(HOME_ROUTE) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                deviceSpec = deviceSpec
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ){

    }
}