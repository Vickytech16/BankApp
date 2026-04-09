package com.example.bankapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.R
import com.example.bankapp.entities.SessionState
import com.example.bankapp.entities.errors.FormError
import com.example.bankapp.repositories.CurrencyExchangeRepository
import com.example.bankapp.utilities.CurrencyUtils
import com.example.bankapp.utilities.amountFieldValidator
import com.example.bankapp.utilities.depositAmountRegex
import com.example.bankapp.utilities.emptyTextFieldErrorMessageBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigDecimal

class DepositViewModel(
    sessionState: SessionState.Authenticated.AccountRegistered,
    private val authorizationViewModel: AuthorizationViewModel,
    private val currencyRepository: CurrencyExchangeRepository
) : ViewModel() {

    val account = sessionState.account.value
    val user = sessionState.user.value

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

    private var exchangeRates by mutableStateOf<Map<String, Double>>(emptyMap())

    init {
        loadRates()
    }

    private fun loadRates() {
        viewModelScope.launch {
            val ratesData = currencyRepository.getLatestRates()
            if (ratesData != null) exchangeRates = ratesData.rates
        }
    }

    private fun getAvailableRoom(): BigDecimal {
        val currentBalance = account.balance

        val maxUsd = account.accountType.maxBalanceLimit
        val myRate = exchangeRates[CurrencyUtils.getCurrencyCode(user.countryCode)] ?: 1.0
        val maxLocal = maxUsd.multiply(BigDecimal.valueOf(myRate))

        return (maxLocal - currentBalance).coerceAtLeast(BigDecimal.ZERO)
    }

    fun onAmountChange(newAmount: String) {
        if (newAmount.length > 12) return

        if (newAmount.isEmpty() || newAmount.matches(depositAmountRegex)) {
            amount = newAmount
        }

        val room = getAvailableRoom()
        val inputVal = newAmount.toBigDecimalOrNull() ?: BigDecimal.ZERO

        amountError = when {
            newAmount.isEmpty() -> newAmount.emptyTextFieldErrorMessageBuilder(R.string.amount_field_name)
            inputVal > room -> {
                val formattedRoom = CurrencyUtils.formatCurrency(room, user.countryCode) + " " + CurrencyUtils.getCurrencyCode(user.countryCode)
                FormError.LimitExceededDeposit(formattedRoom)
            }
            else -> newAmount.amountFieldValidator(depositAmountRegex)
        }

        if (amountError == null) submitError = null
    }

    fun onSubmit() {
        if (isLoading) return

        onAmountChange(amount)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                isLoading = true
                if (amountError != null) {
                    submitError = FormError.InvalidData
                    return@launch
                }

                val convertedAmount = amount.toBigDecimalOrNull()
                if (convertedAmount == null || convertedAmount <= BigDecimal.ZERO) {
                    amountError = FormError.InvalidAmount
                    return@launch
                }

                authorizationViewModel.initializeDeposit(
                    accNo = account.accNo,
                    amount = convertedAmount,
                    countryCode = user.countryCode
                )
                isVerifySuccessful = true

            } catch (e: Exception) {
                submitError = FormError.UnknownError
            } finally {
                isLoading = false
            }
        }
    }
}