package com.example.bankapp.viewmodels

import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.bankapp.entities.SessionState

class HomeViewModel(
    sessionState: SessionState.Authenticated.AccountRegistered
) : ViewModel() {

    val user = sessionState.user

    val account = sessionState.account

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
