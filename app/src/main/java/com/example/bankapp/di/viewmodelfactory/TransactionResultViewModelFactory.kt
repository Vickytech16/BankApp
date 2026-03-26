package com.example.bankapp.di.viewmodelfactory

import SharedTransactionViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bankapp.entities.SessionState
import com.example.bankapp.repositories.BeneficiaryRepository
import com.example.bankapp.repositories.TransactionRepository
import com.example.bankapp.usecases.SharedPreferenceHelper
import com.example.bankapp.viewmodels.TransactionResultViewModel

class TransactionResultViewModelFactory(
    private val transactionRepository: TransactionRepository,
    private val beneficiaryRepository: BeneficiaryRepository,
    private val sharedTransactionViewModel: SharedTransactionViewModel
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionResultViewModel::class.java)) {
            return TransactionResultViewModel(
                transactionRepository = transactionRepository,
                beneficiaryRepository = beneficiaryRepository,
                sharedTransactionViewModel = sharedTransactionViewModel
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}