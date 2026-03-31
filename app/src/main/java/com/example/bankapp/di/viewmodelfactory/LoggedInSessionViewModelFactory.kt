package com.example.bankapp.di.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bankapp.repositories.AccountRepository
import com.example.bankapp.repositories.UserRepository
import com.example.bankapp.usecases.SharedPreferenceHelper
import com.example.bankapp.viewmodels.LoggedInSessionViewModel

class LoggedInSessionViewModelFactory(
    private val sharedPreferenceHelper: SharedPreferenceHelper,
    private val accountRepository: AccountRepository,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass == LoggedInSessionViewModel::class.java) {

            return LoggedInSessionViewModel(sharedPreferenceHelper, accountRepository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}