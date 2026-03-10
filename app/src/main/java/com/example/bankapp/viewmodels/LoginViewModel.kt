package com.example.bankapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.R
import com.example.bankapp.entities.User
import com.example.bankapp.usecases.SessionUseCase
import com.example.bankapp.repositories.UserRepository
import com.example.bankapp.services.PasswordHashingService
import com.example.bankapp.utilities.emptyTextFieldErrorMessageBuilder
import com.example.bankapp.entities.errors.FormError
import com.example.bankapp.utilities.uiUserId
import kotlinx.coroutines.launch
import com.example.bankapp.entities.types.LoginType



class LoginViewModel (
    private val userRepository: UserRepository,
    private val sessionUseCase: SessionUseCase
): ViewModel(){

    init {
        viewModelScope.launch {
            userRepository.ping()
        }
    }

    var loginType by mutableStateOf(LoginType.EMAIL)
        private set

    fun onLoginTypeChange(newLoginType: LoginType){
        email = ""
        password = ""
        phoneNumber = ""
        emailError = null
        phoneNumberError = null
        passwordError = null
        passwordVisible = false
        loginType = newLoginType
        submitError = null
    }

    var email by mutableStateOf("")
        private  set

    var emailError by mutableStateOf<FormError?>(null)
        private set

    fun onEmailChange(newEmail: String){
        email = newEmail
        emailError = email.emptyTextFieldErrorMessageBuilder(R.string.email_field_name)
        resetSubmitError()
    }

    var phoneNumber by mutableStateOf("")
        private set

    var phoneNumberError by mutableStateOf<FormError?>(null)
        private set

    fun onPhoneNumberChange(newPhoneNumber: String){
        phoneNumber = newPhoneNumber
        phoneNumberError = phoneNumber.emptyTextFieldErrorMessageBuilder(R.string.phone_number_field_name)
        resetSubmitError()
    }

    var password by mutableStateOf("")
        private set

    var passwordError by mutableStateOf<FormError?>(null)
        private set

    var passwordVisible by mutableStateOf(false)
        private set

    fun onPasswordChange(newPassword: String){
        password = newPassword
        passwordError = password.emptyTextFieldErrorMessageBuilder(R.string.password_field_name)
        resetSubmitError()
    }

    fun onPasswordVisibleChange(){
        passwordVisible = !passwordVisible
    }

    var submitError by mutableStateOf<FormError?>(null)
        private set

    fun resetSubmitError(){
        when(loginType){
           LoginType.EMAIL -> {
                if(emailError==null && passwordError==null)
                    submitError = null
            }
           LoginType.PHONE_NUMBER -> {
                if(phoneNumberError==null && passwordError==null)
                    submitError = null
            }
        }
    }

    var isLoginSuccessful by mutableStateOf(false)
        private  set

    var isLoading by mutableStateOf(false)
        private set

    fun onSubmit() {

        if (isLoading)
            return

        isLoading = true

        when (loginType) {
            LoginType.EMAIL -> {
                onEmailChange(email)
            }

            LoginType.PHONE_NUMBER -> {
                onPhoneNumberChange(phoneNumber)
            }
        }
        onPasswordChange(password)


        viewModelScope.launch {
            try {

                if (emailError != null || passwordError != null || phoneNumberError != null)
                    submitError = FormError.AllFieldsAreRequired
                else {
                    val user: User? =
                        when (loginType) {
                            LoginType.EMAIL -> userRepository.getUserByEmail(email)
                            LoginType.PHONE_NUMBER -> userRepository.getUserByPhoneNumber(
                                phoneNumber
                            )
                        }
                    if (user == null) {
                        submitError = FormError.InvalidCredentials
                    } else {
                        if (PasswordHashingService.matches(password, user.passwordHashed)) {
                            sessionUseCase.saveUserOnSharedPreferences(user.userId.uiUserId)
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