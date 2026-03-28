package com.example.bankapp.services

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class SharedPreferenceService(context: Context) {
    private val prefs = context.getSharedPreferences(
        "bank_app_prefs",
        Context.MODE_PRIVATE
    )

    private val _themeFlow = MutableStateFlow(prefs.getString(THEME_KEY, DEFAULT_THEME) ?: DEFAULT_THEME)
    val themeFlow: StateFlow<String> = _themeFlow.asStateFlow()
    companion object {
        private const val KEY_USER_ID = "logged_in_user_id"
        private const val THEME_KEY = "app_theme"
        private const val DEFAULT_THEME = "SYSTEM_DEFAULT"

        private const val KEY_USER_TIMEZONE = "user_timezone"
        private const val DEFAULT_TIMEZONE = "UTC"
    }

    fun saveUserId(userId: String) {
        prefs.edit { putString(KEY_USER_ID, userId) }
    }

    fun getUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }

    fun clearSession() {
        prefs.edit {
            remove(KEY_USER_ID)
            remove(KEY_USER_TIMEZONE)
        }
    }

    fun getSavedTheme(): Flow<String> = themeFlow

    suspend fun saveTheme(theme: String) = withContext(Dispatchers.IO) {
        prefs.edit {
            putString(THEME_KEY, theme)
        }
        _themeFlow.value = theme
    }

    fun saveUserTimezone(userId: String, timezone: String) {
        prefs.edit {
            putString(KEY_USER_ID, userId)
            putString(KEY_USER_TIMEZONE, timezone)
        }
    }

    fun getUserTimezone(): String {
        return prefs.getString(KEY_USER_TIMEZONE, DEFAULT_TIMEZONE) ?: DEFAULT_TIMEZONE
    }

}