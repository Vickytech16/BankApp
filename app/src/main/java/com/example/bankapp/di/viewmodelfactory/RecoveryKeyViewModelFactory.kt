package com.example.bankapp.di.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bankapp.entities.ChangePasswordState
import com.example.bankapp.viewmodels.authviewmodels.RecoveryKeyViewModel

class RecoveryKeyViewModelFactory(
    private val changePasswordState: ChangePasswordState
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecoveryKeyViewModel::class.java)) {
            return RecoveryKeyViewModel(
                changePasswordState = changePasswordState
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}