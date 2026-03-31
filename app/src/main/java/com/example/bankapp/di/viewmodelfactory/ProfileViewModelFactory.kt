package com.example.bankapp.di.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bankapp.entities.SessionState
import com.example.bankapp.repositories.UserRepository
import com.example.bankapp.usecases.ChangePasswordUseCase
import com.example.bankapp.viewmodels.ProfileViewModel

@Suppress("UNCHECKED_CAST")
class ProfileViewModelFactory(
    private val userRepository: UserRepository,
    private val sessionState: SessionState.Authenticated.AccountRegistered,
    private val changePasswordUseCase: ChangePasswordUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProfileViewModel(
            userRepository = userRepository,
            sessionState = sessionState,
            changePasswordUseCase = changePasswordUseCase
        ) as T
    }
}