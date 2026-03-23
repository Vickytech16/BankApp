package com.example.bankapp.di.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bankapp.entities.SessionState
import com.example.bankapp.repositories.TransactionRepository
import com.example.bankapp.usecases.TransactionSessionHolder
import com.example.bankapp.viewmodels.PasswordConfirmationViewModel
import com.example.bankapp.viewmodels.TransactionsViewModel

class PasswordConfirmationViewModelFactory(
    private val sessionState: SessionState.Authenticated.AccountRegistered,

) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PasswordConfirmationViewModel::class.java)) {
            return PasswordConfirmationViewModel(
                sessionState = sessionState,
//                transactionSessionHolder = transactionSessionHolder
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}