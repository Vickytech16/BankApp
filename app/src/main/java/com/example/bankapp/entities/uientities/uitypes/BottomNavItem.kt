package com.example.bankapp.entities.uientities.uitypes

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.Index
import com.example.bankapp.R
import com.example.bankapp.ui.components.navigators.HOME_ROUTE
import com.example.bankapp.ui.components.navigators.PAY_ROUTE
import com.example.bankapp.ui.components.navigators.PROFILE_ROUTE

enum class BottomNavItem(
    val label: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String,
    val index: Int
) {
    HOME(
        label = R.string.bottom_nav_home,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        route = HOME_ROUTE,
        index = 0
    ),
    PAY(
        label = R.string.bottom_nav_pay,
        selectedIcon = Icons.Filled.SwapHoriz,
        unselectedIcon = Icons.Outlined.SwapHoriz,
        route = PAY_ROUTE,
        index = 1
    ),
    PROFILE(
        label = R.string.bottom_nav_profile,
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
        route = PROFILE_ROUTE,
        index = 2
    )
}