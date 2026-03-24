package com.example.bankapp.di

import com.example.bankapp.di.viewmodelfactory.AccountCreationViewModelFactory
import com.example.bankapp.di.viewmodelfactory.ChangePasswordViewModelFactory
import com.example.bankapp.di.viewmodelfactory.FilterViewModelFactory
import com.example.bankapp.di.viewmodelfactory.ForgotPasswordViewModelFactory
import com.example.bankapp.di.viewmodelfactory.LoggedInSessionViewModelFactory
import com.example.bankapp.di.viewmodelfactory.LoginViewModelFactory
import com.example.bankapp.di.viewmodelfactory.NotificationViewModelFactory
import com.example.bankapp.di.viewmodelfactory.OtpVerificationViewModelFactory
import com.example.bankapp.di.viewmodelfactory.OtpViewModelFactory
import com.example.bankapp.di.viewmodelfactory.RegisterViewModelFactory
import com.example.bankapp.di.viewmodelfactory.TransactionDetailsViewModelFactory
import com.example.bankapp.repositories.AccountRepository
import com.example.bankapp.repositories.TransactionRepository
import com.example.bankapp.repositories.UserRepository


class ViewModelContainer(
    userRepository: UserRepository,
    useCaseContainer: UseCaseContainer,
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository
) {
    val loginViewModelFactory: LoginViewModelFactory = LoginViewModelFactory(
        userRepository,
        useCaseContainer.sessionUseCase,
    )

    val registerViewModelFactory: RegisterViewModelFactory = RegisterViewModelFactory(
        userRepository
    )

    val changePasswordViewModelFactory: ChangePasswordViewModelFactory = ChangePasswordViewModelFactory(
            userRepository,
            useCaseContainer.changePasswordUseCase
        )

    val forgotPasswordViewModelFactory: ForgotPasswordViewModelFactory = ForgotPasswordViewModelFactory(
            userRepository,
            useCaseContainer.changePasswordUseCase
        )

    val otpViewModelFactory: OtpViewModelFactory =
        OtpViewModelFactory()

    val notificationViewModelFactory: NotificationViewModelFactory =
        NotificationViewModelFactory()

    val accountCreationViewModelFactory: AccountCreationViewModelFactory = AccountCreationViewModelFactory(
            accountRepository = accountRepository,
            sessionUseCase = useCaseContainer.sessionUseCase,
            transactionRepository = transactionRepository
        )

    val filterViewModelFactory: FilterViewModelFactory =
        FilterViewModelFactory()

    val transactionDetailsViewModelFactory: TransactionDetailsViewModelFactory =
        TransactionDetailsViewModelFactory(transactionRepository)


}