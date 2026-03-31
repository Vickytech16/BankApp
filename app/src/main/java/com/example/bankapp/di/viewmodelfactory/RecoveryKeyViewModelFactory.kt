package com.example.bankapp.di.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bankapp.entities.SessionState
import com.example.bankapp.repositories.AccountRepository
import com.example.bankapp.usecases.ChangePasswordUseCase
import com.example.bankapp.viewmodels.authviewmodels.RecoveryKeyViewModel

class RecoveryKeyViewModelFactory(
    private val changePasswordUseCase: ChangePasswordUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecoveryKeyViewModel::class.java)) {
            return RecoveryKeyViewModel(
                changePasswordUseCase = changePasswordUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}