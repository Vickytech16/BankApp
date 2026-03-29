package com.example.bankapp.di.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bankapp.repositories.CountryRepository
import com.example.bankapp.repositories.UserRepository
import com.example.bankapp.viewmodels.authviewmodels.LoginViewModel
import com.example.bankapp.viewmodels.authviewmodels.RegisterViewModel

@Suppress("UNCHECKED_CAST")
class RegisterViewModelFactory(
    private val userRepository: UserRepository,
    private val countryRepository: CountryRepository
) : ViewModelProvider.Factory{


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(
                userRepository = userRepository,
                countryRepository = countryRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}