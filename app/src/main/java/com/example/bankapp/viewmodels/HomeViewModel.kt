package com.example.bankapp.viewmodels

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.entities.dbtables.Account
import com.example.bankapp.entities.SessionState
import com.example.bankapp.entities.uimodels.AccountUiModel
import com.example.bankapp.repositories.AccountRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    sessionState: SessionState.Authenticated.AccountRegistered,
    accountRepository: AccountRepository
) : ViewModel() {

    val user by mutableStateOf(sessionState.user)

    val username = user.userName
    val countryCode = user.countryCode
    val account: StateFlow<AccountUiModel> = accountRepository
        .getAccountAsFlowByAccountNumber(sessionState.account.accNo).map { it!! }
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

    var showLogoutDialog by mutableStateOf(false)

    fun onLogoutClickChange(newValue: Boolean) {
        showLogoutDialog = newValue
    }

    var drawerState by mutableStateOf(DrawerValue.Closed)
}
