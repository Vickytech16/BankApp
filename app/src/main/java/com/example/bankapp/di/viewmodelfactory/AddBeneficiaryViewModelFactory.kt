package com.example.bankapp.di.viewmodelfactory

import AuthorizationViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bankapp.entities.SessionState
import com.example.bankapp.repositories.AccountRepository
import com.example.bankapp.repositories.BeneficiaryRepository
import com.example.bankapp.repositories.UserRepository
import com.example.bankapp.viewmodels.AddBeneficiaryViewModel

class AddBeneficiaryViewModelFactory(
    private val userRepository: UserRepository,
    private val beneficiaryRepository: BeneficiaryRepository,
    private val sessionState: SessionState.Authenticated.AccountRegistered,
    private val authorizationViewModel: AuthorizationViewModel,
    private val accountRepository: AccountRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddBeneficiaryViewModel::class.java)) {
            return AddBeneficiaryViewModel(
                userRepository, beneficiaryRepository, sessionState, authorizationViewModel, accountRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}