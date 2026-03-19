package com.example.bankapp.ui.components.navigators

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.example.bankapp.di.viewmodelfactory.OtpVerificationViewModelFactory
import com.example.bankapp.di.viewmodelfactory.OtpViewModelFactory
import com.example.bankapp.di.viewmodelfactory.RegisterViewModelFactory
import com.example.bankapp.ui.screens.authscreens.ChangePasswordScreen
import com.example.bankapp.ui.screens.authscreens.ForgetPasswordScreen
import com.example.bankapp.ui.screens.authscreens.LoginScreen
import com.example.bankapp.ui.screens.authscreens.OtpScreen
import com.example.bankapp.ui.screens.authscreens.RegisterScreen
import com.example.bankapp.ui.screens.SuccessConfirmation
import com.example.bankapp.viewmodels.LoggedInSessionViewModel
import com.example.bankapp.viewmodels.OtpVerificationViewModel

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.authNavGraph(
    navController: NavController,
    windowSizeClass: WindowSizeClass,
    loginViewModelFactory: LoginViewModelFactory,
    registerViewModelFactory: RegisterViewModelFactory,
    forgotPasswordViewModelFactory: ForgotPasswordViewModelFactory,
    changePasswordViewModelFactory: ChangePasswordViewModelFactory,
    loggedInSessionViewModel: LoggedInSessionViewModel,
    otpViewModelFactory: OtpViewModelFactory,
    notificationViewModelFactory: NotificationViewModelFactory,
    otpVerificationViewModelFactory: OtpVerificationViewModelFactory
) {
    navigation(
        startDestination = LOGIN_ROUTE,
        route = AUTH_ROUTE,
    ) {
        composable(LOGIN_ROUTE) {
            LoginScreen(
                windowSizeClass = windowSizeClass,
                navController = navController,
                loginViewModelFactory = loginViewModelFactory,
                loggedInSessionViewModel = loggedInSessionViewModel
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
            val otpVerificationViewModel: OtpVerificationViewModel = viewModel(
                factory = otpVerificationViewModelFactory
            )

            ForgetPasswordScreen(
                windowSizeClass = windowSizeClass,
                navController = navController,
                forgotPasswordViewModelFactory = forgotPasswordViewModelFactory,
                otpVerificationViewModel = otpVerificationViewModel
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
                stringResource(R.string.login_succcessful),
                stringResource(R.string.loading_your_account),
                {}
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