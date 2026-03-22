package com.example.bankapp.ui.components.bottomnavbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.example.bankapp.R
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.ui.theme.DeviceSpec

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    deviceSpec: DeviceSpec
) {
    val navbarHeight = deviceSpec.navBarItemheight
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
                start = AppSpacing.sm,
                end = AppSpacing.sm
            )
            .height(dimensionResource(navbarHeight))
            .padding(vertical = AppSpacing.xs),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem.entries.forEach { item ->
            val isSelected = currentRoute == item.route

            BottomNavItemButton(
                item = item,
                isSelected = isSelected,
                onClick = {
                    onNavigate(item.route)
                },
                deviceSpec = deviceSpec
            )
        }
    }
}
