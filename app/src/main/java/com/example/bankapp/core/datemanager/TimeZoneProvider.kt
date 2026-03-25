package com.example.bankapp.core.datemanager

import android.content.SharedPreferences
import androidx.core.content.edit

interface TimezoneProvider {
    fun getTimezone(): String
    fun setTimezone(zoneId: String)
}

class TimezoneProviderImpl(private val preferences: SharedPreferences) : TimezoneProvider {
    companion object {
        private const val KEY_TIMEZONE = "user_timezone"
        private const val DEFAULT_TIMEZONE = "Asia/Kolkata"
    }

    override fun getTimezone(): String {
        return preferences.getString(KEY_TIMEZONE, DEFAULT_TIMEZONE) ?: DEFAULT_TIMEZONE
    }

    override fun setTimezone(zoneId: String) {
        preferences.edit { putString(KEY_TIMEZONE, zoneId) }
    }
}