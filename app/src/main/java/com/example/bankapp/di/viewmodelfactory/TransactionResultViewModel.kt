package com.example.bankapp.di.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bankapp.entities.SessionState
import com.example.bankapp.repositories.BeneficiaryRepository
import com.example.bankapp.repositories.TransactionRepository
import com.example.bankapp.usecases.TransactionSessionHolder
import com.example.bankapp.viewmodels.TransactionResultViewModel
import com.example.bankapp.viewmodels.TransactionsViewModel

class TransactionResultViewModelFactory(
    private val sessionState: SessionState.Authenticated.AccountRegistered,
    private val transactionRepository: TransactionRepository,
    private val beneficiaryRepository: BeneficiaryRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionResultViewModel::class.java)) {
            return TransactionResultViewModel(
                sessionState = sessionState,
                transactionRepository = transactionRepository,
                beneficiaryRepository = beneficiaryRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}