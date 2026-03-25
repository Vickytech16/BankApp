package com.example.bankapp.di.providers

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.bankapp.entities.SessionState
import com.example.bankapp.entities.dbtables.User
import com.example.bankapp.repositories.AccountRepository
import com.example.bankapp.usecases.SharedPreferenceHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SessionStateProvider(
    private val sharedPreferenceHelper: SharedPreferenceHelper,
    private val accountRepository: AccountRepository
) {
    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Loading)
    val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

    var currentUser by mutableStateOf<User?>(null)
        private set

    private val scope = CoroutineScope(Dispatchers.IO)

    fun restoreSession() {
        scope.launch {

            val user = sharedPreferenceHelper.getUserFromSharedPreferences()
            println("restoreSession user = $user")

            _sessionState.value = if (user == null) {
                println("Session -> UnAuthenticated")
                SessionState.UnAuthenticated
            } else {
                val accounts = accountRepository.getAccountByUserId(user.userId)

                println("Accounts found = ${accounts.size}")
                val account = accounts.firstOrNull()
                currentUser = user

                if (account == null) {
                    println("Session -> AccountNotRegistered")
                    SessionState.Authenticated.AccountNotRegistered(user)
                } else {
                    println("Session -> AccountRegistered")
                    SessionState.Authenticated.AccountRegistered(user, account)
                }
            }
        }
    }

    fun logout() {
        scope.launch {
            sharedPreferenceHelper.clearSession()
            _sessionState.value = SessionState.UnAuthenticated
            currentUser = null
        }
    }
}