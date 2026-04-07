package com.example.bankapp.di.viewmodelfactory


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bankapp.viewmodels.AuthorizationViewModel
import com.example.bankapp.viewmodels.authviewmodels.OtpViewModel

class AuthorizationViewModelFactory() : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthorizationViewModel::class.java)) {
            return AuthorizationViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}