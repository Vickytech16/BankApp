package com.example.bankapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.R
import com.example.bankapp.entities.SessionState
import com.example.bankapp.entities.errors.FormError
import com.example.bankapp.entities.types.account.AccountStatus
import com.example.bankapp.repositories.AccountRepository
import com.example.bankapp.repositories.BeneficiaryRepository
import com.example.bankapp.repositories.TransactionRepository
import com.example.bankapp.di.providers.HomeSessionHandlerProvider
import com.example.bankapp.usecases.CurrentSessionIntent
import com.example.bankapp.usecases.HomeSessionHandler
import com.example.bankapp.utilities.ACCOUNT_NUMBER_SIZE
import com.example.bankapp.utilities.amountFieldValidator
import com.example.bankapp.utilities.cashTransferAmountRegex
import com.example.bankapp.utilities.emptyTextFieldErrorMessageBuilder
import com.example.bankapp.utilities.invalidNumericalFieldErrorMessageBuilder
import com.example.bankapp.utilities.maxAllowedCharacterErrorMessageBuilder
import com.example.bankapp.utilities.toDbAccNo
import kotlinx.coroutines.launch

class CashTransferViewModel(
    private val sessionState: SessionState.Authenticated.AccountRegistered,
    private val transactionRepository: TransactionRepository,
    private val beneficiaryRepository: BeneficiaryRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val account = sessionState.account

    private val homeSessionHandler: HomeSessionHandler
        get() = HomeSessionHandlerProvider.currentHandler

    private val cashTransfer: HomeSessionHandler.CashTransfer
        get() = homeSessionHandler as HomeSessionHandler.CashTransfer


    var accountNumber by mutableStateOf("")
        private set

    var accountNumberError by mutableStateOf<FormError?>(null)
        private set

    var accountExistsStatus by mutableStateOf<AccountStatus?>(null)
        private set

    var amount by mutableStateOf("")
        private set

    var amountError by mutableStateOf<FormError?>(null)
        private set

    var submitError by mutableStateOf<FormError?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var isNavigationSet by mutableStateOf(false)


    var isVerifySuccessful by mutableStateOf(false)
        private set

    fun onFriendPay(accNo: String){
        accountNumber = accNo
        accountNumberError = null
        checkAccountExists(accNo)
        isVerifySuccessful = false
    }

    fun onAccountNumberChange(newAccountNumber: String) {
        if (newAccountNumber.length <= ACCOUNT_NUMBER_SIZE)
            accountNumber = newAccountNumber
        accountNumberError =
            newAccountNumber.emptyTextFieldErrorMessageBuilder(R.string.account_number) ?:
                    newAccountNumber.maxAllowedCharacterErrorMessageBuilder(R.string.account_number, ACCOUNT_NUMBER_SIZE) ?:
                    newAccountNumber.invalidNumericalFieldErrorMessageBuilder(R.string.account_number)
        onSubmitErrorReset()

        if (newAccountNumber.length == ACCOUNT_NUMBER_SIZE && accountNumberError == null) {
            checkAccountExists(newAccountNumber)
        } else {
            accountExistsStatus = null
        }
    }

    private fun checkAccountExists(accNo: String) {
        if(accNo.isEmpty() || accNo.isBlank() || accNo=="0"){
            accountExistsStatus = AccountStatus.EMPTY
            return
        }
        if (accNo.toDbAccNo() == account.accNo) {
            accountExistsStatus = AccountStatus.SAME_ACCOUNT
            return
        }
        viewModelScope.launch {
            try {
                val exists = transactionRepository.checkIfAccountExists(accNo.toDbAccNo())
                accountExistsStatus =
                    if (exists)
                        AccountStatus.EXISTS
                    else
                        AccountStatus.NOT_FOUND
            } catch (e: Exception) {
                accountExistsStatus = AccountStatus.ERROR
            }
        }
    }

    fun onAmountChange(newAmount: String) {
        if (newAmount.isEmpty() || newAmount.matches(cashTransferAmountRegex))
            amount = newAmount
        amountError =
            newAmount.emptyTextFieldErrorMessageBuilder(R.string.amount_field_name) ?:
                    newAmount.amountFieldValidator(cashTransferAmountRegex)
        onSubmitErrorReset()
    }

    private fun onSubmitErrorReset() {
        if (amountError == null && accountNumberError == null) {
            submitError = null
        }
    }

    fun onCredentialsSubmit() {
        if (isLoading)
            return

        onAmountChange(amount)
        onAccountNumberChange(accountNumber)


        if(accountNumber.length<12 && accountNumber.isNotEmpty()){
            accountExistsStatus = AccountStatus.NOT_FOUND
        }
        onSubmitErrorReset()

        viewModelScope.launch {
            try {
                isLoading = true
                if (amountError != null || accountNumberError != null || accountExistsStatus != AccountStatus.EXISTS) {
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
                    val otherUserId = accountRepository.getUserIdByAccNo(accountNumber.toDbAccNo())

                    val isFriend = beneficiaryRepository.getBeneficiary(sessionState.user.userId, otherUserId) != null

                    cashTransfer.onInitialize(
                        sessionState.account.accNo,
                        accountNumber.toDbAccNo(),
                        convertedAmount,
                        isFriend
                    )

                    cashTransfer.intent = CurrentSessionIntent.CASH_TRANSFER

                    isVerifySuccessful = true
                }
            } catch (_: Exception) {
                submitError = FormError.UnknownError
            } finally {
                isLoading = false
            }
        }
    }

    fun resetScreenState() {
        accountNumber = ""
        amount = ""
        accountNumberError = null
        amountError = null
        submitError = null
        accountExistsStatus = null
        isVerifySuccessful = false
        isLoading = false
    }
}