package com.example.bankapp.services

import android.content.Context
import androidx.core.content.edit

class SessionManagementService(context: Context) {
    private val prefs = context.getSharedPreferences(
        "bank_app_prefs",
        Context.MODE_PRIVATE
    )
    companion object {

        private const val KEY_USER_ID = "logged_in_user_id"
    }

    fun saveUserId(userId: String) {
        prefs.edit { putString(KEY_USER_ID, userId) }
    }

    fun getUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }

    fun clearSession() {
        prefs.edit { remove(KEY_USER_ID) }
    }
}