package com.example.bankapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.More
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.bankapp.R
import com.example.bankapp.ui.components.appbar.Appbar
import com.example.bankapp.ui.components.bottomnavbar.BottomNavigationBar
import com.example.bankapp.ui.components.buttons.QuickActionsButton
import com.example.bankapp.ui.components.navigators.HOME_ROUTE
import com.example.bankapp.ui.components.navigators.PAY_ROUTE
import com.example.bankapp.ui.components.XLSpacer
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.ui.theme.screenPadding

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayScreen(
    windowSizeClass: WindowSizeClass,
    navController: NavController,
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route ?: PAY_ROUTE

    val scrollState = rememberScrollState()

    val contentColumnWidth = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 1f
        WindowWidthSizeClass.Medium -> 0.75f
        WindowWidthSizeClass.Expanded -> 0.6f
        else -> 0.9f
    }

    Scaffold(
        topBar = {
            Appbar(stringResource(R.string.pay_screen_title), { navController.navigate(HOME_ROUTE) }, null)
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = currentRoute,
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
        contentWindowInsets = WindowInsets.systemBars
    ) { contentPadding ->

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(contentPadding)
                .padding(screenPadding)
                .padding(top = AppSpacing.md),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier.fillMaxWidth(contentColumnWidth)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        text = stringResource(R.string.pay_section_label),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = AppSpacing.sm)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        QuickActionsButton(
                            onClickAction = { navController.navigate(HOME_ROUTE) },
                            icon = Icons.Outlined.AccountBalance,
                            label = stringResource(R.string.pay_to_friend),
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionsButton(
                            onClickAction = { navController.navigate(HOME_ROUTE) },
                            icon = Icons.Outlined.SwapHoriz,
                            label = stringResource(R.string.pay_anyone),
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionsButton(
                            onClickAction = { navController.navigate(HOME_ROUTE) },
                            icon = Icons.Outlined.AccountBalanceWallet,
                            label = stringResource(R.string.deposit_button),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    XLSpacer()

                    Text(
                        text = stringResource(R.string.beneficiary_section_label),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = AppSpacing.sm)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        QuickActionsButton(
                            onClickAction = { navController.navigate(HOME_ROUTE) },
                            icon = Icons.Outlined.AccountBalance,
                            label = stringResource(R.string.add_beneficiary),
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionsButton(
                            onClickAction = { navController.navigate(HOME_ROUTE) },
                            icon = Icons.AutoMirrored.Outlined.More,
                            label = stringResource(R.string.manage_beneficiary),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    XLSpacer()

                    Text(
                        text = stringResource(R.string.schedule_pay_section_label),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = AppSpacing.sm)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        QuickActionsButton(
                            onClickAction = { navController.navigate(HOME_ROUTE) },
                            icon = Icons.Outlined.AccountBalance,
                            label = stringResource(R.string.schedule_pay),
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionsButton(
                            onClickAction = { navController.navigate(HOME_ROUTE) },
                            icon = Icons.AutoMirrored.Outlined.More,
                            label = stringResource(R.string.manage_schedule),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    XLSpacer()

                    Text(
                        text = stringResource(R.string.scheduled_payments_label),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = AppSpacing.sm)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = dimensionResource(R.dimen.scheduled_pay_list_height))
                    ) {

                    }
                }
            }
        }
    }
}