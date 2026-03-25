package com.example.bankapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.collectAsState
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bankapp.di.AppContainer
import com.example.bankapp.di.ViewModelContainer
import com.example.bankapp.ui.components.navigators.AppNavHost
import com.example.bankapp.ui.theme.BankAppTheme
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
            val windowSizeClass = calculateWindowSizeClass(this)
            val themeViewModel: ThemeViewModel = viewModel(factory = viewModelContainer.themeViewModelFactory)

            val currentTheme = themeViewModel.currentTheme.collectAsState()

            val useDarkTheme = when (currentTheme.value) {
                ThemeType.DARK -> true
                ThemeType.LIGHT -> false
                ThemeType.SYSTEM_DEFAULT -> isSystemInDarkTheme()
            }

            BankAppTheme (darkTheme = useDarkTheme){
                AppNavHost(
                    windowSizeClass = windowSizeClass,
                    viewModelContainer,
                    appContainer.transactionRepository,
                    appContainer.accountRepository,
                    appContainer.beneficiaryRepository,
                    appContainer.userRepository,
                    themeViewModel = themeViewModel
                )
            }
        }
    }
}

