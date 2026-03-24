package com.example.bankapp.ui.components.navigators

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.bankapp.R
import com.example.bankapp.di.providers.SessionStateProvider
import com.example.bankapp.di.viewmodelfactory.ChangePasswordViewModelFactory
import com.example.bankapp.di.viewmodelfactory.ForgotPasswordViewModelFactory
import com.example.bankapp.di.viewmodelfactory.LoginViewModelFactory
import com.example.bankapp.di.viewmodelfactory.NotificationViewModelFactory
import com.example.bankapp.di.viewmodelfactory.OtpViewModelFactory
import com.example.bankapp.di.viewmodelfactory.RegisterViewModelFactory
import com.example.bankapp.ui.screens.authscreens.ChangePasswordScreen
import com.example.bankapp.ui.screens.authscreens.ForgetPasswordScreen
import com.example.bankapp.ui.screens.authscreens.LoginScreen
import com.example.bankapp.ui.screens.authscreens.OtpScreen
import com.example.bankapp.ui.screens.authscreens.RegisterScreen
import com.example.bankapp.ui.screens.SuccessConfirmation


fun NavGraphBuilder.authNavGraph(
    navController: NavController,
    windowSizeClass: WindowSizeClass,
    loginViewModelFactory: LoginViewModelFactory,
    registerViewModelFactory: RegisterViewModelFactory,
    forgotPasswordViewModelFactory: ForgotPasswordViewModelFactory,
    changePasswordViewModelFactory: ChangePasswordViewModelFactory,
    otpViewModelFactory: OtpViewModelFactory,
    notificationViewModelFactory: NotificationViewModelFactory,
    restoreSession: () -> Unit
) {
    navigation(
        startDestination = LOGIN_ROUTE,
        route = AUTH_ROUTE,
    ) {

        composable(LOGIN_ROUTE) {
            LoginScreen(
                windowSizeClass = windowSizeClass,
                navController = navController,
                loginViewModelFactory = loginViewModelFactory
            )
        }

        composable(REGISTER_ROUTE) {
            RegisterScreen(
                windowSizeClass = windowSizeClass,
                navController = navController,
                registerViewModelFactory = registerViewModelFactory
            )
        }

        composable(FORGOT_PASSWORD_ROUTE) {
            ForgetPasswordScreen(
                windowSizeClass = windowSizeClass,
                navController = navController,
                forgotPasswordViewModelFactory = forgotPasswordViewModelFactory
            )
        }

        composable(CHANGE_PASSWORD_ROUTE) {
            ChangePasswordScreen(
                windowSizeClass = windowSizeClass,
                navController = navController,
                viewModelFactory = changePasswordViewModelFactory
            )
        }

        composable(REGISTER_SUCCESS_ROUTE) {
            SuccessConfirmation(
                stringResource(R.string.register_successful),
                stringResource(R.string.redirecting_to_login),
                {
                    navController.navigate(AUTH_ROUTE) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(LOGIN_SUCCESS_ROUTE) {
            SuccessConfirmation(
                 title =stringResource(R.string.login_succcessful),
                 subtitle = stringResource(R.string.loading_your_account),
                 onDone = { restoreSession() }
            )
        }

        composable(CHANGE_PASSWORD_SUCCESS_ROUTE) {
            SuccessConfirmation(
                stringResource(R.string.password_changed_successfully),
                stringResource(R.string.redirecting_to_login),
                {
                    navController.navigate(AUTH_ROUTE) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(
            route = "$AUTH_OTP/{backRoute}",
            arguments = listOf(
                navArgument("backRoute") {
                    type = NavType.StringType
                    defaultValue = FORGOT_PASSWORD_ROUTE
                }
            )
        ) { backStackEntry ->
            val backRoute = backStackEntry.arguments?.getString("backRoute") ?: FORGOT_PASSWORD_ROUTE
            OtpScreen(
                navController = navController,
                notificationViewModelFactory = notificationViewModelFactory,
                otpViewModelFactory = otpViewModelFactory,
                backRoute = backRoute,
                onOtpSuccess = {
                    navController.navigate(CHANGE_PASSWORD_ROUTE) {
                        popUpTo(FORGOT_PASSWORD_ROUTE) { inclusive = true }
                    }
                }
            )
        }
    }
}