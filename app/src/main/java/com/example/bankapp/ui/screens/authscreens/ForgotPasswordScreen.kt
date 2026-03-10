package com.example.bankapp.ui.screens.authscreens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bankapp.R
import com.example.bankapp.di.viewmodelfactory.ForgotPasswordViewModelFactory
import com.example.bankapp.ui.components.Appbar
import com.example.bankapp.ui.components.BackButtonHandler
import com.example.bankapp.ui.components.ErrorTextBuilder
import com.example.bankapp.ui.components.XLSpacer
import com.example.bankapp.ui.components.MediumSpacer
import com.example.bankapp.ui.components.buttons.SubmitButton

import com.example.bankapp.ui.components.navigators.AUTH_ROUTE
import com.example.bankapp.ui.components.navigators.FORGOT_PASSWORD_OTP_ROUTE
import com.example.bankapp.ui.components.navigators.LOGIN_ROUTE
import com.example.bankapp.ui.components.textfields.GenericOutlinedTextField
import com.example.bankapp.ui.theme.AppPadding
import com.example.bankapp.ui.theme.screenPadding
import com.example.bankapp.ui.theme.titleFontSize

import com.example.bankapp.viewmodels.ForgotPasswordViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgetPasswordScreen(windowSizeClass: WindowSizeClass,
                         forgotPasswordViewModelFactory: ForgotPasswordViewModelFactory,
                         navController: NavController)
{

    val viewModel: ForgotPasswordViewModel = viewModel(factory = forgotPasswordViewModelFactory)
    val scrollState = rememberScrollState()

    val textFieldColumnWidth =
        when (windowSizeClass.widthSizeClass) {
            WindowWidthSizeClass.Compact -> 0.9f
            WindowWidthSizeClass.Medium -> 0.6f
            WindowWidthSizeClass.Expanded -> 0.5f
            else -> 0.8f
        }

    BackButtonHandler(navController, LOGIN_ROUTE)

    Scaffold(
        topBar = { Appbar("Forgot Password", { navController.navigate(AUTH_ROUTE) }, null) },
        contentWindowInsets = WindowInsets(0,0,0,0)
    )
    {
        Column(
            modifier = AppPadding
                .padding(screenPadding)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ){
            Image(
                painter = painterResource(id = R.drawable.forgot_password_illustration),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 30.dp),
                contentScale = ContentScale.Fit
            )

            XLSpacer()

            Text(
                text = stringResource(R.string.enter_your_details_headline),
                fontSize = titleFontSize,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.ExtraBold
            )

            XLSpacer()

            Column(
                modifier = Modifier.fillMaxWidth(textFieldColumnWidth),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                GenericOutlinedTextField(
                    value = viewModel.email,
                    onValueChange = viewModel::onEmailChange,
                    labelText = stringResource(R.string.email_field_name),
                    isError = viewModel.emailError == null,
                    supportingText = {
                        ErrorTextBuilder(viewModel.emailError)
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                    ),
                    leadingIcon = Icons.Outlined.Email,
                )

                MediumSpacer()


                GenericOutlinedTextField(
                    value = viewModel.phoneNumber,
                    onValueChange = viewModel::onPhoneNumberChange,
                    labelText = stringResource(R.string.phone_number_field_name),
                    isError = viewModel.phoneNumberError != null,
                    supportingText = {
                        ErrorTextBuilder(viewModel.phoneNumberError)
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    leadingIcon = Icons.Outlined.Phone,
                )

                XLSpacer()

                SubmitButton(
                    onClick = { viewModel.onSubmit() },
                    text = stringResource(R.string.submit_button),
                    isLoading = viewModel.isLoading,
                )

                ErrorTextBuilder(viewModel.submitError)

                MediumSpacer()

                LaunchedEffect(viewModel.isVerificationSuccessful) {
                    if(viewModel.isVerificationSuccessful)
                        navController.navigate(FORGOT_PASSWORD_OTP_ROUTE)
                }

            }
        }
    }
    }

