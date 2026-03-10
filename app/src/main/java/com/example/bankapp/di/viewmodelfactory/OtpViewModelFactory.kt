package com.example.bankapp.di.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bankapp.repositories.UserRepository
import com.example.bankapp.usecases.ChangePasswordUseCase
import com.example.bankapp.viewmodels.ChangePasswordViewModel
import com.example.bankapp.viewmodels.OtpViewModel


class OtpViewModelFactory() : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OtpViewModel::class.java)) {
            return OtpViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}