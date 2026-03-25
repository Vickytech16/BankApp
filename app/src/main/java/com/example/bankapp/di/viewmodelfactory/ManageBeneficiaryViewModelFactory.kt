package com.example.bankapp.di.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bankapp.entities.SessionState
import com.example.bankapp.repositories.AccountRepository
import com.example.bankapp.repositories.BeneficiaryRepository
import com.example.bankapp.repositories.TransactionRepository
import com.example.bankapp.usecases.SharedPreferenceHelper
import com.example.bankapp.viewmodels.AccountCreationViewModel
import com.example.bankapp.viewmodels.ManageBeneficiaryViewModel

class ManageBeneficiaryViewModelFactory(
private val sessionState: SessionState.Authenticated.AccountRegistered,
private val beneficiaryRepository: BeneficiaryRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ManageBeneficiaryViewModel::class.java)) {
            return ManageBeneficiaryViewModel(
                sessionState, beneficiaryRepository,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}