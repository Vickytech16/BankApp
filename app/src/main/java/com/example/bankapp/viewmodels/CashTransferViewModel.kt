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
import com.example.bankapp.repositories.*
import com.example.bankapp.utilities.*
import kotlinx.coroutines.launch
import java.math.BigDecimal

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
    var lastUpdatedTime by mutableStateOf("---")
        private set
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
    private var receiverCountryCode by mutableStateOf("")
    var exchangeRate by mutableStateOf(BigDecimal.ONE)
    var showInternetAlert by mutableStateOf(false)
    var recipientName by mutableStateOf("")
        private set

    init {
        loadExchangeRates()
    }

    private fun loadExchangeRates() {
        viewModelScope.launch {
            val ratesData = currencyRepository.getLatestRates()
            if (ratesData != null) exchangeRates = ratesData.rates
            lastUpdatedTime = currencyRepository.getLastUpdated()
        }
    }

    val convertedAmountDisplay: String
        get() {
            if (amount.isEmpty() || exchangeRates.isEmpty()) return ""
            val result = CurrencyUtils.convertCurrency(amount, exchangeRates, user.value.countryCode, receiverCountryCode)
            val symbol = CurrencyUtils.getCurrencySymbol(receiverCountryCode)
            return "$result $symbol"
        }

    fun onFriendPay(accNo: String) {
        accountNumber = accNo
        accountNumberError = null
        checkAccountExists(accNo)
        isVerifySuccessful = false
    }

    fun onAccountNumberChange(newAccountNumber: String) {
        if (newAccountNumber.length <= ACCOUNT_NUMBER_SIZE) accountNumber = newAccountNumber
        accountNumberError = newAccountNumber.emptyTextFieldErrorMessageBuilder(R.string.account_number)
            ?: newAccountNumber.maxAllowedCharacterErrorMessageBuilder(R.string.account_number, ACCOUNT_NUMBER_SIZE)
                    ?: newAccountNumber.invalidNumericalFieldErrorMessageBuilder(R.string.account_number)

        if (newAccountNumber.length == ACCOUNT_NUMBER_SIZE && accountNumberError == null) {
            checkAccountExists(newAccountNumber)
        } else {
            accountExistsStatus = null
            recipientName = ""
        }
    }

    private fun checkAccountExists(accNo: String) {
        if (accNo.isEmpty() || accNo == "0") {
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
                accountExistsStatus = if (exists) AccountStatus.EXISTS else AccountStatus.NOT_FOUND
                if (exists) {
                    val otherUserId = accountRepository.getUserIdByAccNo(accNo.toDbAccNo()).uiUserId
                    val otherUser = userRepository.getUserByUserId(otherUserId)
                    val beneficiary = beneficiaryRepository.getBeneficiary(user.value.userId, otherUserId.toDbUserId())

                    recipientName = if (!beneficiary?.nickname.isNullOrEmpty()) beneficiary?.nickname!! else otherUser?.userName ?: ""

                    isInternational = otherUser!!.countryCode != user.value.countryCode
                    if (isInternational) {
                        receiverCountryCode = otherUser.countryCode
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

    fun onRecipientSubmit(onSuccess: () -> Unit) {
        if (accountExistsStatus != AccountStatus.EXISTS) return

        viewModelScope.launch {
            try {
                val otherUserId = accountRepository.getUserIdByAccNo(accountNumber.toDbAccNo()).uiUserId
                val beneficiary = beneficiaryRepository.getBeneficiary(user.value.userId, otherUserId.toDbUserId())
                val isFriend = beneficiary != null

                recipientName = if (!beneficiary?.nickname.isNullOrEmpty()) beneficiary?.nickname!! else recipientName

                if (isInternational) {
                    sharedSessionViewModel.initializeInternationalTransfer(
                        fromAccNo = account.value.accNo,
                        toAccNo = accountNumber.toDbAccNo(),
                        isFriend = isFriend,
                        amount = BigDecimal.ZERO,
                        baseCurrency = CurrencyUtils.getCurrencySymbol(user.value.countryCode),
                        targetCurrency = CurrencyUtils.getCurrencySymbol(receiverCountryCode),
                        rate = exchangeRate
                    )
                } else {
                    sharedSessionViewModel.initializeCashTransfer(
                        fromAccNo = account.value.accNo,
                        toAccNo = accountNumber.toDbAccNo(),
                        amount = BigDecimal.ZERO,
                        friend = isFriend,
                        countryCode = user.value.countryCode
                    )
                }
                onSuccess()
            } catch (_: Exception) {}
        }
    }

    fun onAmountChange(newAmount: String) {
        if (newAmount.isEmpty() || newAmount.matches(cashTransferAmountRegex)) amount = newAmount
        amountError = newAmount.emptyTextFieldErrorMessageBuilder(R.string.amount_field_name)
            ?: newAmount.amountFieldValidator(cashTransferAmountRegex)
    }

    fun onCredentialsSubmit() {
        if (isLoading) return
        onAmountChange(amount)

        viewModelScope.launch {
            try {
                isLoading = true
                if (amountError != null || accountExistsStatus != AccountStatus.EXISTS) {
                    submitError = FormError.InvalidData
                    return@launch
                }

                val currentAmount = amount.toBigDecimalOrNull() ?: BigDecimal.ZERO
                if (currentAmount <= BigDecimal.ZERO) {
                    amountError = FormError.InvalidAmount
                    return@launch
                }

                val otherUserId = accountRepository.getUserIdByAccNo(accountNumber.toDbAccNo()).uiUserId
                val beneficiary = beneficiaryRepository.getBeneficiary(user.value.userId, otherUserId.toDbUserId())
                val isFriend = beneficiary != null

                recipientName = if (!beneficiary?.nickname.isNullOrEmpty()) beneficiary.nickname else recipientName

                if (isInternational) {
                    if (currencyRepository.getLatestRates() == null) {
                        showInternetAlert = true
                        return@launch
                    }
                    sharedSessionViewModel.initializeInternationalTransfer(
                        fromAccNo = account.value.accNo,
                        toAccNo = accountNumber.toDbAccNo(),
                        isFriend = isFriend,
                        amount = currentAmount,
                        baseCurrency = CurrencyUtils.getCurrencySymbol(user.value.countryCode),
                        targetCurrency = CurrencyUtils.getCurrencySymbol(receiverCountryCode),
                        rate = exchangeRate
                    )
                } else {
                    sharedSessionViewModel.initializeCashTransfer(
                        fromAccNo = account.value.accNo,
                        toAccNo = accountNumber.toDbAccNo(),
                        amount = currentAmount,
                        friend = isFriend,
                        countryCode = user.value.countryCode
                    )
                }
                isVerifySuccessful = true
            } catch (_: Exception) {
                submitError = FormError.UnknownError
            } finally {
                isLoading = false
            }
        }
    }
}