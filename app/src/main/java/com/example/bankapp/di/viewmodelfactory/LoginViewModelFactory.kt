package com.example.bankapp.di.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bankapp.repositories.UserRepository
import com.example.bankapp.usecases.SharedPreferenceHelper
import com.example.bankapp.viewmodels.LoginViewModel


class LoginViewModelFactory(
    private val userRepository: UserRepository,
    private val sharedPreferenceHelper: SharedPreferenceHelper,

    ) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(
                userRepository = userRepository,
                sharedPreferenceHelper = sharedPreferenceHelper
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}