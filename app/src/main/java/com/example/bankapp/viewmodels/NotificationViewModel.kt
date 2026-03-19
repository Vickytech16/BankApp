package com.example.bankapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class NotificationViewmodel: ViewModel() {

    var isPermissionGranted by mutableStateOf(false)
        private set

    var hasPermissionBeenRequested by mutableStateOf(false)
        private set

    fun onPermissionGranted() {
        isPermissionGranted = true
        hasPermissionBeenRequested = true
    }

    fun onPermissionDenied() {
        isPermissionGranted = false
        hasPermissionBeenRequested = true
    }

    fun resetPermissionState() {
        isPermissionGranted = false
        hasPermissionBeenRequested = false
    }

}