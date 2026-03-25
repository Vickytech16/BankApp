package com.example.bankapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.R
import com.example.bankapp.entities.dbtables.User
import com.example.bankapp.repositories.UserRepository
import com.example.bankapp.services.PasswordHashingService
import com.example.bankapp.usecases.ChangePasswordUseCase
import com.example.bankapp.utilities.*
import com.example.bankapp.utilities.emptyTextFieldErrorMessageBuilder
import com.example.bankapp.entities.errors.FormError
import com.example.bankapp.entities.errors.UiError
import com.example.bankapp.utilities.invalidConfirmPasswordErrorMessageBuilder
import com.example.bankapp.utilities.invalidPasswordErrorMessageBuilder
import com.example.bankapp.utilities.maxAllowedCharacterErrorMessageBuilder
import kotlinx.coroutines.launch



class ChangePasswordViewModel(
    private val userRepository: UserRepository,
    private val changePasswordUseCase: ChangePasswordUseCase
) : ViewModel(){

    private var user by mutableStateOf<User?>(null)

    private fun updateCurrentUser() {
        user = changePasswordUseCase.user
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
        if (newPassword.length <= PASSWORD_MAX_SIZE)
            password = newPassword
        passwordError = listOfNotNull(
            newPassword.emptyTextFieldErrorMessageBuilder(R.string.password_field_name),
                        newPassword.maxAllowedCharacterErrorMessageBuilder(R.string.password_field_name, PASSWORD_MAX_SIZE)
        ) + newPassword.invalidPasswordErrorMessageBuilder()

        if(confirmPassword.isNotBlank())
            confirmPasswordError = confirmPassword.invalidConfirmPasswordErrorMessageBuilder(newPassword)
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
        if (newConfirmPassword.length <= PASSWORD_MAX_SIZE)
            confirmPassword = newConfirmPassword
        confirmPasswordError =
                    newConfirmPassword.emptyTextFieldErrorMessageBuilder(R.string.confirm_password_field_name) ?:
                    newConfirmPassword.maxAllowedCharacterErrorMessageBuilder(R.string.confirm_password_field_name, PASSWORD_MAX_SIZE) ?:
                    newConfirmPassword.invalidConfirmPasswordErrorMessageBuilder(password)
    }

    var confirmPasswordVisible by mutableStateOf(false)
        private set

    fun onConfirmPasswordVisibleChange() {
        confirmPasswordVisible = !confirmPasswordVisible
    }

    private var isSubmitButtonClicked by mutableStateOf(false)
        private set

    var submitError by mutableStateOf<FormError?>(null)
        private set

    var isSubmitSuccessful by mutableStateOf(false)
        private set

    fun submitErrorReset(){
        if(passwordError.isEmpty() && confirmPasswordError==null){
            submitError = null
        }
    }

    var isLoading by mutableStateOf(false)
        private set

    fun onSubmit(){

        if(isLoading)
            return

        println("This reached")

        isLoading = true

        isSubmitButtonClicked = true
        onPasswordChange(password)
        onConfirmPasswordChange(confirmPassword)


        viewModelScope.launch {
            try {

                if (password.isBlank() || confirmPassword.isBlank()) {
                    submitError = FormError.AllFieldsAreRequired
                    return@launch
                }

                if (passwordError.isNotEmpty() || confirmPasswordError != null) {
                    submitError = FormError.InvalidData
                    return@launch
                }

                updateCurrentUser()

                if (user == null) {
                    submitError = FormError.UnknownError
                    return@launch
                }

                val updatedUser = user!!.copy(
                    passwordHashed = PasswordHashingService.hash(password)
                )
                userRepository.updateUser(updatedUser)

                isSubmitSuccessful = true
                submitError = null

            } catch (_: Exception) {
                submitError = FormError.UnknownError
            } finally {
                isLoading = false
            }
        }
    }
}