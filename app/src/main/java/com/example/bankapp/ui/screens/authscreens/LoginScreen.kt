package com.example.bankapp.ui.screens.authscreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bankapp.R
import com.example.bankapp.di.viewmodelfactory.LoginViewModelFactory
import com.example.bankapp.ui.components.ErrorTextBuilder
import com.example.bankapp.ui.components.XLSpacer
import com.example.bankapp.ui.components.MediumSpacer
import com.example.bankapp.ui.components.buttons.SubmitButton
import com.example.bankapp.ui.components.navigators.REGISTER_ROUTE
import com.example.bankapp.ui.components.screenModifier
import com.example.bankapp.viewmodels.authviewmodels.LoginViewModel
import com.example.bankapp.ui.components.navigators.FORGOT_PASSWORD_ROUTE_AUTH
import com.example.bankapp.ui.components.navigators.LOGIN_SUCCESS_ROUTE
import com.example.bankapp.ui.components.textfields.UnifiedOutlinedTextField
import com.example.bankapp.ui.theme.DeviceSpec
import com.example.bankapp.ui.theme.LocalDeviceSpec
import com.example.bankapp.entities.uientities.uidata.EmailFieldStrategy
import com.example.bankapp.entities.uientities.uidata.PasswordFieldStrategy
import com.example.bankapp.ui.components.IllustrationComponent
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.utilities.EMAIL_MAX_SIZE
import com.example.bankapp.utilities.PASSWORD_MAX_SIZE

@Composable
fun LoginScreen(
    navController: NavController,
    loginViewModelFactory: LoginViewModelFactory
) {
    val loginViewModel: LoginViewModel = viewModel(factory = loginViewModelFactory)
    val scrollState = rememberScrollState()
    val deviceSpec = LocalDeviceSpec.current
    val isAnyFieldFocused = remember { mutableStateOf(false) }
    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    // Requesters for auto-focus
    val identifierFocus = remember { FocusRequester() }
    val passwordFocus = remember { FocusRequester() }


    LaunchedEffect(loginViewModel.isLoginSuccessful) {
        if (loginViewModel.isLoginSuccessful) {
            navController.navigate(LOGIN_SUCCESS_ROUTE)
        }
    }


    // Handle Auto-Focus on error
    LaunchedEffect(loginViewModel.submitError, loginViewModel.isLoading) {
        if (loginViewModel.submitError != null && !loginViewModel.isLoading) {
            when {
                loginViewModel.userIdentifierError != null -> identifierFocus.requestFocus()
                loginViewModel.passwordError != null -> passwordFocus.requestFocus()
            }
        }
    }


    LaunchedEffect(deviceSpec) {
        if (deviceSpec is DeviceSpec.MobileLandscape) {
            if (!isAnyFieldFocused.value) {
                bringIntoViewRequester.bringIntoView()
            }
        }
    }


    Scaffold(
        contentWindowInsets = if (deviceSpec is DeviceSpec.MobileLandscape) {
            WindowInsets(0.dp, 0.dp, 0.dp, 0.dp)
        }
        else {
            ScaffoldDefaults.contentWindowInsets
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier.screenModifier(contentPadding, scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            IllustrationComponent(R.drawable.signin_illustration)

            MediumSpacer()

            Text(
                text = stringResource(R.string.login_heading),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.ExtraBold
            )

            XLSpacer()

            Column(
                modifier = Modifier.fillMaxWidth(deviceSpec.textFieldWidth),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                UnifiedOutlinedTextField(
                    value = loginViewModel.userIdentifier,
                    onValueChange = loginViewModel::onIdentifierChange,
                    labelText = stringResource(R.string.email_or_phone_number_label),
                    isError = loginViewModel.userIdentifierError != null,
                    showTickCondition = { false },
                    leadingContent = {
                        val icon = when {
                            loginViewModel.userIdentifier.contains("@") -> Icons.Outlined.Email
                            loginViewModel.userIdentifier.isNotEmpty() && (loginViewModel.userIdentifier.all { it.isDigit() } || loginViewModel.userIdentifier.startsWith("+")) -> Icons.Outlined.Phone
                            else -> Icons.AutoMirrored.Outlined.Login
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (loginViewModel.userIdentifierError != null) {
                                MaterialTheme.colorScheme.error
                            }
                            else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    },
                    supportingText = {
                        Column {
                            if (loginViewModel.userIdentifier.isNotEmpty() && loginViewModel.userIdentifier.all { it.isDigit() } && !loginViewModel.userIdentifier.startsWith("+")) {
                                Text(
                                    text = stringResource(R.string.phone_number_tip),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(bottom = AppSpacing.xs)
                                )
                            }
                            ErrorTextBuilder(loginViewModel.userIdentifierError)
                        }
                    },
                    strategy = EmailFieldStrategy,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(identifierFocus)
                        .onFocusChanged {
                            isAnyFieldFocused.value = it.isFocused
                        },
                    maxCharLimit = EMAIL_MAX_SIZE,
                    showCharCount = true
                )

                MediumSpacer()

                UnifiedOutlinedTextField(
                    value = loginViewModel.password,
                    onValueChange = loginViewModel::onPasswordChange,
                    labelText = stringResource(R.string.password_field_name),
                    isError = loginViewModel.passwordError != null,
                    showTickCondition = { false },
                    supportingText = {
                        ErrorTextBuilder(loginViewModel.passwordError)
                    },
                    strategy = PasswordFieldStrategy(
                        loginViewModel.passwordVisible,
                        loginViewModel::onPasswordVisibleChange
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(passwordFocus)
                        .onFocusChanged {
                            isAnyFieldFocused.value = it.isFocused
                        },
                    maxCharLimit = PASSWORD_MAX_SIZE,
                    showCharCount = true
                )

                TextButton(
                    onClick = {
                        navController.navigate(FORGOT_PASSWORD_ROUTE_AUTH)
                    },
                    modifier = Modifier.align(alignment = Alignment.End),
                ) {
                    Text(stringResource(R.string.forgot_password))
                }

                XLSpacer()

                SubmitButton(
                    onClick = { loginViewModel.onSubmit() },
                    text = stringResource(R.string.login_button),
                    isLoading = loginViewModel.isLoading,
                    modifier = Modifier.fillMaxWidth().bringIntoViewRequester(bringIntoViewRequester)
                )

                ErrorTextBuilder(loginViewModel.submitError)

                XLSpacer()
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

                XLSpacer()
                XLSpacer()
            }
        }
    }
}