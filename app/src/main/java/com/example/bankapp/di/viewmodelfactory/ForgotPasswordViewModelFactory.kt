package com.example.bankapp.di.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bankapp.repositories.UserRepository
import com.example.bankapp.entities.ChangePasswordState
import com.example.bankapp.viewmodels.authviewmodels.ForgotPasswordViewModel


class ForgotPasswordViewModelFactory(
    private val userRepository: UserRepository,
    private val changePasswordState: ChangePasswordState
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ForgotPasswordViewModel::class.java)) {
            return ForgotPasswordViewModel(userRepository, changePasswordState) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}