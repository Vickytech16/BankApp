package com.example.bankapp.di.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bankapp.entities.SessionState
import com.example.bankapp.repositories.CountryRepository
import com.example.bankapp.repositories.CurrencyExchangeRepository
import com.example.bankapp.viewmodels.CurrencyConvertorViewModel


class CurrencyConvertorViewModelFactory(
    private val currencyExchangeRepository: CurrencyExchangeRepository,
    private val sessionState: SessionState.Authenticated.AccountRegistered,
    private val countryRepository: CountryRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CurrencyConvertorViewModel::class.java)) {
            return CurrencyConvertorViewModel(
                currencyExchangeRepository = currencyExchangeRepository,
                sessionState = sessionState,
                countryRepository = countryRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}