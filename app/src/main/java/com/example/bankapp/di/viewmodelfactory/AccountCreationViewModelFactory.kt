package com.example.bankapp.di.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bankapp.entities.SessionState
import com.example.bankapp.repositories.AccountRepository
import com.example.bankapp.repositories.CurrencyExchangeRepository
import com.example.bankapp.repositories.TransactionRepository
import com.example.bankapp.viewmodels.AccountCreationViewModel
import com.example.bankapp.viewmodels.CurrencyConvertorViewModel
import com.example.bankapp.viewmodels.SessionViewModel


class AccountCreationViewModelFactory(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val sessionViewModel: SessionViewModel,
    private val sessionState: SessionState.Authenticated.AccountNotRegistered,
    private val currencyRepository: CurrencyExchangeRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccountCreationViewModel::class.java)) {
            return AccountCreationViewModel(
                accountRepository, sessionState, transactionRepository, sessionViewModel, currencyRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}