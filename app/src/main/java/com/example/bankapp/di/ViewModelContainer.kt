package com.example.bankapp.di

import com.example.bankapp.di.viewmodelfactory.AuthorizationViewModelFactory
import com.example.bankapp.di.viewmodelfactory.ChangePasswordViewModelFactory
import com.example.bankapp.di.viewmodelfactory.FilterViewModelFactory
import com.example.bankapp.di.viewmodelfactory.ForgotPasswordViewModelFactory
import com.example.bankapp.di.viewmodelfactory.LoggedInSessionViewModelFactory
import com.example.bankapp.di.viewmodelfactory.LoginViewModelFactory
import com.example.bankapp.di.viewmodelfactory.OtpViewModelFactory
import com.example.bankapp.di.viewmodelfactory.RecoveryKeyViewModelFactory
import com.example.bankapp.di.viewmodelfactory.RegisterViewModelFactory
import com.example.bankapp.di.viewmodelfactory.ThemeViewModelFactory
import com.example.bankapp.repositories.AccountRepository
import com.example.bankapp.repositories.CountryRepository
import com.example.bankapp.repositories.UserRepository
import com.example.bankapp.entities.ChangePasswordState
import com.example.bankapp.utilities.SharedPreferenceHelper


class ViewModelContainer(
    userRepository: UserRepository,
    changePasswordState: ChangePasswordState,
    private val sharedPreferenceHelper: SharedPreferenceHelper,
    private val accountRepository: AccountRepository,
    private val countryRepository: CountryRepository
) {
    val loginViewModelFactory: LoginViewModelFactory = LoginViewModelFactory(
        userRepository,
        sharedPreferenceHelper,
    )

    val registerViewModelFactory: RegisterViewModelFactory = RegisterViewModelFactory(
        userRepository, countryRepository = countryRepository
    )

    val changePasswordViewModelFactory: ChangePasswordViewModelFactory = ChangePasswordViewModelFactory(
            userRepository,
            changePasswordState
        )

    val forgotPasswordViewModelFactory: ForgotPasswordViewModelFactory = ForgotPasswordViewModelFactory(
            userRepository,
            changePasswordState
        )

    val otpViewModelFactory: OtpViewModelFactory =
        OtpViewModelFactory()


    val loggedInSessionViewModelFactory: LoggedInSessionViewModelFactory =
        LoggedInSessionViewModelFactory(accountRepository = accountRepository, sharedPreferenceHelper = sharedPreferenceHelper, userRepository = userRepository)

    val filterViewModelFactory: FilterViewModelFactory =
        FilterViewModelFactory()


    val themeViewModelFactory: ThemeViewModelFactory =
        ThemeViewModelFactory(sharedPreferenceHelper)

    val recoveryKeyViewModelFactory: RecoveryKeyViewModelFactory =
        RecoveryKeyViewModelFactory(changePasswordState)

    val authorizationViewModelFactory: AuthorizationViewModelFactory =
        AuthorizationViewModelFactory()
}