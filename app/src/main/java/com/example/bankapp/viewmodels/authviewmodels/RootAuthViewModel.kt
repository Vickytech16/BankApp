package com.example.bankapp.viewmodels.authviewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RootAuthViewModel : ViewModel() {

    data class RootAuthUiState(
        val isAuthenticated: Boolean = false,
        val isPromptActive: Boolean = false,
        val isDeviceSecure: Boolean = true
    )

    private val _uiState = MutableStateFlow(RootAuthUiState())
    val uiState = _uiState.asStateFlow()

    fun setAuthenticated(value: Boolean) {
        _uiState.update { it.copy(isAuthenticated = value, isPromptActive = !value && it.isPromptActive) }
    }

    fun setDeviceSecure(secure: Boolean) {
        _uiState.update { it.copy(isDeviceSecure = secure) }
        if (!secure) setAuthenticated(true)
    }

    fun markPromptActive(active: Boolean) {
        _uiState.update { it.copy(isPromptActive = active) }
    }

    fun canPrompt(): Boolean {
        val state = _uiState.value
        return !state.isAuthenticated && !state.isPromptActive && state.isDeviceSecure
    }
}