package com.example.bankapp.viewmodels

import com.example.bankapp.usecases.TransactionSessionHolder


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.R
import com.example.bankapp.entities.SessionState
import com.example.bankapp.entities.errors.FormError
import com.example.bankapp.services.PasswordHashingService
import com.example.bankapp.usecases.CurrentTransactionStatus
import com.example.bankapp.utilities.PASSWORD_MAX_SIZE
import com.example.bankapp.utilities.emptyTextFieldErrorMessageBuilder
import com.example.bankapp.utilities.maxAllowedCharacterErrorMessageBuilder
import kotlinx.coroutines.launch

class PasswordConfirmationViewModel(
    private val sessionState: SessionState.Authenticated.AccountRegistered,
   // private val transactionSessionHolder: TransactionSessionHolder
) : ViewModel() {

    private val user = sessionState.user

    var password by mutableStateOf("")
        private set

    var passwordError by mutableStateOf<FormError?>(null)
        private set

    var passwordVisible by mutableStateOf(false)
        private set

    var submitError by mutableStateOf<FormError?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var showPasswordDialog by mutableStateOf(false)
        private set

    fun onPasswordDialogDismiss() {
        showPasswordDialog = false
        showCancelDialog = true
    }

    var showCancelDialog by mutableStateOf(false)
        private set

    var isPasswordVerified by mutableStateOf(false)
        private set

    fun onPasswordChange(newPassword: String) {
        if (newPassword.length <= PASSWORD_MAX_SIZE)
            password = newPassword
        passwordError =
            newPassword.emptyTextFieldErrorMessageBuilder(R.string.password_field_name) ?:
                    newPassword.maxAllowedCharacterErrorMessageBuilder(R.string.password_field_name, PASSWORD_MAX_SIZE)
        submitError = null
    }

    fun onPasswordVisibleChange() {
        passwordVisible = !passwordVisible
    }

    fun onShowPasswordDialogChange(newValue: Boolean) {
        showPasswordDialog = newValue
    }

    fun onShowCancelDialogChange(newValue: Boolean) {
        showCancelDialog = newValue
    }

    fun onSubmit(password: String) {
        if (isLoading)
            return

        onPasswordChange(password)

        viewModelScope.launch {
            try {
                isLoading = true
                if (passwordError != null) {
                    submitError = FormError.InvalidData
                    return@launch
                }

                if (PasswordHashingService.matches(password, user.passwordHashed)) {
                   // transactionSessionHolder.onPasswordVerification?.invoke()
                    isPasswordVerified = true
                } else {
                    submitError = FormError.PasswordDoesntMatch
                }
            } catch (_: Exception) {
                submitError = FormError.UnknownError
            } finally {
                isLoading = false
            }
        }
    }

    fun resetSession(){
        ///transactionSessionHolder.reset()
    }

    fun resetScreenState() {
        password = ""
        passwordError = null
        passwordVisible = false
        submitError = null
        isLoading = false
        showPasswordDialog = false
        showCancelDialog = false
        isPasswordVerified = false
    }
}