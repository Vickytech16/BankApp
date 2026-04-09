package com.example.bankapp

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bankapp.core.WorkManagerInitializer
import com.example.bankapp.di.AppContainer
import com.example.bankapp.di.ViewModelContainer
import com.example.bankapp.services.TransactionExportService
import com.example.bankapp.ui.components.navigators.AppNavHost
import com.example.bankapp.ui.theme.BankAppTheme
import com.example.bankapp.di.providers.DeviceSpecProvider
import com.example.bankapp.ui.theme.LocalDeviceSpec
import com.example.bankapp.entities.types.ThemeType
import com.example.bankapp.ui.components.authenticationItems.RootAuthWrapper
import com.example.bankapp.ui.components.navigators.NavStarter
import com.example.bankapp.viewmodels.SessionViewModel
import com.example.bankapp.viewmodels.ThemeViewModel
import kotlinx.coroutines.launch

class MainActivity : FragmentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContainer: AppContainer = (application as BankApp).appContainer
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val viewModelContainer: ViewModelContainer = appContainer.viewModelContainer

        WorkManagerInitializer.scheduleCurrencyExchangeSync(this)
        WorkManagerInitializer.scheduleDailyInterest(this)

        lifecycleScope.launch {
            appContainer.currencyExchangeRepository.getLatestRates()
        }

        val transactionExportService = TransactionExportService(this)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        setContent {
            val themeViewModel: ThemeViewModel = viewModel(factory = viewModelContainer.themeViewModelFactory)
            val sessionViewModel: SessionViewModel = viewModel(factory = viewModelContainer.loggedInSessionViewModelFactory)
            val currentTheme = themeViewModel.currentTheme.collectAsState()

            val useDarkTheme =  when (currentTheme.value) {
                ThemeType.DARK -> true
                ThemeType.LIGHT -> false
                ThemeType.SYSTEM_DEFAULT -> isSystemInDarkTheme()
            }

            val configuration = LocalConfiguration.current
            val windowSizeClass = calculateWindowSizeClass(this)

            val deviceSpec = remember(configuration, windowSizeClass) {
                DeviceSpecProvider.getCurrentDeviceSpec(configuration)
            }

            CompositionLocalProvider(LocalDeviceSpec provides deviceSpec) {
                BankAppTheme(darkTheme = useDarkTheme) {
                    RootAuthWrapper(this) {
                       NavStarter(sessionViewModel = sessionViewModel) {
                            AppNavHost(
                                windowSizeClass = windowSizeClass,
                                viewModelContainer = viewModelContainer,
                                transactionRepository = appContainer.transactionRepository,
                                accountRepository = appContainer.accountRepository,
                                beneficiaryRepository = appContainer.beneficiaryRepository,
                                userRepository = appContainer.userRepository,
                                themeViewModel = themeViewModel,
                                currencyExchangeRepository = appContainer.currencyExchangeRepository,
                                countryRepository = appContainer.countryRepository,
                                changePasswordState = appContainer.changePasswordState,
                                transactionExportService = transactionExportService,
                                sessionViewModel = sessionViewModel
                            )
                       }
                    }
                }
            }
        }
    }
}

