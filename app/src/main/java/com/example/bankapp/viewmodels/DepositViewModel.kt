package com.example.bankapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.R
import com.example.bankapp.entities.SessionState
import com.example.bankapp.entities.errors.FormError
import com.example.bankapp.utilities.amountFieldValidator
import com.example.bankapp.utilities.depositAmountRegex
import com.example.bankapp.utilities.emptyTextFieldErrorMessageBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DepositViewModel(
    sessionState: SessionState.Authenticated.AccountRegistered,
    private val authorizationViewModel: AuthorizationViewModel
): ViewModel() {

    val account = sessionState.account.value

    val user = sessionState.user.value

    var amount by mutableStateOf("")
        private set

    var amountError by mutableStateOf<FormError?>(null)
        private set

    fun onAmountChange(newAmount: String) {
        if (newAmount.isEmpty() || newAmount.matches(depositAmountRegex))
            amount = newAmount
        amountError =
            newAmount.emptyTextFieldErrorMessageBuilder(R.string.amount_field_name)
                ?: newAmount.amountFieldValidator(depositAmountRegex)
        onSubmitErrorReset()
    }

    var submitError by mutableStateOf<FormError?>(null)
        private set

    fun onSubmitErrorReset() {
        if (amountError == null) {
            submitError = null
        }
    }

    var isLoading by mutableStateOf(false)
        private set

    var isVerifySuccessful by mutableStateOf(false)
        private set

    fun onSubmit() {
        if (isLoading)
            return

        onAmountChange(amount)
        onSubmitErrorReset()

        viewModelScope.launch(Dispatchers.IO) {

            try {
                isLoading = true
                if (amountError != null) {
                    submitError = FormError.InvalidData
                    return@launch
                }

                val convertedAmount = amount.toBigDecimalOrNull()
                if (convertedAmount == null) {
                    amountError = FormError.InvalidAmount
                    submitError = FormError.InvalidData
                    return@launch
                }
                if (submitError == null) {
                    authorizationViewModel.initializeDeposit(accNo = account.accNo, amount = convertedAmount, countryCode = user.countryCode)
                    isVerifySuccessful = true
                }
            } catch (e: Exception) {
                submitError = FormError.UnknownError
            } finally {
                isLoading = false
            }

        }
    }
}