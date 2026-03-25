package com.example.bankapp.entities

import com.example.bankapp.entities.dbtables.Account
import com.example.bankapp.entities.dbtables.User


sealed class SessionState {

    object Loading: SessionState()

    object UnAuthenticated: SessionState()

    sealed class Authenticated() : SessionState() {

        data class AccountRegistered(val user: User, val account: Account) : SessionState()

        data class AccountNotRegistered(val user: User): SessionState()
    }
}