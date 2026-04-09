package com.example.bankapp.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.bankapp.R
import com.example.bankapp.entities.dbtables.User
import com.example.bankapp.ui.components.appbar.HomeAppBar
import com.example.bankapp.ui.components.appbar.RegularAppBar
import com.example.bankapp.ui.components.bottomnavbar.BottomNavigationBar
import com.example.bankapp.ui.components.navigators.HOME_ROUTE
import com.example.bankapp.ui.components.navigators.PAY_ROUTE
import com.example.bankapp.ui.components.navigators.PROFILE_ROUTE
import com.example.bankapp.ui.theme.DeviceSpec

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenShell(
    navController: NavController,
    user: User,
    drawerState: DrawerState,
    deviceSpec: DeviceSpec,
    content: @Composable (PaddingValues, TopAppBarScrollBehavior) -> Unit
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route ?: HOME_ROUTE
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val titles = mapOf(
        HOME_ROUTE to "",
        PAY_ROUTE to stringResource(R.string.pay_screen_title),
        PROFILE_ROUTE to stringResource(R.string.profile_screen_title)
    )

    Scaffold(
        topBar = {
            if (currentRoute == HOME_ROUTE) {
                HomeAppBar(
                    username = user.userName,
                    drawerState = drawerState,
                    scrollBehavior = scrollBehavior,
                    deviceSpec = deviceSpec
                )
            } else {
                RegularAppBar(
                    title = titles[currentRoute] ?: "",
                    scrollBehavior = scrollBehavior
                )
            }
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = currentRoute,
                deviceSpec = deviceSpec,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(HOME_ROUTE) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        // This is where the sliding content (NavHost) will live
        content(innerPadding, scrollBehavior)
    }
}