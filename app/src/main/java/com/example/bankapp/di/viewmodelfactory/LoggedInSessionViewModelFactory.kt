package com.example.bankapp.di.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bankapp.repositories.AccountRepository
import com.example.bankapp.usecases.SessionUseCase
import com.example.bankapp.viewmodels.LoggedInSessionViewModel
import com.example.bankapp.viewmodels.OtpViewModel

class LoggedInSessionViewModelFactory(
    private val sessionUseCase: SessionUseCase,
    private val accountRepository: AccountRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass == LoggedInSessionViewModel::class.java) {

            return LoggedInSessionViewModel(sessionUseCase, accountRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}