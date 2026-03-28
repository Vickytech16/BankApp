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
import com.example.bankapp.utilities.USERNAME_MAX_SIZE
import com.example.bankapp.utilities.emptyTextFieldErrorMessageBuilder
import com.example.bankapp.utilities.invalidUserNameErrorMessageBuilder
import com.example.bankapp.utilities.maxAllowedCharacterErrorMessageBuilder
import kotlinx.coroutines.launch

class AddBeneficiaryViewModel(
    private val userRepository: UserRepository,
    private val beneficiaryRepository: BeneficiaryRepository,
    private val sessionState: SessionState.Authenticated.AccountRegistered,
    private val sharedTransactionViewModel: SharedTransactionViewModel,
    private val accountRepository: AccountRepository
) : ViewModel() {


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

    var nickname by mutableStateOf("")
        private set

    var nicknameError by mutableStateOf<FormError?>(null)
        private set

    fun onNickNameChange(newNickName: String){
        if(newNickName.length <= USERNAME_MAX_SIZE)
            nickname = newNickName

        nicknameError = nickname.maxAllowedCharacterErrorMessageBuilder(R.string.nickname_optional_field_name, USERNAME_MAX_SIZE) ?:
                        nickname.invalidUserNameErrorMessageBuilder()
    }

    var submitError by mutableStateOf<FormError?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var isVerificationSuccessful by mutableStateOf(false)
        private set


    private var alreadySuceeded = false

    fun resetSubmitError(){
        if(userIdentifierError==null && nicknameError==null)
            submitError = null
    }


    fun onSubmit() {

        if(isLoading)
            return

        onIdentifierChange(userIdentifier)
        onNickNameChange(nickname)

        isLoading = true

        viewModelScope.launch {
            try {
                if (userIdentifier.isBlank()) {
                    submitError = FormError.AllFieldsAreRequired
                    return@launch
                }

                if(userIdentifier==sessionState.user.email || userIdentifier==sessionState.user.phoneNumber) {
                    submitError = FormError.YouAreTheUser
                }

                if(submitError==null) {
                    val friend: User? =
                        if(userIdentifier.all { it.isDigit() })
                            userRepository.getUserByPhoneNumber(userIdentifier)
                        else
                            userRepository.getUserByEmail(userIdentifier)

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
                                    nickname
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