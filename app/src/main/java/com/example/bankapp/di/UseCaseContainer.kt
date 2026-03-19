package com.example.bankapp.di

import com.example.bankapp.repositories.UserRepository
import com.example.bankapp.usecases.ChangePasswordUseCase
import com.example.bankapp.usecases.SessionUseCase
import com.example.bankapp.services.SessionManagementService
import com.example.bankapp.usecases.TransactionSessionHolder
import com.example.bankapp.viewmodels.OtpVerificationViewModel

class UseCaseContainer(
    sessionManagementService: SessionManagementService,
    userRepository: UserRepository
    ) {

    val sessionUseCase: SessionUseCase = SessionUseCase(sessionManagementService, userRepository)

    val changePasswordUseCase: ChangePasswordUseCase = ChangePasswordUseCase()

}