package com.example.bankapp.di

import com.example.bankapp.repositories.UserRepository
import com.example.bankapp.usecases.ChangePasswordUseCase
import com.example.bankapp.usecases.SharedPreferenceHelper
import com.example.bankapp.services.SharedPreferenceService

class UseCaseContainer(
    sharedPreferenceService: SharedPreferenceService,
    userRepository: UserRepository
    ) {

    val sharedPreferenceHelper: SharedPreferenceHelper = SharedPreferenceHelper(sharedPreferenceService, userRepository)

    val changePasswordUseCase: ChangePasswordUseCase = ChangePasswordUseCase()

}