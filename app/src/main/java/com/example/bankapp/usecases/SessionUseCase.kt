package com.example.bankapp.usecases

import com.example.bankapp.entities.dbtables.User
import com.example.bankapp.repositories.UserRepository
import com.example.bankapp.services.SessionManagementService


class SessionUseCase(
    private val sessionManagementService: SessionManagementService,
    private val userRepository: UserRepository
){
    fun saveUserOnSharedPreferences(userId: String){
        sessionManagementService.saveUserId(userId)
    }

    suspend fun getUserFromSharedPreferences(): User?{
        val userId: String? = sessionManagementService.getUserId()

        return if(userId!=null){
            userRepository.getUserByUserId(userId)
        } else
            null
    }

    fun clearSession(){
        sessionManagementService.clearSession()
    }
}