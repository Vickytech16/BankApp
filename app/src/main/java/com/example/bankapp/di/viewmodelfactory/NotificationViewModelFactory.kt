package com.example.bankapp.di.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bankapp.viewmodels.NotificationViewmodel
import com.example.bankapp.viewmodels.OtpViewModel


class NotificationViewModelFactory() : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationViewmodel::class.java)) {
            return NotificationViewmodel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}