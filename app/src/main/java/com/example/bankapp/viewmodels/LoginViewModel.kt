package com.example.bankapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.R
import com.example.bankapp.entities.dbtables.User
import com.example.bankapp.usecases.SharedPreferenceHelper
import com.example.bankapp.repositories.UserRepository
import com.example.bankapp.services.PasswordHashingService
import com.example.bankapp.utilities.emptyTextFieldErrorMessageBuilder
import com.example.bankapp.entities.errors.FormError
import com.example.bankapp.utilities.uiUserId
import kotlinx.coroutines.launch
import com.example.bankapp.entities.types.ui.LoginType
import com.example.bankapp.utilities.EMAIL_MAX_SIZE
import com.example.bankapp.utilities.PASSWORD_MAX_SIZE
import com.example.bankapp.utilities.PHONE_NUMBER_MAX_SIZE
import com.example.bankapp.utilities.maxAllowedCharacterErrorMessageBuilder


class LoginViewModel (
    private val userRepository: UserRepository,
    private val sharedPreferenceHelper: SharedPreferenceHelper
): ViewModel(){

    init {
        viewModelScope.launch {
            userRepository.ping()
        }
    }

    var userIdentifier by mutableStateOf("")
        private set

    var userIdentifierError by mutableStateOf<FormError?>(null)
        private set

    fun onIdentifierChange(newIdentifier: String){
        if(newIdentifier.length <= EMAIL_MAX_SIZE)
            userIdentifier = newIdentifier

        userIdentifierError =
            newIdentifier.emptyTextFieldErrorMessageBuilder(R.string.generic_field_name) ?:
                    newIdentifier.maxAllowedCharacterErrorMessageBuilder(R.string.generic_field_name, EMAIL_MAX_SIZE)
        resetSubmitError()
    }

    var password by mutableStateOf("")
        private set

    var passwordError by mutableStateOf<FormError?>(null)
        private set

    var passwordVisible by mutableStateOf(false)
        private set

    fun onPasswordChange(newPassword: String){
        if(newPassword.length <= PASSWORD_MAX_SIZE)
            password = newPassword
        passwordError =
            newPassword.emptyTextFieldErrorMessageBuilder(R.string.password_field_name) ?:
            newPassword.maxAllowedCharacterErrorMessageBuilder(R.string.password_field_name, PASSWORD_MAX_SIZE)
        resetSubmitError()
    }

    fun onPasswordVisibleChange(){
        passwordVisible = !passwordVisible
    }

    fun resetSubmitError(){
        if(userIdentifierError==null && passwordError==null)
            submitError = null
    }

    var submitError by mutableStateOf<FormError?>(null)
        private set

    var isLoginSuccessful by mutableStateOf(false)
        private  set

    var isLoading by mutableStateOf(false)
        private set

    fun onSubmit() {

        if (isLoading)
            return

        isLoading = true

        onPasswordChange(password)

        viewModelScope.launch {
            try {

                if ( userIdentifierError != null || passwordError != null)
                    submitError = FormError.AllFieldsAreRequired
                else {
                    val user: User? =
                        if(userIdentifier.all { it.isDigit() })
                            userRepository.getUserByPhoneNumber(userIdentifier)
                        else
                            userRepository.getUserByEmail(userIdentifier)

                    if (user == null) {
                        submitError = FormError.InvalidCredentials
                    } else {
                        if (PasswordHashingService.matches(password, user.passwordHashed)) {
                            sharedPreferenceHelper.saveUserOnSharedPreferences(user.userId.uiUserId)
                            isLoginSuccessful = true

                        } else
                            submitError = FormError.InvalidCredentials
                    }
                }
            } catch (_: Exception) {
                submitError = FormError.UnknownError
            } finally {
                isLoading = false
            }
        }
    }
}