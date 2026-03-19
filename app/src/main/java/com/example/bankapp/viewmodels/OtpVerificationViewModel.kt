package com.example.bankapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class OtpVerificationViewModel : ViewModel() {
    var onOtpSuccess: ( () -> Unit)? = null

    fun onOtpSuccessChange(callback:  () -> Unit) {
        onOtpSuccess = callback
    }

    var showPasswordDialog by mutableStateOf(false)
        private set

    fun onShowPasswordDialogCHange(show: Boolean) {
        showPasswordDialog = show
    }
    fun otpVerified() {
        onOtpSuccess?.invoke()
    }


}