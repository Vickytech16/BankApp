package com.example.bankapp.di.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bankapp.entities.SessionState
import com.example.bankapp.repositories.BeneficiaryRepository
import com.example.bankapp.viewmodels.PayToBeneficiaryViewModel

class PayToBeneficiaryViewModelFactory(
    private val beneficiaryRepository: BeneficiaryRepository,
    private val sessionState: SessionState.Authenticated.AccountRegistered
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PayToBeneficiaryViewModel::class.java)) {
            return PayToBeneficiaryViewModel(
                beneficiaryRepository = beneficiaryRepository,
                sessionState = sessionState
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
