package com.example.bankapp.di.viewmodelfactory

import SharedTransactionViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bankapp.entities.SessionState
import com.example.bankapp.repositories.TransactionRepository
import com.example.bankapp.viewmodels.authviewmodels.DepositViewModel

class DepositViewModelFactory(
    private  val sessionState: SessionState.Authenticated.AccountRegistered,
    private val sharedTransactionViewModel: SharedTransactionViewModel
    ): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DepositViewModel::class.java)) {
            return DepositViewModel(
                sessionState = sessionState,
                sharedTransactionViewModel = sharedTransactionViewModel
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}