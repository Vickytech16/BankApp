package com.example.bankapp.di.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bankapp.repositories.BeneficiaryRepository
import com.example.bankapp.repositories.TransactionRepository
import com.example.bankapp.viewmodels.AuthorizationViewModel
import com.example.bankapp.viewmodels.ResultViewModel

class TransactionResultViewModelFactory(
    private val transactionRepository: TransactionRepository,
    private val beneficiaryRepository: BeneficiaryRepository,
    private val authorizationViewModel: AuthorizationViewModel
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ResultViewModel::class.java)) {
            return ResultViewModel(
                transactionRepository = transactionRepository,
                beneficiaryRepository = beneficiaryRepository,
                authorizationViewModel = authorizationViewModel
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}