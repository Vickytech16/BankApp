package com.example.bankapp.di.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bankapp.entities.SessionState
import com.example.bankapp.viewmodels.SessionViewModel
import com.example.bankapp.viewmodels.PasswordConfirmationViewModel

class PasswordConfirmationViewModelFactory(
    private val sessionState: SessionState.Authenticated.AccountRegistered,
    private val sessionViewModel: SessionViewModel
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PasswordConfirmationViewModel::class.java)) {
            return PasswordConfirmationViewModel(
                sessionState = sessionState,
                sessionViewModel
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}