package com.example.bankapp.viewmodels

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
import com.example.bankapp.repositories.AccountRepository
import com.example.bankapp.utilities.EMAIL_MAX_SIZE
import com.example.bankapp.utilities.USERNAME_MAX_SIZE
import com.example.bankapp.utilities.emptyTextFieldErrorMessageBuilder
import com.example.bankapp.utilities.invalidUserNameErrorMessageBuilder
import com.example.bankapp.utilities.maxAllowedCharacterErrorMessageBuilder
import kotlinx.coroutines.launch
import java.math.BigDecimal

class AddBeneficiaryViewModel(
    private val userRepository: UserRepository,
    private val beneficiaryRepository: BeneficiaryRepository,
    sessionState: SessionState.Authenticated.AccountRegistered,
    private val authorizationViewModel: AuthorizationViewModel,
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val user = sessionState.user

    var userIdentifier by mutableStateOf("")
        private set

    var userIdentifierError by mutableStateOf<FormError?>(null)
        private set

    var nickname by mutableStateOf("")
        private set

    var nicknameError by mutableStateOf<FormError?>(null)
        private set

    var submitError by mutableStateOf<FormError?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var isVerificationSuccessful by mutableStateOf(false)
        private set

    var showPhoneTip by mutableStateOf(false)
        private set

    fun onIdentifierChange(newIdentifier: String) {
        if (newIdentifier.length <= EMAIL_MAX_SIZE) {
            userIdentifier = newIdentifier
        }

        showPhoneTip = newIdentifier.isNotEmpty() &&
                newIdentifier.all { it.isDigit() } &&
                !newIdentifier.startsWith("+")

        userIdentifierError = newIdentifier.emptyTextFieldErrorMessageBuilder(R.string.generic_field_name) ?:
                newIdentifier.maxAllowedCharacterErrorMessageBuilder(R.string.generic_field_name, EMAIL_MAX_SIZE)

        resetSubmitError()
    }

    fun onNickNameChange(newNickName: String) {
        if (newNickName.length <= USERNAME_MAX_SIZE) {
            nickname = newNickName
        }

        if(newNickName.isNotEmpty()) {
            nicknameError = nickname.maxAllowedCharacterErrorMessageBuilder(
                R.string.nickname_optional_field_name,
                USERNAME_MAX_SIZE
            ) ?: nickname.invalidUserNameErrorMessageBuilder()
        }

        resetSubmitError()
    }

    private fun resetSubmitError() {
        submitError = null
    }

    fun onSubmit() {
        if (isLoading) return

        onIdentifierChange(userIdentifier)
        onNickNameChange(nickname)

        if (userIdentifierError != null || nicknameError != null) return

        viewModelScope.launch {
            try {
                isLoading = true

                if (userIdentifier.isBlank()) {
                    submitError = FormError.AllFieldsAreRequired
                    return@launch
                }

                if (userIdentifier == user.value.email || userIdentifier == user.value.phoneNumber) {
                    submitError = FormError.YouAreTheUser
                    return@launch
                }

                val currentBeneficiaries = beneficiaryRepository.getAllBeneficiariesForUser(user.value.userId)
                if (currentBeneficiaries.size >= 3) {
                    submitError = FormError.BeneficiaryLimitReached(3)
                    return@launch
                }
                var checkedByPhone = false
                var friend: User? = if (userIdentifier.any { it.isDigit() } || userIdentifier.startsWith("+")) {
                    checkedByPhone = true
                    userRepository.getUserByPhoneNumber(userIdentifier)
                } else {
                    userRepository.getUserByEmail(userIdentifier)
                }

                if (checkedByPhone && friend == null) {
                    friend = userRepository.getUserByEmail(userIdentifier)
                }

                if (friend == null) {
                    submitError = FormError.InvalidCredentials
                    return@launch
                }

                val existing = beneficiaryRepository.getBeneficiary(user.value.userId, friend.userId)
                if (existing != null) {
                    submitError = FormError.AlreadyYourFriendError
                    return@launch
                }

                val friendAccounts = accountRepository.getAccountByUserId(friend.userId)
                if (friendAccounts.isEmpty()) {
                    submitError = FormError.UserDoesNotHaveAccountError
                    return@launch
                }

                authorizationViewModel.initializeAddBeneficiary(
                    fromUserId = user.value.userId,
                    toUserId = friend.userId,
                    nickname = nickname
                )

                isVerificationSuccessful = true

            } catch (e: Exception) {
                submitError = FormError.UnknownError
            } finally {
                isLoading = false
            }
        }
    }

    fun clearState() {
        userIdentifier = ""
        userIdentifierError = null
        nickname = ""
        nicknameError = null
        submitError = null
        isVerificationSuccessful = false
    }
}