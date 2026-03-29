package com.example.bankapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bankapp.di.AppContainer
import com.example.bankapp.di.ViewModelContainer
import com.example.bankapp.ui.components.navigators.AppNavHost
import com.example.bankapp.ui.theme.BankAppTheme
import com.example.bankapp.ui.theme.DeviceSpec
import com.example.bankapp.ui.theme.DeviceSpecProvider
import com.example.bankapp.ui.theme.DeviceSpecProviderTemp
import com.example.bankapp.ui.theme.LocalDeviceSpec
import com.example.bankapp.viewmodels.ThemeType
import com.example.bankapp.viewmodels.ThemeViewModel

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContainer: AppContainer = (application as BankApp).appContainer
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val viewModelContainer: ViewModelContainer = appContainer.viewModelContainer


        setContent {
            val themeViewModel: ThemeViewModel = viewModel(factory = viewModelContainer.themeViewModelFactory)
            val currentTheme = themeViewModel.currentTheme.collectAsState()

            val useDarkTheme = when (currentTheme.value) {
                ThemeType.DARK -> true
                ThemeType.LIGHT -> false
                ThemeType.SYSTEM_DEFAULT -> isSystemInDarkTheme()
            }

            // These two lines are the "engine" that detects rotation
            val configuration = LocalConfiguration.current
            val windowSizeClass = calculateWindowSizeClass(this)

            // This recalculates ONLY when rotation or window size changes
            val deviceSpec = remember(configuration, windowSizeClass) {
                DeviceSpecProviderTemp.getCurrentDeviceSpec(windowSizeClass, configuration)
            }

            // Provide the spec to the entire hierarchy
            CompositionLocalProvider(LocalDeviceSpec provides deviceSpec) {
                BankAppTheme(darkTheme = useDarkTheme) {
                    AppNavHost(
                        windowSizeClass = windowSizeClass,
                        viewModelContainer = viewModelContainer,
                        transactionRepository = appContainer.transactionRepository,
                        accountRepository = appContainer.accountRepository,
                        beneficiaryRepository = appContainer.beneficiaryRepository,
                        userRepository = appContainer.userRepository,
                        themeViewModel = themeViewModel
                    )
                }
            }
        }
    }
}

