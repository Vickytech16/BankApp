package com.example.bankapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.R
import com.example.bankapp.entities.dbtables.User
import com.example.bankapp.repositories.UserRepository
import com.example.bankapp.usecases.ChangePasswordUseCase
import com.example.bankapp.utilities.EMAIL_MAX_SIZE
import com.example.bankapp.utilities.PHONE_NUMBER_MAX_SIZE
import com.example.bankapp.utilities.emptyTextFieldErrorMessageBuilder
import com.example.bankapp.utilities.maxAllowedCharacterErrorMessageBuilder
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
        if (newEmail.length <= EMAIL_MAX_SIZE)
            email = newEmail
        emailError =
                    newEmail.emptyTextFieldErrorMessageBuilder(R.string.email_field_name) ?:
                    newEmail.maxAllowedCharacterErrorMessageBuilder(R.string.email_field_name, EMAIL_MAX_SIZE)
        onSubmitErrorReset()
    }

    var phoneNumber by mutableStateOf("")
        private set

    var phoneNumberError by mutableStateOf<FormError?>(null)
        private set

    fun onPhoneNumberChange(newPhoneNumber: String){
        if (newPhoneNumber.length <= PHONE_NUMBER_MAX_SIZE)
            phoneNumber = newPhoneNumber
        phoneNumberError =
                    newPhoneNumber.emptyTextFieldErrorMessageBuilder(R.string.phone_number_field_name) ?:
                    newPhoneNumber.maxAllowedCharacterErrorMessageBuilder(R.string.phone_number_field_name, PHONE_NUMBER_MAX_SIZE)
        onSubmitErrorReset()
    }

    var submitError by mutableStateOf<FormError?>(null)
        private set

    var isVerificationSuccessful by mutableStateOf(false)
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun onSubmitErrorReset(){
        if(emailError==null && phoneNumberError==null)
            submitError = null
    }


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