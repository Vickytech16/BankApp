package com.example.bankapp.di.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bankapp.entities.SessionState
import com.example.bankapp.repositories.TransactionRepository
import com.example.bankapp.viewmodels.CashTransferViewModel
import com.example.bankapp.viewmodels.DepositViewModel

class DepositViewModelFactory(
    private  val sessionState: SessionState.Authenticated.AccountRegistered,
    private val transactionRepository: TransactionRepository): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DepositViewModel::class.java)) {
            return DepositViewModel(
                sessionState = sessionState,
                transactionRepository = transactionRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}