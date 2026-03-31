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
import com.example.bankapp.utilities.CurrencyUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CurrencyConvertorViewModel(
    private val currencyExchangeRepository: CurrencyExchangeRepository,
    private val sessionState: SessionState.Authenticated.AccountRegistered,
    private val countryRepository: CountryRepository
) : ViewModel() {

    private val user = sessionState.user.value

    var converterInput by mutableStateOf("")
        private set

    var exchangeRates by mutableStateOf<CurrencyRates?>(null)
        private set

    var topCurrency by mutableStateOf<Country?>(null)
    var bottomCurrency by mutableStateOf<Country?>(null)

    var showTopSheet by mutableStateOf(false)
    var topSearchQuery by mutableStateOf("")

    var showBottomSheet by mutableStateOf(false)
    var bottomSearchQuery by mutableStateOf("")

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
        get() = CurrencyUtils.convertCurrency(
            amount = converterInput,
            rates = exchangeRates?.rates,
            baseCountryCode = topCurrency?.countryCode ?: user.countryCode,
            targetCountryCode = bottomCurrency?.countryCode ?: "US"
        )

    fun onConverterInputChange(newValue: String) {
        if (newValue.isEmpty() || newValue.all { it.isDigit() || it == '.' }) {
            if (newValue.count { it == '.' } <= 1) {
                converterInput = newValue
            }
        }
    }

    fun onCurrencySwap() {
        val temp = topCurrency
        topCurrency = bottomCurrency
        bottomCurrency = temp
    }

    fun onTopCurrencySelected(country: Country) { topCurrency = country }
    fun onBottomCurrencySelected(country: Country) { bottomCurrency = country }
}