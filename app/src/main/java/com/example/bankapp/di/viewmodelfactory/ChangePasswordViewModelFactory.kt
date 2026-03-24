package com.example.bankapp.di.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bankapp.repositories.UserRepository
import com.example.bankapp.usecases.ChangePasswordUseCase
import com.example.bankapp.viewmodels.ChangePasswordViewModel


class ChangePasswordViewModelFactory(
        private val userRepository: UserRepository,
        private val changePasswordUseCase: ChangePasswordUseCase
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ChangePasswordViewModel::class.java)) {
                return ChangePasswordViewModel(
                    userRepository = userRepository,
                    changePasswordUseCase = changePasswordUseCase
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

