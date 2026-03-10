package com.example.bankapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.entities.SessionState
import com.example.bankapp.repositories.TransactionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class TransactionsViewModel(
    val sessionState: SessionState.Authenticated.AccountRegistered,
    val transactionRepository: TransactionRepository
): ViewModel() {
    val transactions =
        transactionRepository.getAllTransactions(accNo = sessionState.account.accNo)
        .stateIn(
            scope = viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
}