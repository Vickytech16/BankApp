package com.example.bankapp.ui.screens.authscreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.bankapp.di.viewmodelfactory.LoginViewModelFactory
import com.example.bankapp.ui.components.ErrorTextBuilder
import com.example.bankapp.ui.components.XLSpacer
import com.example.bankapp.ui.components.MediumSpacer
import com.example.bankapp.ui.components.buttons.SubmitButton
import com.example.bankapp.ui.components.navigators.REGISTER_ROUTE

import com.example.bankapp.entities.types.ui.LoginType
import com.example.bankapp.ui.components.MediumHorizontalSpacer
import com.example.bankapp.viewmodels.LoginViewModel
import com.example.bankapp.ui.components.navigators.FORGOT_PASSWORD_ROUTE
import com.example.bankapp.ui.components.navigators.LOGIN_SUCCESS_ROUTE
import com.example.bankapp.ui.components.textfields.UnifiedOutlinedTextField
import com.example.bankapp.utilities.EmailFieldStrategy
import com.example.bankapp.utilities.PasswordFieldStrategy
import com.example.bankapp.utilities.PhoneNumberFieldStrategy


@Composable
fun LoginScreen( windowSizeClass: WindowSizeClass, navController: NavController,
                 loginViewModelFactory: LoginViewModelFactory, )
{
    val loginViewModel: LoginViewModel = viewModel(factory = loginViewModelFactory)

    val scrollState = rememberScrollState()

    val currentLoginTypeName = when(loginViewModel.loginType){
        LoginType.PHONE_NUMBER -> stringResource(R.string.phone_number_field_name)
        LoginType.EMAIL -> stringResource(R.string.email_field_name)
    }

    val textFieldColumnWidth =
        when (windowSizeClass.widthSizeClass) {
            WindowWidthSizeClass.Compact -> 0.9f
            WindowWidthSizeClass.Medium -> 0.6f
            WindowWidthSizeClass.Expanded -> 0.5f
            else -> 0.8f
        }

    LaunchedEffect(loginViewModel.isLoginSuccessful) {
        if (loginViewModel.isLoginSuccessful) {
            navController.navigate(LOGIN_SUCCESS_ROUTE)
        }
    }

    Scaffold {
        contentPadding ->
        Column(
            modifier = getAppModifier(windowSizeClass, contentPadding, scrollState)
            ,

            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {

            Image(
                painter = painterResource(id = R.drawable.signin_illustration),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.illustration_height))
                    .padding(bottom = dimensionResource(R.dimen.illustration_bottom_padding)),
                contentScale = ContentScale.Fit
            )

            MediumSpacer()

            Text(
                text = when(loginViewModel.loginType){
                    LoginType.PHONE_NUMBER -> stringResource(R.string.login_type_description, currentLoginTypeName)
                    LoginType.EMAIL ->stringResource(R.string.login_type_description, currentLoginTypeName)
                },
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

                when (loginViewModel.loginType) {
                    LoginType.EMAIL ->
                        UnifiedOutlinedTextField(
                             value = loginViewModel.email,
                             onValueChange = loginViewModel::onEmailChange,
                             labelText = stringResource(R.string.email_field_name),
                             isError = loginViewModel.emailError != null,
                             supportingText = {
                                 ErrorTextBuilder(loginViewModel.emailError)
                             },
                             strategy = EmailFieldStrategy,
                         )

                    LoginType.PHONE_NUMBER ->
                        UnifiedOutlinedTextField(
                            value = loginViewModel.phoneNumber,
                            onValueChange = loginViewModel::onPhoneNumberChange,
                            labelText = stringResource(R.string.phone_number_field_name),
                            isError = loginViewModel.phoneNumberError != null,
                            supportingText = {
                                ErrorTextBuilder(loginViewModel.phoneNumberError)
                            },
                            strategy = PhoneNumberFieldStrategy
                        )
                }

                MediumSpacer()

                UnifiedOutlinedTextField(
                    value = loginViewModel.password,
                    onValueChange = loginViewModel::onPasswordChange,
                    labelText = stringResource(R.string.password_field_name),
                    isError = loginViewModel.passwordError != null,
                    supportingText = {
                        ErrorTextBuilder(loginViewModel.passwordError)
                    },
                    strategy = PasswordFieldStrategy(loginViewModel.passwordVisible, loginViewModel::onPasswordVisibleChange)
                )

                TextButton(
                    onClick = {
                        navController.navigate(
                            FORGOT_PASSWORD_ROUTE
                        )
                    },
                    modifier = Modifier.align(alignment = Alignment.End),
                ) {

                    Text(stringResource(R.string.forgot_password))
                }

                XLSpacer()

                SubmitButton(
                    onClick = { loginViewModel.onSubmit() },
                    text = stringResource(R.string.login_button),
                    isLoading = loginViewModel.isLoading
                )

               ErrorTextBuilder(loginViewModel.submitError)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        thickness = dimensionResource(R.dimen.divider_thickness),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    Text(" or ")
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        thickness = dimensionResource(R.dimen.divider_thickness),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }

                XLSpacer()

                when (loginViewModel.loginType) {
                    LoginType.EMAIL -> {
                        SwitchToPhoneNumberLoginButton(loginViewModel::onLoginTypeChange)
                    }

                    LoginType.PHONE_NUMBER -> {
                        SwitchToEmailLoginButton(
                            loginViewModel::onLoginTypeChange
                        )
                    }
                }

                MediumSpacer()

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(R.string.dont_have_an_Account))
                    TextButton(onClick = {
                        navController.navigate(REGISTER_ROUTE)
                    }) {
                        Text(stringResource(R.string.register_button))
                    }
                }
            }
        }
    }
}

@Composable
private fun SwitchToEmailLoginButton(onclickAction: (LoginType)->Unit){

    OutlinedButton(
        onClick = {
            onclickAction(LoginType.EMAIL)
        },
        modifier = Modifier.fillMaxWidth()
    ){
        Icon(Icons.Outlined.Email, contentDescription = null)
        MediumHorizontalSpacer()
        Text(stringResource(R.string.login_type_description, stringResource(R.string.email_field_name)))
    }
}

@Composable
private fun SwitchToPhoneNumberLoginButton(onclickAction: (LoginType) -> Unit){
    OutlinedButton(
        onClick = {
            onclickAction(LoginType.PHONE_NUMBER)
        },
        modifier = Modifier.fillMaxWidth()
    ){
        Icon(Icons.Outlined.Phone, contentDescription = null)
        MediumHorizontalSpacer()
        Text(stringResource(R.string.login_type_description, stringResource(R.string.phone_number_field_name)))
    }
}

