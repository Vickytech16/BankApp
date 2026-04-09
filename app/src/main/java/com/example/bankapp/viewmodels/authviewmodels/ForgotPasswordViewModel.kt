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
import com.example.bankapp.utilities.invalidEmailErrorMessageBuilder
import com.example.bankapp.utilities.maxAllowedCharacterErrorMessageBuilder
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val userRepository: UserRepository,
    private val changePasswordState: ChangePasswordState
) : ViewModel() {

    var userIdentifier by mutableStateOf("")
        private set

    var userIdentifierError by mutableStateOf<FormError?>(null)
        private set

    var submitError by mutableStateOf<FormError?>(null)
        private set

    var isVerificationSuccessful by mutableStateOf(false)
        private set

    var isLoading by mutableStateOf(false)
        private set

    private var checkedByPhone = false


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
                    processed.maxAllowedCharacterErrorMessageBuilder(
                        R.string.generic_field_name,
                        EMAIL_MAX_SIZE
                    )
                }
                else {
                    null
                }

        onSubmitErrorReset()
    }


    fun onSubmitErrorReset() {
        if (userIdentifierError == null) {
            submitError = null
        }
    }


    fun onSubmit() {
        if (isLoading) {
            return
        }

        isLoading = true
        onIdentifierChange(userIdentifier)

        viewModelScope.launch {
            try {
                if (userIdentifierError != null || userIdentifier.isBlank()) {
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
                        submitError = null
                        changePasswordState.user = user
                        isVerificationSuccessful = true
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