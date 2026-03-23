package com.example.bankapp.di.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bankapp.entities.SessionState
import com.example.bankapp.repositories.AccountRepository
import com.example.bankapp.repositories.UserRepository
import com.example.bankapp.viewmodels.ProfileViewModel

class ProfileViewModelFactory(
    private val userRepository: UserRepository,
    private val accountRepository: AccountRepository,
    private val sessionState: SessionState.Authenticated.AccountRegistered
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProfileViewModel(
            userRepository = userRepository,
            accountRepository = accountRepository,
            sessionState = sessionState
        ) as T
    }
}