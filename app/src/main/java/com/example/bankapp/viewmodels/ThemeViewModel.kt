package com.example.bankapp.viewmodels

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.R
import com.example.bankapp.usecases.SharedPreferenceHelper
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class ThemeViewModel(
    private val sharedPreferenceHelper: SharedPreferenceHelper
) : ViewModel() {

    val currentTheme: StateFlow<ThemeType> = sharedPreferenceHelper.getSavedTheme().map { themeString ->
        ThemeType.fromStringToThemeType(themeString)
    }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeType.SYSTEM_DEFAULT
        )

    var showThemeDialog by mutableStateOf(false)
        private set

    fun onThemeDialogChange(newValue: Boolean) {
        showThemeDialog = newValue
    }

    fun onThemeSelected(theme: ThemeType) {
        viewModelScope.launch {
            sharedPreferenceHelper.saveTheme(theme.name)
            showThemeDialog = false
        }
    }
}

enum class ThemeType{
    LIGHT,
    DARK,
    SYSTEM_DEFAULT;

    @Composable
    fun getDisplayName(): String = when (this) {
        LIGHT -> stringResource(R.string.light_theme_label)
        DARK -> stringResource(R.string.dark_theme_label)
        SYSTEM_DEFAULT -> stringResource(R.string.system_default_theme_label)
    }

    companion object {
        fun fromStringToThemeType(value: String): ThemeType {
            return try {
                valueOf(value)
            } catch (e: IllegalArgumentException) {
                SYSTEM_DEFAULT
            }
        }
    }
}
