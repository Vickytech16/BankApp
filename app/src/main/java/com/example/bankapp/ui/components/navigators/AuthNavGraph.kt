package com.example.bankapp.ui.components.navigators

import RecoveryKeyDisplayScreen
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.bankapp.R
import com.example.bankapp.di.viewmodelfactory.ChangePasswordViewModelFactory
import com.example.bankapp.di.viewmodelfactory.ForgotPasswordViewModelFactory
import com.example.bankapp.di.viewmodelfactory.LoginViewModelFactory
import com.example.bankapp.di.viewmodelfactory.NotificationViewModelFactory
import com.example.bankapp.di.viewmodelfactory.OtpViewModelFactory
import com.example.bankapp.di.viewmodelfactory.RecoveryKeyViewModelFactory
import com.example.bankapp.di.viewmodelfactory.RegisterViewModelFactory
import com.example.bankapp.ui.screens.authscreens.ChangePasswordScreen
import com.example.bankapp.ui.screens.authscreens.ForgetPasswordScreen
import com.example.bankapp.ui.screens.authscreens.LoginScreen
import com.example.bankapp.ui.screens.authscreens.OtpScreen
import com.example.bankapp.ui.screens.authscreens.RegisterScreen
import com.example.bankapp.ui.screens.SuccessConfirmation
import com.example.bankapp.ui.screens.authscreens.RecoveryKeyVerificationScreen


fun NavGraphBuilder.authNavGraph(
    navController: NavController,
    windowSizeClass: WindowSizeClass,
    loginViewModelFactory: LoginViewModelFactory,
    registerViewModelFactory: RegisterViewModelFactory,
    forgotPasswordViewModelFactory: ForgotPasswordViewModelFactory,
    changePasswordViewModelFactory: ChangePasswordViewModelFactory,
    otpViewModelFactory: OtpViewModelFactory,
    notificationViewModelFactory: NotificationViewModelFactory,
    recoveryKeyViewModelFactory: RecoveryKeyViewModelFactory,
    restoreSession: () -> Unit
) {
    navigation(
        startDestination = LOGIN_ROUTE,
        route = AUTH_ROUTE,
    ) {

        composable(LOGIN_ROUTE) {
            LoginScreen(
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

        composable(FORGOT_PASSWORD_ROUTE_AUTH) {
            ForgetPasswordScreen(
                navController = navController,
                forgotPasswordViewModelFactory = forgotPasswordViewModelFactory
            )
        }

        composable(CHANGE_PASSWORD_ROUTE_AUTH) {
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
                    navController.navigate(RECOVERY_KEY_DISPLAY_ROUTE) {
                        popUpTo(AUTH_ROUTE) {
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

        composable(RECOVERY_KEY_AUTH_ROUTE) {
            RecoveryKeyVerificationScreen(
                recoveryKeyViewModelFactory,
                navController,
                {navController.navigate(CHANGE_PASSWORD_ROUTE_AUTH)}
            )
        }

        composable(
            route = "$RECOVERY_KEY_DISPLAY_ROUTE/{key}",
            arguments = listOf(
                navArgument("key") {
                    type = NavType.StringType
                })
            ) { backStackEntry ->
            val key = backStackEntry.arguments?.getString("key")
            RecoveryKeyDisplayScreen(key!!, {
                navController.navigate(AUTH_ROUTE) {
                    popUpTo(0) {
                        inclusive = true
                    }
                }
            })
        }



        composable(
            route = "$AUTH_OTP/{backRoute}",
            arguments = listOf(
                navArgument("backRoute") {
                    type = NavType.StringType
                    defaultValue = FORGOT_PASSWORD_ROUTE_AUTH
                }
            )
        ) { backStackEntry ->
            val backRoute = backStackEntry.arguments?.getString("backRoute") ?: FORGOT_PASSWORD_ROUTE_AUTH
            OtpScreen(
                navController = navController,
                notificationViewModelFactory = notificationViewModelFactory,
                otpViewModelFactory = otpViewModelFactory,
                backRoute = backRoute,
                onOtpSuccess = {
                    navController.navigate(CHANGE_PASSWORD_ROUTE_AUTH) {
                        popUpTo(FORGOT_PASSWORD_ROUTE_AUTH) { inclusive = true }
                    }
                }
            )
        }
    }
}