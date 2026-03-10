package com.example.bankapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.R
import com.example.bankapp.entities.User
import com.example.bankapp.repositories.UserRepository
import com.example.bankapp.services.PasswordHashingService
import com.example.bankapp.utilities.*
import com.example.bankapp.utilities.maxAllowedCharacterErrorMessageBuilder
import com.example.bankapp.utilities.emptyTextFieldErrorMessageBuilder
import com.example.bankapp.entities.errors.FormError
import com.example.bankapp.entities.errors.UiError
import com.example.bankapp.utilities.invalidConfirmPasswordErrorMessageBuilder
import com.example.bankapp.utilities.invalidEmailErrorMessageBuilder
import com.example.bankapp.utilities.invalidPasswordErrorMessageBuilder
import com.example.bankapp.utilities.invalidNumericalFieldErrorMessageBuilder
import com.example.bankapp.utilities.invalidUserNameErrorMessageBuilder
import kotlinx.coroutines.launch


class RegisterViewModel(
    private val userRepository: UserRepository
): ViewModel()
{
    var userName by mutableStateOf("")
        private set

    var userNameError by mutableStateOf<FormError?>(null)
        private set

    fun onUserNameChange(newUserName: String) {
        userName = newUserName
        userNameError =
            userName.emptyTextFieldErrorMessageBuilder(R.string.username_field_name) ?:
            userName.maxAllowedCharacterErrorMessageBuilder(
                R.string.username_field_name, USERNAME_MAX_SIZE
            ) ?:
            userName.invalidUserNameErrorMessageBuilder()

        submitErrorReset()
    }

    var email by mutableStateOf("")
        private set

    var emailError by mutableStateOf<FormError?>(null)
        private set

    var emailFieldSelected by mutableStateOf(false)
        private set


    fun onEmailChange(newEmail: String) {
        email = newEmail

        emailError =
            email.emptyTextFieldErrorMessageBuilder(R.string.email_field_name) ?:
            email.maxAllowedCharacterErrorMessageBuilder(R.string.email_field_name, EMAIL_MAX_SIZE) ?:
            email.invalidEmailErrorMessageBuilder()

        submitErrorReset()
    }

    fun onEmailFieldSelectedChange(newValue: Boolean){
        emailFieldSelected = newValue
    }

    var phoneNumber by mutableStateOf("")
        private set

    var phoneNumberError by mutableStateOf<FormError?>(null)
        private set

    fun onPhoneNumberChange(newPhoneNumber: String) {
        phoneNumber = newPhoneNumber
        phoneNumberError =
            phoneNumber.emptyTextFieldErrorMessageBuilder(R.string.phone_number_field_name) ?:
            phoneNumber.maxAllowedCharacterErrorMessageBuilder(R.string.phone_number_field_name,
                PHONE_NUMBER_MAX_SIZE
            ) ?:
            phoneNumber.invalidNumericalFieldErrorMessageBuilder(R.string.phone_number_field_name)

        submitErrorReset()
    }

    var password by mutableStateOf("")
        private set

    var passwordError by mutableStateOf<List<UiError>>(emptyList())
        private set

    var hasPasswordFieldEverFocused by mutableStateOf(false)
        private set
    var hasPasswordFieldEverUnFocused by mutableStateOf(false)
        private set

    fun onHasPasswordFieldEverFocusedChange(newValue: Boolean){
        hasPasswordFieldEverFocused = newValue
    }

    fun onHasPasswordFieldEverUnFocusedChange(newValue: Boolean){
        hasPasswordFieldEverUnFocused = newValue
    }

    fun onPasswordChange(newPassword: String) {
        password = newPassword
        passwordError = listOfNotNull(
            password.emptyTextFieldErrorMessageBuilder(R.string.password_field_name),
                        password.maxAllowedCharacterErrorMessageBuilder(R.string.password_field_name,
                            PASSWORD_MAX_SIZE
                        )
        ) + password.invalidPasswordErrorMessageBuilder()

        if(confirmPassword.isNotBlank())
            confirmPasswordError = confirmPassword.invalidConfirmPasswordErrorMessageBuilder(password)

        submitErrorReset()
    }

    var passwordVisible by mutableStateOf(false)
        private set

    fun onPasswordVisibleChange() {
        passwordVisible = !passwordVisible
    }

    var confirmPassword by mutableStateOf("")
        private set

    var confirmPasswordError by mutableStateOf<FormError?>(null)
        private set

    fun onConfirmPasswordChange(newConfirmPassword: String) {
        confirmPassword = newConfirmPassword
        confirmPasswordError =
            confirmPassword.emptyTextFieldErrorMessageBuilder(R.string.confirm_password_field_name) ?:
            confirmPassword.maxAllowedCharacterErrorMessageBuilder(R.string.confirm_password_field_name, PASSWORD_MAX_SIZE) ?:
            confirmPassword.invalidConfirmPasswordErrorMessageBuilder(password)

        submitErrorReset()
    }

    var confirmPasswordVisible by mutableStateOf(false)
        private set

    fun onConfirmPasswordVisibleChange() {
        confirmPasswordVisible = !confirmPasswordVisible
    }

    var isSubmitButtonClicked by mutableStateOf(false)
        private set

    fun onIsSubmitButtonClickedChange(newValue: Boolean){
        isSubmitButtonClicked = newValue
    }

    var submitError by mutableStateOf<FormError?>(null)
        private set

    var isSubmitSuccessful by mutableStateOf(false)
        private set

    private fun submitErrorReset()
    {
        if(userNameError==null && passwordError.isEmpty()  && emailError==null && phoneNumberError==null && confirmPasswordError==null)
            submitError=null
    }

    var isLoading by mutableStateOf(false)
        private set

     fun onSubmit() {

            if(isLoading)
                return

            isLoading = true

            isSubmitSuccessful = false
            submitError = null

            hasPasswordFieldEverFocused = true
            hasPasswordFieldEverUnFocused = true

         onUserNameChange(userName)
         onEmailChange(email)
         onPhoneNumberChange(phoneNumber)
         onPasswordChange(password)
         onConfirmPasswordChange(confirmPassword)

         viewModelScope.launch {
             try {
                 if (userNameError != null || passwordError.isNotEmpty() || emailError != null || phoneNumberError != null || confirmPasswordError != null) {
                     submitError = FormError.InvalidData
                 } else {
                     if (userRepository.getUserByEmail(email) != null)
                         submitError = FormError.UserAlreadyExists(R.string.email_field_name)
                     else if (userRepository.getUserByPhoneNumber(phoneNumber) != null)
                         submitError =
                             FormError.UserAlreadyExists(R.string.phone_number_field_name)
                     else {
                         submitError = null
                         isSubmitSuccessful = true
                         userRepository.createNewUser(
                             User(
                                 email = email,
                                 passwordHashed = PasswordHashingService.hash(password),
                                 userName = userName
                                     .trim()
                                     .replace(Regex("\\s+"), " "),
                                 phoneNumber = phoneNumber
                             )
                         )
                     }
                 }

             }
             catch (_: Exception){
                 submitError = FormError.InvalidData
             }
             finally {
                 isLoading = false
             }
         }
     }
}