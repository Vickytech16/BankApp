package com.example.bankapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.core.view.WindowCompat
import com.example.bankapp.di.AppContainer
import com.example.bankapp.di.ViewModelContainer
import com.example.bankapp.ui.components.navigators.AppNavHost
import com.example.bankapp.ui.theme.BankAppTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appContainer: AppContainer = (application as BankApp).appContainer
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            BankAppTheme {
                val viewModelContainer: ViewModelContainer = appContainer.viewModelContainer
                AppNavHost(
                    windowSizeClass = windowSizeClass,
                    viewModelContainer,
                    appContainer.transactionRepository,
                    appContainer.accountRepository,
                    //appContainer.transactionHolder,
                    appContainer.beneficiaryRepository,
                    appContainer.userRepository
                )
            }
        }
    }
}

