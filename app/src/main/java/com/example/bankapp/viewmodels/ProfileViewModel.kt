package com.example.bankapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.bankapp.entities.SessionState
import com.example.bankapp.entities.dbtables.Account
import com.example.bankapp.entities.dbtables.User
import com.example.bankapp.utilities.uiAccNo
import com.example.bankapp.utilities.uiUserId
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.repositories.AccountRepository
import com.example.bankapp.repositories.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val accountRepository: AccountRepository,
    private val sessionState: SessionState.Authenticated.AccountRegistered
) : ViewModel() {

    val user = sessionState.user
    val account: StateFlow<Account> = accountRepository
        .getAccountAsFlowByAccountNumber(sessionState.account.accNo).map { it!! }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = sessionState.account
        )

    var isLoading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    var isAccNoVisible by mutableStateOf(false)
    val isDarkMode = mutableStateOf(true)

    fun onAccNoVisibilityChange() {
        isAccNoVisible = !isAccNoVisible
    }

    fun toggleTheme() {
        isDarkMode.value = !isDarkMode.value
    }

    fun getMaskedAccountNo(): String {
        val accNo = account.value.accNo.uiAccNo
        return if (isAccNoVisible) {
            accNo
        } else {
            "**** ${accNo.takeLast(4)}"
        }
    }
}