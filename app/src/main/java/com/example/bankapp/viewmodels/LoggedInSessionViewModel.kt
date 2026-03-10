package com.example.bankapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.entities.SessionState
import com.example.bankapp.entities.User
import com.example.bankapp.repositories.AccountRepository
import com.example.bankapp.usecases.SessionUseCase
import kotlinx.coroutines.launch


class LoggedInSessionViewModel(
    private val sessionUseCase: SessionUseCase,
    private val accountRepository: AccountRepository
): ViewModel(){

   var sessionState by mutableStateOf<SessionState>(SessionState.Loading)
       private set

   var currentUser by mutableStateOf<User?>(null)
        private set

    fun restoreSession(){
        viewModelScope.launch {

            val user = sessionUseCase.getUserFromSharedPreferences()
            println("restoreSession user = $user")

            sessionState = if(user == null) {
                println("Session -> UnAuthenticated")
                SessionState.UnAuthenticated
            } else {
                val accounts = accountRepository.getAccountByUserId(user.userId)
                println("Accounts found = ${accounts.size}")
                val account = accounts.firstOrNull()
                currentUser = user

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
        sessionUseCase.clearSession()
        sessionState = SessionState.UnAuthenticated
        restoreSession()
    }
}