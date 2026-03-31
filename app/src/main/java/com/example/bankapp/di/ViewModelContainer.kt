package com.example.bankapp.di

import AuthorizationViewModel
import com.example.bankapp.di.viewmodelfactory.AccountCreationViewModelFactory
import com.example.bankapp.di.viewmodelfactory.AuthorizationViewModelFactory
import com.example.bankapp.di.viewmodelfactory.ChangePasswordViewModelFactory
import com.example.bankapp.di.viewmodelfactory.FilterViewModelFactory
import com.example.bankapp.di.viewmodelfactory.ForgotPasswordViewModelFactory
import com.example.bankapp.di.viewmodelfactory.LoggedInSessionViewModelFactory
import com.example.bankapp.di.viewmodelfactory.LoginViewModelFactory
import com.example.bankapp.di.viewmodelfactory.NotificationViewModelFactory
import com.example.bankapp.di.viewmodelfactory.OtpViewModelFactory
import com.example.bankapp.di.viewmodelfactory.RecoveryKeyViewModelFactory
import com.example.bankapp.di.viewmodelfactory.RegisterViewModelFactory
import com.example.bankapp.di.viewmodelfactory.ThemeViewModelFactory
import com.example.bankapp.di.viewmodelfactory.TransactionDetailsViewModelFactory
import com.example.bankapp.repositories.AccountRepository
import com.example.bankapp.repositories.CountryRepository
import com.example.bankapp.repositories.TransactionRepository
import com.example.bankapp.repositories.UserRepository


class ViewModelContainer(
    userRepository: UserRepository,
    useCaseContainer: UseCaseContainer,
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val countryRepository: CountryRepository
) {
    val loginViewModelFactory: LoginViewModelFactory = LoginViewModelFactory(
        userRepository,
        useCaseContainer.sharedPreferenceHelper,
    )

    val registerViewModelFactory: RegisterViewModelFactory = RegisterViewModelFactory(
        userRepository, countryRepository = countryRepository
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
            sharedPreferenceHelper = useCaseContainer.sharedPreferenceHelper,
            transactionRepository = transactionRepository
        )

    val loggedInSessionViewModelFactory: LoggedInSessionViewModelFactory =
        LoggedInSessionViewModelFactory(accountRepository = accountRepository, sharedPreferenceHelper = useCaseContainer.sharedPreferenceHelper, userRepository = userRepository)

    val filterViewModelFactory: FilterViewModelFactory =
        FilterViewModelFactory()

    val transactionDetailsViewModelFactory: TransactionDetailsViewModelFactory =
        TransactionDetailsViewModelFactory(transactionRepository)

    val themeViewModelFactory: ThemeViewModelFactory =
        ThemeViewModelFactory(useCaseContainer.sharedPreferenceHelper)

    val recoveryKeyViewModelFactory: RecoveryKeyViewModelFactory =
        RecoveryKeyViewModelFactory(useCaseContainer.changePasswordUseCase)

    val authorizationViewModelFactory: AuthorizationViewModelFactory =
        AuthorizationViewModelFactory()


}