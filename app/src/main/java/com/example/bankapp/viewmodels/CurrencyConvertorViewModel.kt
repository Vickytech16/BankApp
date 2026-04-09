package com.example.bankapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.entities.SessionState
import com.example.bankapp.entities.dbtables.CurrencyRates
import com.example.bankapp.entities.dtos.Country
import com.example.bankapp.repositories.CountryRepository
import com.example.bankapp.repositories.CurrencyExchangeRepository
import com.example.bankapp.utilities.COUNTRY_MAX_SIZE
import com.example.bankapp.utilities.CurrencyUtils
import com.example.bankapp.utilities.depositAmountRegex
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal

class CurrencyConvertorViewModel(
    private val currencyExchangeRepository: CurrencyExchangeRepository,
    sessionState: SessionState.Authenticated.AccountRegistered,
    countryRepository: CountryRepository
) : ViewModel() {

    var lastUpdated by mutableStateOf<String?>(null)
        private set

    init {
        viewModelScope.launch {
            lastUpdated = currencyExchangeRepository.getLastUpdated()
        }
    }

    private val user = sessionState.user.value

    var converterInput by mutableStateOf("")
        private set

    var exchangeRates by mutableStateOf<CurrencyRates?>(null)
        private set

    var topCurrency by mutableStateOf<Country?>(null)
    var bottomCurrency by mutableStateOf<Country?>(null)

    var showTopSheet by mutableStateOf(false)
    var topSearchQuery by mutableStateOf("")
        private set



    fun ontOpSearchQueryChange(newValue: String){
        if(newValue.length <= COUNTRY_MAX_SIZE)
            topSearchQuery = newValue
    }

    var showBottomSheet by mutableStateOf(false)
    var bottomSearchQuery by mutableStateOf("")
        private set

    fun onBottomSearchQueryChange(newValue: String){
        if(newValue.length <= COUNTRY_MAX_SIZE)
            bottomSearchQuery = newValue
    }

    val countries: StateFlow<List<Country>> = countryRepository.getCountries()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        loadRates()
        observeCountries()
    }

    private fun loadRates() {
        viewModelScope.launch {
            exchangeRates = currencyExchangeRepository.getLatestRates()
        }
    }

    private fun observeCountries() {
        viewModelScope.launch {
            countries.collectLatest { list ->
                if (list.isNotEmpty()) {
                    if (topCurrency == null) topCurrency = list.find { it.countryCode == user.countryCode }
                    if (bottomCurrency == null) bottomCurrency = list.find { it.countryCode == "US" }
                }
            }
        }
    }

    val convertedValue: String
        get() =
            CurrencyUtils.formatCurrency(
            CurrencyUtils.convertCurrency(
            amount = converterInput.toBigDecimalOrNull() ?: BigDecimal.ZERO,
            rates = exchangeRates?.rates,
            baseCountryCode = topCurrency?.countryCode ?: user.countryCode,
            targetCountryCode = bottomCurrency?.countryCode ?: "US"
        ), topCurrency?.countryCode ?: user.countryCode)

    fun onConverterInputChange(newValue: String) {
        if (newValue.isEmpty() || newValue.matches(depositAmountRegex)) {
            converterInput = newValue
        }
    }


    fun onCurrencySwap() {
        val temp = topCurrency
        topCurrency = bottomCurrency
        bottomCurrency = temp
    }

    fun onTopCurrencySelected(country: Country) {
        topCurrency = country
    }
    fun onBottomCurrencySelected(country: Country) {
        bottomCurrency = country
    }
}