package com.example.bankapp.di.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bankapp.entities.SessionState
import com.example.bankapp.repositories.UserRepository
import com.example.bankapp.entities.ChangePasswordState
import com.example.bankapp.viewmodels.ProfileViewModel

@Suppress("UNCHECKED_CAST")
class ProfileViewModelFactory(
    private val userRepository: UserRepository,
    private val sessionState: SessionState.Authenticated.AccountRegistered,
    private val changePasswordState: ChangePasswordState
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProfileViewModel(
            userRepository = userRepository,
            sessionState = sessionState,
            changePasswordState = changePasswordState
        ) as T
    }
}