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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bankapp.R
import com.example.bankapp.di.viewmodelfactory.ChangePasswordViewModelFactory
import com.example.bankapp.ui.components.appbar.Appbar
import com.example.bankapp.ui.components.BackButtonHandler
import com.example.bankapp.ui.components.ErrorTextBuilder
import com.example.bankapp.ui.components.XLSpacer
import com.example.bankapp.ui.components.MediumSpacer
import com.example.bankapp.ui.components.PasswordErrorTextBuilder
import com.example.bankapp.ui.components.buttons.SubmitButton
import com.example.bankapp.ui.components.screenModifier
import com.example.bankapp.ui.components.navigators.LOGIN_ROUTE
import com.example.bankapp.ui.components.navigators.AUTH_ROUTE
import com.example.bankapp.ui.components.textfields.UnifiedOutlinedTextField
import com.example.bankapp.ui.theme.DeviceSpecProvider
import com.example.bankapp.utilities.PasswordFieldStrategy
import com.example.bankapp.viewmodels.ChangePasswordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    windowSizeClass: WindowSizeClass,
    navController: NavController,
    viewModelFactory: ChangePasswordViewModelFactory
) {
    val scrollState = rememberScrollState()
    val viewModel: ChangePasswordViewModel = viewModel(factory = viewModelFactory)

    val deviceSpec = DeviceSpecProvider.getCurrentDeviceSpec(windowSizeClass)

    val textFieldColumnWidth =
        deviceSpec.textFieldWidth


    BackButtonHandler(navController, LOGIN_ROUTE)

    LaunchedEffect(viewModel.isSubmitSuccessful) {
        if (viewModel.isSubmitSuccessful) {
            navController.navigate(LOGIN_ROUTE)
        }
    }

    Scaffold(
        topBar = {
            Appbar(
                stringResource(R.string.reset_password),
                {
                    navController.navigate(AUTH_ROUTE) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                },
                null
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier.screenModifier(windowSizeClass,contentPadding, scrollState),
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
                    value = viewModel.password,
                    onValueChange = viewModel::onPasswordChange,
                    labelText = stringResource(R.string.password_field_name),
                    modifier = Modifier
                        .onFocusChanged {
                            if (it.isFocused)
                                viewModel.onHasPasswordFieldEverFocusedChange(true)
                            else if (viewModel.hasPasswordFieldEverFocused)
                                viewModel.onHasPasswordFieldEverUnFocusedChange(true)
                        }
                        .fillMaxWidth(),
                    isError = viewModel.passwordError.isNotEmpty() && viewModel.hasPasswordFieldEverUnFocused,
                    supportingText = {
                        if (viewModel.hasPasswordFieldEverUnFocused)
                            PasswordErrorTextBuilder(viewModel.passwordError)
                    },
                    strategy = PasswordFieldStrategy(viewModel.passwordVisible, viewModel::onPasswordVisibleChange)
                )

                MediumSpacer()

                UnifiedOutlinedTextField(
                    value = viewModel.confirmPassword,
                    onValueChange = viewModel::onConfirmPasswordChange,
                    labelText = stringResource(R.string.confirm_password_field_name),
                    isError = viewModel.confirmPasswordError != null,
                    supportingText = {
                        ErrorTextBuilder(viewModel.confirmPasswordError)
                    },
                    strategy = PasswordFieldStrategy(viewModel.passwordVisible, viewModel::onPasswordVisibleChange)
                )

                XLSpacer()

                SubmitButton(
                    onClick = {
                        viewModel.onSubmit()
                    },
                    isLoading = viewModel.isLoading,
                    text = stringResource(R.string.submit_button)
                )

                ErrorTextBuilder(viewModel.submitError)

                XLSpacer()
            }
        }
    }
}