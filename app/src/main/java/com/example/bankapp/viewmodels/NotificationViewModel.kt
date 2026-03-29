package com.example.bankapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel


class NotificationViewmodel : ViewModel() {

    var hasPermissionBeenRequested by mutableStateOf(false)
        private set

    fun onPermissionResult() {
        hasPermissionBeenRequested = true
    }

}