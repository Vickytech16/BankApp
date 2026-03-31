package com.example.bankapp.di.viewmodelfactory

import AuthorizationViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bankapp.entities.SessionState
import com.example.bankapp.repositories.AccountRepository
import com.example.bankapp.repositories.BeneficiaryRepository
import com.example.bankapp.repositories.CurrencyExchangeRepository
import com.example.bankapp.repositories.TransactionRepository
import com.example.bankapp.repositories.UserRepository
import com.example.bankapp.viewmodels.CashTransferViewModel

class CashTransferViewModelFactory(
    private  val sessionState: SessionState.Authenticated.AccountRegistered,
    private val transactionRepository: TransactionRepository,
    private val beneficiaryRepository: BeneficiaryRepository,
    private val accountRepository: AccountRepository,
    private val authorizationViewModel: AuthorizationViewModel,
    private  val userRepository: UserRepository,
    private val currencyExchangeRepository: CurrencyExchangeRepository

    ): ViewModelProvider.Factory {
@Suppress("UNCHECKED_CAST")
override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(CashTransferViewModel::class.java)) {
        return CashTransferViewModel(
            sessionState = sessionState,
            transactionRepository = transactionRepository,
            beneficiaryRepository = beneficiaryRepository,
            accountRepository = accountRepository,
            authorizationViewModel,
            userRepository,
            currencyExchangeRepository
        ) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
}
}