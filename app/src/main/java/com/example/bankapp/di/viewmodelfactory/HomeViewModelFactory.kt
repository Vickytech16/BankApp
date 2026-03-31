package com.example.bankapp.di.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bankapp.entities.SessionState
import com.example.bankapp.repositories.AccountRepository
import com.example.bankapp.viewmodels.HomeViewModel

class HomeViewModelFactory(
    private val sessionState: SessionState.Authenticated.AccountRegistered,
    private val accountRepository: AccountRepository
    ) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(
                sessionState = sessionState
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}