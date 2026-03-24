package com.example.bankapp.di.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bankapp.viewmodels.OtpVerificationViewModel

class OtpVerificationViewModelFactory() : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OtpVerificationViewModel::class.java)) {
            return OtpVerificationViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}