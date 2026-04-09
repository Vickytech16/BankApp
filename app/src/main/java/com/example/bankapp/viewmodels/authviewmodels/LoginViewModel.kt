package com.example.bankapp.viewmodels.authviewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.R
import com.example.bankapp.entities.dbtables.User
import com.example.bankapp.entities.errors.FormError
import com.example.bankapp.repositories.UserRepository
import com.example.bankapp.services.PasswordHashingService
import com.example.bankapp.ui.theme.emailRegex
import com.example.bankapp.utilities.SharedPreferenceHelper
import com.example.bankapp.utilities.EMAIL_MAX_SIZE
import com.example.bankapp.utilities.PASSWORD_MAX_SIZE
import com.example.bankapp.utilities.emptyTextFieldErrorMessageBuilder
import com.example.bankapp.utilities.invalidEmailErrorMessageBuilder
import com.example.bankapp.utilities.maxAllowedCharacterErrorMessageBuilder
import com.example.bankapp.utilities.uiUserId
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userRepository: UserRepository,
    private val sharedPreferenceHelper: SharedPreferenceHelper
) : ViewModel() {

    var userIdentifier by mutableStateOf("")
        private set

    var userIdentifierError by mutableStateOf<FormError?>(null)
        private set

    var password by mutableStateOf("")
        private set

    var passwordError by mutableStateOf<FormError?>(null)
        private set

    var passwordVisible by mutableStateOf(false)
        private set

    var submitError by mutableStateOf<FormError?>(null)
        private set

    var isLoginSuccessful by mutableStateOf(false)
        private set

    var isLoading by mutableStateOf(false)
        private set

    private var checkedByPhone = false


    init {
        viewModelScope.launch {
            userRepository.ping()
        }
    }


    fun onIdentifierChange(newIdentifier: String) {
        val processed = if (newIdentifier.length > EMAIL_MAX_SIZE) {
            newIdentifier.substring(0, EMAIL_MAX_SIZE)
        }
        else {
            newIdentifier
        }

        userIdentifier = processed
        userIdentifierError = processed.emptyTextFieldErrorMessageBuilder(R.string.generic_field_name) ?:
                if (processed.length >= EMAIL_MAX_SIZE) {
                    processed.maxAllowedCharacterErrorMessageBuilder(R.string.generic_field_name, EMAIL_MAX_SIZE)
                }
                else {
                    null
                }

        resetSubmitError()
    }


    fun onPasswordChange(newPassword: String) {
        val processed = if (newPassword.length > PASSWORD_MAX_SIZE) {
            newPassword.substring(0, PASSWORD_MAX_SIZE)
        }
        else {
            newPassword
        }

        password = processed
        passwordError = processed.emptyTextFieldErrorMessageBuilder(R.string.password_field_name) ?:
                if (processed.length >= PASSWORD_MAX_SIZE) {
                    processed.maxAllowedCharacterErrorMessageBuilder(R.string.password_field_name, PASSWORD_MAX_SIZE)
                }
                else {
                    null
                }

        resetSubmitError()
    }


    fun onPasswordVisibleChange() {
        passwordVisible = !passwordVisible
    }


    fun resetSubmitError() {
        if (userIdentifierError == null && passwordError == null) {
            submitError = null
        }
    }


    fun onSubmit() {
        if (isLoading) {
            return
        }

        isLoading = true
        onPasswordChange(password)
        onIdentifierChange(userIdentifier)

        viewModelScope.launch {
            try {
                if (userIdentifierError != null || passwordError != null) {
                    submitError = FormError.AllFieldsAreRequired
                }
                else {
                    val user: User? =
                        if(userIdentifier.invalidEmailErrorMessageBuilder()==null)
                            userRepository.getUserByEmail(userIdentifier)
                        else
                            userRepository.getUserByPhoneNumber(userIdentifier)


                    if (user == null) {
                        submitError = FormError.InvalidCredentials
                    }
                    else {
                        if (PasswordHashingService.matches(password, user.passwordHashed)) {
                            sharedPreferenceHelper.saveUserOnSharedPreferences(user.userId.uiUserId)
                            isLoginSuccessful = true
                        }
                        else {
                            submitError = FormError.InvalidCredentials
                        }
                    }
                }
            }
            catch (_: Exception) {
                submitError = FormError.UnknownError
            }
            finally {
                isLoading = false
            }
        }
    }
}