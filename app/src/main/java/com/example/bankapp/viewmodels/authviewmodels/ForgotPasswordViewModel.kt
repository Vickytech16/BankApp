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
import com.example.bankapp.entities.ChangePasswordState
import com.example.bankapp.utilities.EMAIL_MAX_SIZE
import com.example.bankapp.utilities.emptyTextFieldErrorMessageBuilder
import com.example.bankapp.utilities.maxAllowedCharacterErrorMessageBuilder
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val userRepository: UserRepository,
    private val changePasswordState: ChangePasswordState
): ViewModel() {

    var userIdentifier by mutableStateOf("")
        private set

    var userIdentifierError by mutableStateOf<FormError?>(null)
        private set

    fun onIdentifierChange(newIdentifier: String) {
        if (newIdentifier.length <= EMAIL_MAX_SIZE)
            userIdentifier = newIdentifier

        userIdentifierError =
            newIdentifier.emptyTextFieldErrorMessageBuilder(R.string.generic_field_name) ?:
                    newIdentifier.maxAllowedCharacterErrorMessageBuilder(
                        R.string.generic_field_name,
                        EMAIL_MAX_SIZE
                    )
        onSubmitErrorReset()
    }

    var submitError by mutableStateOf<FormError?>(null)
        private set

    var isVerificationSuccessful by mutableStateOf(false)
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun onSubmitErrorReset() {
        if (userIdentifierError == null)
            submitError = null
    }

    var checkedByPhone = false

    fun onSubmit() {
        if (isLoading) return
        isLoading = true

        onIdentifierChange(userIdentifier)

        viewModelScope.launch {
            try {
                if (userIdentifierError != null || userIdentifier.isBlank()) {
                    submitError = FormError.AllFieldsAreRequired
                } else {
                    var user: User? =
                        if (userIdentifier.any { it.isDigit() } || userIdentifier.startsWith("+")) {
                            checkedByPhone = true
                            userRepository.getUserByPhoneNumber(userIdentifier)
                        } else {
                            userRepository.getUserByEmail(userIdentifier)
                        }

                    if(checkedByPhone && user == null){
                        user = userRepository.getUserByEmail(userIdentifier)
                    }

                    if (user == null) {
                        submitError = FormError.InvalidCredentials
                    } else {
                        submitError = null
                        changePasswordState.user = user
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