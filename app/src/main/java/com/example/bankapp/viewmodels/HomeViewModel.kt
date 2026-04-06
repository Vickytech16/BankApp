package com.example.bankapp.viewmodels

import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.entities.AccountVelocityStatus
import com.example.bankapp.entities.SessionState
import com.example.bankapp.repositories.TransactionRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    sessionState: SessionState.Authenticated.AccountRegistered,
    transactionRepository: TransactionRepository
) : ViewModel() {

    val user = sessionState.user
    val account = sessionState.account

    var velocityStatus by mutableStateOf<AccountVelocityStatus?>(null)
        private set

    init {
        viewModelScope.launch {
            velocityStatus = transactionRepository.getAccountVelocityStatus(account.value.accNo, user.value)
        }
    }

    var currentCardPage by mutableIntStateOf(0)
        private set

    fun onNextCard() { currentCardPage = 1 }
    fun onPreviousCard() { currentCardPage = 0 }

    var isBalanceVisible by mutableStateOf(false)
        private set

    fun onIsBalanceVisibleChange() {
        isBalanceVisible = !isBalanceVisible
    }

    var isVelocityToggleVisible by mutableStateOf(false)
        private set

    fun onVelocityToggleVisibleChange() {
        isVelocityToggleVisible = !isVelocityToggleVisible
    }

    var showLogoutDialog by mutableStateOf(false)

    fun onLogoutClickChange(newValue: Boolean) {
        showLogoutDialog = newValue
    }

    var drawerState by mutableStateOf(DrawerValue.Closed)
}
