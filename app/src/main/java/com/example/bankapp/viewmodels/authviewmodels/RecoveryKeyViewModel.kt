package com.example.bankapp.viewmodels.authviewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.entities.errors.FormError
import com.example.bankapp.services.PasswordHashingService
import com.example.bankapp.entities.ChangePasswordState
import kotlinx.coroutines.launch

class RecoveryKeyViewModel(
    private val changePasswordState: ChangePasswordState
) : ViewModel() {
    var recoveryKey by mutableStateOf("")
        private set

    var recoveryKeyError by mutableStateOf<FormError?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var isVerified by mutableStateOf(false)
        private set

    fun onKeyChange(newValue: String) {
        if (newValue.length <= 10) {
            recoveryKey = newValue
            recoveryKeyError = null
        }
    }

    fun onSubmit() {
        if (recoveryKey.length < 10) {
            recoveryKeyError = FormError.RecoveryKeyMustBe10DigitsLong
            return
        }

        viewModelScope.launch {
            isLoading = true
            try {
                val isValid = PasswordHashingService.matches(recoveryKey, changePasswordState.user!!.recoveryKey)
                if (isValid) {
                    isVerified = true
                } else {
                    recoveryKeyError = FormError.RecoveryKeyDoesNotMatch
                }
            } catch (e: Exception) {
                recoveryKeyError = FormError.UnknownError
            } finally {
                isLoading = false
            }
        }
    }
}