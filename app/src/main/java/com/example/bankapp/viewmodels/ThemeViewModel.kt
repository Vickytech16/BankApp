package com.example.bankapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.entities.types.ThemeType
import com.example.bankapp.utilities.SharedPreferenceHelper
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
            started = SharingStarted.WhileSubscribed(500),
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
