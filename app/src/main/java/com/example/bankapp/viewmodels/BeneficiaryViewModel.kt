package com.example.bankapp.viewmodels

import SharedTransactionViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.R
import com.example.bankapp.entities.SessionState
import com.example.bankapp.entities.dbtables.User
import com.example.bankapp.entities.errors.FormError

import com.example.bankapp.repositories.UserRepository
import com.example.bankapp.repositories.BeneficiaryRepository
import com.example.bankapp.entities.dbtables.Account
import com.example.bankapp.entities.uimodels.AccountUiModel
import com.example.bankapp.repositories.AccountRepository
import com.example.bankapp.utilities.EMAIL_MAX_SIZE
import com.example.bankapp.utilities.PHONE_NUMBER_MAX_SIZE
import com.example.bankapp.utilities.emptyTextFieldErrorMessageBuilder
import com.example.bankapp.utilities.maxAllowedCharacterErrorMessageBuilder
import kotlinx.coroutines.launch

class AddBeneficiaryViewModel(
    private val userRepository: UserRepository,
    private val beneficiaryRepository: BeneficiaryRepository,
    private val sessionState: SessionState.Authenticated.AccountRegistered,
    private val sharedTransactionViewModel: SharedTransactionViewModel,
    private val accountRepository: AccountRepository
) : ViewModel() {


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

    var isLoading by mutableStateOf(false)
        private set

    var isVerificationSuccessful by mutableStateOf(false)
        private set


    private var alreadySuceeded = false

    fun onSubmitErrorReset(){
        if(emailError==null && phoneNumberError==null)
            submitError = null
    }


    fun onSubmit() {

        if(isLoading)
            return

        onEmailChange(email)
        onPhoneNumberChange(phoneNumber)
        isLoading = true

        viewModelScope.launch {
            try {
                if (email.isBlank() || phoneNumber.isBlank()) {
                    submitError = FormError.AllFieldsAreRequired
                    return@launch
                }

                if(email == sessionState.user.email){
                    submitError = FormError.YouAreTheUser(R.string.email_field_name)
                    return@launch
                }

                if(phoneNumber == sessionState.user.phoneNumber){
                    phoneNumberError = FormError.YouAreTheUser(R.string.phone_number_field_name)
                    return@launch
                }

                if(submitError==null) {
                    val friend: User? =
                        userRepository.getUserByEmailAndPhoneNumber(email.trim(), phoneNumber.trim())
                    if (friend == null) {
                        submitError = FormError.InvalidCredentials
                        return@launch
                    }
                    else {
                        if(beneficiaryRepository.getBeneficiary(sessionState.user.userId, friend.userId) == null){
                            if(accountRepository.getAccountByUserId(friend.userId).isEmpty()){
                                submitError = FormError.UserDoesNotHaveAccountError
                            }else {
                                sharedTransactionViewModel.initializeAddBeneficiary(
                                    sessionState.user.userId,
                                    friend.userId,
                                    ""
                                )
                                isVerificationSuccessful = true
                            }
                        }
                        else{
                            submitError = FormError.AlreadyYourFriendError
                            return@launch
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                submitError = FormError.UnknownError
            } finally {
                isLoading = false
            }
        }
    }
}