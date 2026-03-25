package com.example.bankapp.ui.screens.authscreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bankapp.R
import com.example.bankapp.di.viewmodelfactory.ForgotPasswordViewModelFactory
import com.example.bankapp.ui.components.appbar.Appbar
import com.example.bankapp.ui.components.BackButtonHandler
import com.example.bankapp.ui.components.ErrorTextBuilder
import com.example.bankapp.ui.components.XLSpacer
import com.example.bankapp.ui.components.MediumSpacer
import com.example.bankapp.ui.components.buttons.SubmitButton
import com.example.bankapp.ui.components.navigators.AUTH_OTP
import com.example.bankapp.ui.components.navigators.AUTH_ROUTE
import com.example.bankapp.ui.components.navigators.FORGOT_PASSWORD_ROUTE
import com.example.bankapp.ui.components.navigators.LOGIN_ROUTE
import com.example.bankapp.ui.components.textfields.UnifiedOutlinedTextField
import com.example.bankapp.utilities.EmailFieldStrategy
import com.example.bankapp.utilities.PhoneNumberFieldStrategy
import com.example.bankapp.viewmodels.ForgotPasswordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgetPasswordScreen(
    windowSizeClass: WindowSizeClass,
    forgotPasswordViewModelFactory: ForgotPasswordViewModelFactory,
    navController: NavController,
) {
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

    LaunchedEffect(viewModel.isVerificationSuccessful) {
        if (viewModel.isVerificationSuccessful) {
            navController.navigate("$AUTH_OTP/$FORGOT_PASSWORD_ROUTE")
        }
    }

    Scaffold(
        topBar = {
            Appbar(stringResource(R.string.forgot_password),
                { navController.navigate(AUTH_ROUTE) },
                null)
        }
    ) { contentPadding ->
        Column(
            modifier = getAppModifier(windowSizeClass,contentPadding,scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.forgot_password_illustration),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.illustration_height))
                    .padding(bottom = dimensionResource(R.dimen.illustration_bottom_padding)),
                contentScale = ContentScale.Fit
            )

            MediumSpacer()

            Text(
                text = stringResource(R.string.enter_your_details_headline),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.ExtraBold
            )

            XLSpacer()

            Column(
                modifier = Modifier.fillMaxWidth(textFieldColumnWidth),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                UnifiedOutlinedTextField(
                    value = viewModel.email,
                    onValueChange = viewModel::onEmailChange,
                    labelText = stringResource(R.string.email_field_name),
                    isError = viewModel.emailError != null,
                    supportingText = {
                        ErrorTextBuilder(viewModel.emailError)
                    },
                    strategy = EmailFieldStrategy
                )

                MediumSpacer()

                UnifiedOutlinedTextField(
                    value = viewModel.phoneNumber,
                    onValueChange = viewModel::onPhoneNumberChange,
                    labelText = stringResource(R.string.phone_number_field_name),
                    isError = viewModel.phoneNumberError != null,
                    supportingText = {
                        ErrorTextBuilder(viewModel.phoneNumberError)
                    },
                    strategy = PhoneNumberFieldStrategy
                )

                XLSpacer()

                SubmitButton(
                    onClick = { viewModel.onSubmit() },
                    text = stringResource(R.string.submit_button),
                    isLoading = viewModel.isLoading,
                )

                ErrorTextBuilder(viewModel.submitError)

                XLSpacer()
            }
        }
    }
}