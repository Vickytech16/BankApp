package com.example.bankapp.di.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bankapp.repositories.AccountRepository
import com.example.bankapp.repositories.TransactionRepository
import com.example.bankapp.repositories.UserRepository
import com.example.bankapp.usecases.ChangePasswordUseCase
import com.example.bankapp.usecases.SessionUseCase
import com.example.bankapp.viewmodels.AccountCreationViewModel
import com.example.bankapp.viewmodels.ChangePasswordViewModel


class AccountCreationViewModelFactory(
    private val accountRepository: AccountRepository,
    private val sessionUseCase: SessionUseCase,
    private val transactionRepository: TransactionRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccountCreationViewModel::class.java)) {
            return AccountCreationViewModel(
                accountRepository, sessionUseCase, transactionRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}