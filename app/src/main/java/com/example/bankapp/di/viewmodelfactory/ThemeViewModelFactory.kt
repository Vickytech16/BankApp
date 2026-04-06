package com.example.bankapp.di.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bankapp.utilities.SharedPreferenceHelper
import com.example.bankapp.viewmodels.ThemeViewModel

class ThemeViewModelFactory(private val sharedPreferenceHelper: SharedPreferenceHelper) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ThemeViewModel::class.java)) {
            return ThemeViewModel(sharedPreferenceHelper = sharedPreferenceHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}