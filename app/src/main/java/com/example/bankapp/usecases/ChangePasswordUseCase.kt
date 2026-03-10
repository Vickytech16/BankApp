package com.example.bankapp.usecases

import com.example.bankapp.entities.User

class ChangePasswordUseCase {
    var user: User? = null

    fun clearUser(){
        user = null
    }
}