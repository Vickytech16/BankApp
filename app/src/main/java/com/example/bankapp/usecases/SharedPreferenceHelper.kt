package com.example.bankapp.usecases

import com.example.bankapp.entities.dbtables.User
import com.example.bankapp.repositories.UserRepository
import com.example.bankapp.services.SharedPreferenceService
import kotlinx.coroutines.flow.Flow


class SharedPreferenceHelper(
    private val sharedPreferenceService: SharedPreferenceService,
    private val userRepository: UserRepository
){
    fun saveUserOnSharedPreferences(userId: String){
        sharedPreferenceService.saveUserId(userId)
    }

    suspend fun getUserFromSharedPreferences(): User?{
        val userId: String? = sharedPreferenceService.getUserId()

        return if(userId!=null){
            userRepository.getUserByUserId(userId)
        } else
            null
    }

    fun clearSession(){
        sharedPreferenceService.clearSession()
    }

    fun getSavedTheme(): Flow<String> {
       return sharedPreferenceService.getSavedTheme()
    }

    suspend fun saveTheme(theme: String) {
        sharedPreferenceService.saveTheme(theme)
    }

    fun saveUserTimeZone(userId: String, timezone: String){
        sharedPreferenceService.saveUserTimezone(userId, timezone)
    }

    fun getUserTimeZone(): String{
        return sharedPreferenceService.getUserTimezone()
    }
}