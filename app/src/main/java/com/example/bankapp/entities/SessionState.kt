package com.example.bankapp.entities



sealed class SessionState{
    object Loading: SessionState()
    object UnAuthenticated: SessionState()

    sealed class Authenticated() : SessionState() {

        abstract val user: User

        abstract val account: Account

        data class AccountRegistered(val user: User,  val account: Account) : SessionState()
        data class AccountNotRegistered(val user: User): SessionState()
    }
}