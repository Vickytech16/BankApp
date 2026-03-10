package com.example.bankapp.ui.components.navigators

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
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
import com.example.bankapp.viewmodels.LoggedInSessionViewModel


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
    notificationViewModelFactory: NotificationViewModelFactory
                 )
    {
    navigation(
        startDestination = LOGIN_ROUTE,
        route = AUTH_ROUTE,
    ){
        composable(LOGIN_ROUTE){
            LoginScreen(windowSizeClass = windowSizeClass, navController = navController, loginViewModelFactory = loginViewModelFactory, loggedInSessionViewModel = loggedInSessionViewModel)
        }
        composable(REGISTER_ROUTE){
            RegisterScreen(windowSizeClass = windowSizeClass, navController = navController, registerViewModelFactory = registerViewModelFactory)
        }
        composable(FORGOT_PASSWORD_ROUTE){
            ForgetPasswordScreen(windowSizeClass = windowSizeClass, navController = navController, forgotPasswordViewModelFactory = forgotPasswordViewModelFactory)
        }
        composable(CHANGE_PASSWORD_ROUTE){
            ChangePasswordScreen(windowSizeClass = windowSizeClass, navController = navController, viewModelFactory = changePasswordViewModelFactory)
        }
        composable(REGISTER_SUCCESS_ROUTE){
            SuccessConfirmation("Registration successful!", "Redirecting to Login", {
                navController.navigate(AUTH_ROUTE){
                    popUpTo(0) {
                        inclusive = true
                    }
                }
            }
            )
        }
        composable(LOGIN_SUCCESS_ROUTE){
            SuccessConfirmation("Login successful!", "wait a min, we're loading to your account", {

            })
        }
        composable(CHANGE_PASSWORD_SUCCESS_ROUTE){
            SuccessConfirmation("password change successful!", "redirecting to login", {
                navController.navigate(AUTH_ROUTE){
                    popUpTo(0){
                        inclusive = true
                    }
                }
            })
        }
        composable(FORGOT_PASSWORD_OTP_ROUTE){
            OtpScreen(notificationViewModelFactory = notificationViewModelFactory, otpViewModelFactory = otpViewModelFactory,navController = navController, onSuccessfulOtpVerification = {
                navController.navigate(CHANGE_PASSWORD_ROUTE)
            })
        }
    }
}
