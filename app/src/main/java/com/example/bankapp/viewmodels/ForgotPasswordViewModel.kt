package com.example.bankapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.R
import com.example.bankapp.entities.User
import com.example.bankapp.repositories.UserRepository
import com.example.bankapp.usecases.ChangePasswordUseCase
import com.example.bankapp.utilities.emptyTextFieldErrorMessageBuilder
import com.example.bankapp.entities.errors.FormError
import kotlinx.coroutines.launch


class ForgotPasswordViewModel(
   private val userRepository: UserRepository,
   private val changePasswordUseCase: ChangePasswordUseCase
): ViewModel() {

    var email by mutableStateOf("")
        private  set

    var emailError by mutableStateOf<FormError?>(null)
        private set

    fun onEmailChange(newEmail: String){
        email = newEmail
        emailError = email.emptyTextFieldErrorMessageBuilder(R.string.email_field_name)

    }

    var phoneNumber by mutableStateOf("")
        private set

    var phoneNumberError by mutableStateOf<FormError?>(null)
        private set

    fun onPhoneNumberChange(newPhoneNumber: String){
        phoneNumber = newPhoneNumber
        phoneNumberError = phoneNumber.emptyTextFieldErrorMessageBuilder(R.string.phone_number_field_name)
    }

    var submitError by mutableStateOf<FormError?>(null)
        private set

    var isVerificationSuccessful by mutableStateOf(false)
        private set

    var isLoading by mutableStateOf(false)
        private set


    fun onSubmit(){

        if(isLoading)
            return

        onEmailChange(email)
        onPhoneNumberChange(phoneNumber)

        isLoading = true


            viewModelScope.launch {
                try {
                    if (email.isBlank() || phoneNumber.isBlank())
                        submitError = FormError.AllFieldsAreRequired
                    else {
                        val user: User? =
                            userRepository.getUserByEmailAndPhoneNumber(email.trim(), phoneNumber)
                        if (user == null)
                            submitError = FormError.InvalidCredentials
                        else {
                            submitError = null
                            changePasswordUseCase.user = user
                            isVerificationSuccessful = true
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