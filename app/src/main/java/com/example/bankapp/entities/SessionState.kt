package com.example.bankapp.entities

import com.example.bankapp.entities.dbtables.User
import com.example.bankapp.entities.uientities.uimodels.AccountUiModel
import kotlinx.coroutines.flow.StateFlow


sealed class SessionState {

    object Loading: SessionState()

    object UnAuthenticated: SessionState()

    sealed class Authenticated() : SessionState() {

        data class AccountRegistered(val user: StateFlow<User>, val account: StateFlow<AccountUiModel>) : SessionState()

        data class AccountNotRegistered(val user: User): SessionState()
    }
}