package com.example.bankapp.di.viewmodelfactory


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bankapp.repositories.BeneficiaryRepository
import com.example.bankapp.usecases.TransactionSessionHolder
import com.example.bankapp.viewmodels.AddBeneficiaryResultViewModel

@Suppress("UNCHECKED_CAST")
class AddBeneficiaryResultViewModelFactory(
    private val transactionSessionHolder: TransactionSessionHolder,
    private val beneficiaryRepository: BeneficiaryRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddBeneficiaryResultViewModel(
            transactionSessionHolder,
            beneficiaryRepository
        ) as T
    }
}