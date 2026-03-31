package com.example.bankapp.di.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bankapp.entities.SessionState
import com.example.bankapp.repositories.TransactionRepository
import com.example.bankapp.services.TransactionExportService
import com.example.bankapp.viewmodels.TransactionsViewModel


class TransactionsViewModelFactory(
   private val sessionState: SessionState.Authenticated.AccountRegistered,
   private val transactionRepository: TransactionRepository,
   private val transactionExportService: TransactionExportService
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionsViewModel::class.java)) {
            return TransactionsViewModel(
                sessionState = sessionState,
                transactionRepository = transactionRepository,
                exportService = transactionExportService
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}