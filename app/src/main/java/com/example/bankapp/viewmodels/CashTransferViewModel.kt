package com.example.bankapp.viewmodels

import AuthorizationViewModel
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
import com.example.bankapp.repositories.CurrencyExchangeRepository
import com.example.bankapp.repositories.TransactionRepository
import com.example.bankapp.repositories.UserRepository
import com.example.bankapp.utilities.ACCOUNT_NUMBER_SIZE
import com.example.bankapp.utilities.CurrencyUtils
import com.example.bankapp.utilities.amountFieldValidator
import com.example.bankapp.utilities.cashTransferAmountRegex
import com.example.bankapp.utilities.emptyTextFieldErrorMessageBuilder
import com.example.bankapp.utilities.invalidNumericalFieldErrorMessageBuilder
import com.example.bankapp.utilities.maxAllowedCharacterErrorMessageBuilder
import com.example.bankapp.utilities.toDbAccNo
import com.example.bankapp.utilities.uiUserId
import kotlinx.coroutines.launch

class CashTransferViewModel(
    sessionState: SessionState.Authenticated.AccountRegistered,
    private val transactionRepository: TransactionRepository,
    private val beneficiaryRepository: BeneficiaryRepository,
    private val accountRepository: AccountRepository,
    private val sharedSessionViewModel: AuthorizationViewModel,
    private val userRepository: UserRepository,
    private val currencyRepository: CurrencyExchangeRepository
) : ViewModel() {

    var exchangeRates by mutableStateOf<Map<String, Double>>(emptyMap())
        private set

    init {
        loadExchangeRates()
    }

    private fun loadExchangeRates() {
        viewModelScope.launch {

            val ratesData = currencyRepository.getLatestRates()
            if (ratesData != null) {
                exchangeRates = ratesData.rates
            }

            lastUpdatedTime = currencyRepository.getLastUpdated()
        }
    }

    val account = sessionState.account

    val user = sessionState.user

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

    var isVerifySuccessful by mutableStateOf(false)
        private set

    var isInternational by mutableStateOf(false)
        private set
    private var recieverCountryCode by mutableStateOf("")

    private var recieverCurrencyCode  by mutableStateOf("")


    var exchangeRate by mutableStateOf(java.math.BigDecimal.ONE)
    var showInternetAlert by mutableStateOf(false)

    var lastUpdatedTime by mutableStateOf("---")
        private set


    val convertedAmountDisplay: String
        get() {
            if (amount.isEmpty() || exchangeRates.isEmpty())
                return ""
            val result = CurrencyUtils.convertCurrency(
                amount = amount,
                rates = exchangeRates,
                baseCountryCode = user.value.countryCode,
                targetCountryCode = recieverCountryCode
            )

            val symbol = CurrencyUtils.getCurrencySymbol(recieverCountryCode)

            return "$result $symbol"
        }

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
        if (accNo.toDbAccNo() == account.value.accNo) {
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

                if(exists){
                val otherUserId = accountRepository.getUserIdByAccNo(accNo.toDbAccNo()).uiUserId
                val otherUser = userRepository.getUserByUserId(otherUserId)
                    isInternational = otherUser!!.countryCode != user.value.countryCode
                    if (isInternational) {
                        recieverCountryCode = otherUser.countryCode
                        val myRate = exchangeRates[CurrencyUtils.getCurrencyCode(user.value.countryCode)] ?: 1.0
                        val targetRate = exchangeRates[CurrencyUtils.getCurrencyCode(otherUser.countryCode)] ?: 1.0
                        exchangeRate = (targetRate / myRate).toBigDecimal()
                    }
                }
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

                    val isFriend = beneficiaryRepository.getBeneficiary(user.value.userId, otherUserId) != null

                    if (isInternational) {
                        val cache = currencyRepository.getLatestRates()
                        if (cache == null) {
                            showInternetAlert = true
                            return@launch
                        }
                        sharedSessionViewModel.initializeInternationalTransfer(
                            fromAccNo = account.value.accNo,
                            toAccNo = accountNumber.toDbAccNo(),
                            amount = amount.toBigDecimal(),
                            baseCurrency = CurrencyUtils.getCurrencySymbol(user.value.countryCode),
                            targetCurrency = CurrencyUtils.getCurrencySymbol(recieverCountryCode),
                            rate = exchangeRate
                        )
                    }
                    else {
                        sharedSessionViewModel.initializeCashTransfer(
                            fromAccNo = account.value.accNo,
                            toAccNo = accountNumber.toDbAccNo(),
                            amount = amount.toBigDecimal(),
                            friend = isFriend,
                            countryCode = user.value.countryCode
                        )
                    }

                    isVerifySuccessful = true
                }
            } catch (_: Exception) {
                submitError = FormError.UnknownError
            } finally {
                isLoading = false
            }
        }
    }

}