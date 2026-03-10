package com.example.bankapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.entities.Account
import com.example.bankapp.entities.SessionState
import com.example.bankapp.repositories.AccountRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    private val sessionState: SessionState.Authenticated.AccountRegistered,
    private val accountRepository: AccountRepository
) : ViewModel() {

    val user by mutableStateOf(sessionState.user)

    val username = user.userName
    val account: StateFlow<Account> = accountRepository
        .getAccountFlowByAccountNumber(sessionState.account.accNo).map { it!! }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = sessionState.account
        )

    var isBalanceVisible by mutableStateOf(false)
        private set

    fun onIsBalanceVisibleChange() {
        isBalanceVisible = !isBalanceVisible
    }
}
