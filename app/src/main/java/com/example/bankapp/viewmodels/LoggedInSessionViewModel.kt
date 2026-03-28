package com.example.bankapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.core.datecompatability.BankDateFactory
import com.example.bankapp.entities.SessionState
import com.example.bankapp.entities.dbtables.User
import com.example.bankapp.repositories.AccountRepository
import com.example.bankapp.usecases.SharedPreferenceHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class LoggedInSessionViewModel(
    private val sharedPreferenceHelper: SharedPreferenceHelper,
    private val accountRepository: AccountRepository
): ViewModel(){

    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Loading)
    val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

    var currentUser by mutableStateOf<User?>(null)
        private set

    fun restoreSession(){
        viewModelScope.launch {

            val user = sharedPreferenceHelper.getUserFromSharedPreferences()
            println("restoreSession user = $user")

            _sessionState.value = if(user == null) {
                println("Session -> UnAuthenticated")
                SessionState.UnAuthenticated
            } else {
                val accounts = accountRepository.getAccountByUserId(user.userId)
                println("Accounts found = ${accounts.size}")
                val account = accounts.firstOrNull()
                currentUser = user
                BankDateFactory.initialize(user.timeZone)

                if(account == null) {
                    println("Session -> AccountNotRegistered")
                    SessionState.Authenticated.AccountNotRegistered(user)
                } else {
                    println("Session -> AccountRegistered")
                    SessionState.Authenticated.AccountRegistered(user, account)
                }
            }
        }
    }

    init {
        restoreSession()
    }

    fun logout(){
        sharedPreferenceHelper.clearSession()
        _sessionState .value= SessionState.UnAuthenticated
        BankDateFactory.initialize("UTC")
        restoreSession()
    }
}