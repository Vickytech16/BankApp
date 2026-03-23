package com.example.bankapp.ui.screens.authscreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
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
import androidx.compose.ui.text.input.KeyboardType

import androidx.compose.ui.text.style.TextAlign
import com.example.bankapp.viewmodels.RegisterViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bankapp.R
import com.example.bankapp.di.viewmodelfactory.RegisterViewModelFactory
import com.example.bankapp.ui.components.BackButtonHandler
import com.example.bankapp.ui.components.ErrorTextBuilder
import com.example.bankapp.ui.components.XLSpacer
import com.example.bankapp.ui.components.MediumSpacer
import com.example.bankapp.ui.components.PasswordErrorTextBuilder
import com.example.bankapp.ui.components.buttons.SubmitButton
import com.example.bankapp.ui.components.navigators.LOGIN_ROUTE
import com.example.bankapp.ui.components.textfields.GenericOutlinedTextField
import com.example.bankapp.ui.components.textfields.TrialingIconBehaviour
import com.example.bankapp.ui.components.textfields.passwordHide
import com.example.bankapp.ui.theme.AppPadding
import com.example.bankapp.ui.theme.screenPadding


@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun RegisterScreen(
    registerViewModelFactory: RegisterViewModelFactory,
    windowSizeClass: WindowSizeClass,
    navController: NavController
) {
    val viewModel: RegisterViewModel = viewModel(factory = registerViewModelFactory)

    val scrollState = rememberScrollState()

    val textFieldColumnWidth =
        when (windowSizeClass.widthSizeClass) {
            WindowWidthSizeClass.Compact -> 0.9f
            WindowWidthSizeClass.Medium -> 0.6f
            WindowWidthSizeClass.Expanded -> 0.5f
            else -> 0.8f
        }

    BackButtonHandler(navController, LOGIN_ROUTE)

    LaunchedEffect(viewModel.isSubmitSuccessful) {
        if (viewModel.isSubmitSuccessful) {
            navController.navigate("success-register")
            viewModel.reset()
        }
    }

    Scaffold {
        contentPadding ->
        Column(
            modifier = getAppModifier(windowSizeClass, contentPadding, scrollState)
                ,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        )
        {
            Image(
                painter = painterResource(id = R.drawable.signup_illustration),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.illustration_height))
                    .padding(bottom = dimensionResource(R.dimen.illustration_bottom_padding)),
                contentScale = ContentScale.Fit
            )

            MediumSpacer()

            Text(
                text = stringResource(R.string.register_new_user_headline),
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

                GenericOutlinedTextField(
                    value = viewModel.userName,
                    onValueChange = viewModel::onUserNameChange,
                    labelText = stringResource(R.string.username_field_name),
                    isError = viewModel.userNameError!=null,
                    supportingText = { ErrorTextBuilder(viewModel.userNameError) },
                    leadingIcon = Icons.Outlined.Person
                )

                MediumSpacer()

                GenericOutlinedTextField(
                    value = viewModel.email,
                    onValueChange = viewModel::onEmailChange,
                   labelText = stringResource(R.string.email_field_name),
                    modifier = Modifier.onFocusChanged {
                        if (it.isFocused)
                            viewModel.onEmailFieldSelectedChange(true)
                        else
                            viewModel.onEmailFieldSelectedChange(false)
                    }.fillMaxWidth(),
                    isError = viewModel.emailError != null && !viewModel.emailFieldSelected,
                    supportingText = {
                        if (!viewModel.emailFieldSelected)
                            ErrorTextBuilder(viewModel.emailError)
                    },
                    leadingIcon = Icons.Outlined.Email,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    )
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
                    leadingIcon = Icons.Outlined.Phone
                )

                MediumSpacer()

                GenericOutlinedTextField(
                    value = viewModel.password,
                    onValueChange = viewModel::onPasswordChange,
                    visualTransformation = passwordHide(viewModel.passwordVisible),
                    labelText = stringResource(R.string.password_field_name),
                    modifier = Modifier.onFocusChanged {
                        if (it.isFocused)
                            viewModel.onHasPasswordFieldEverFocusedChange(true)
                        else if (viewModel.hasPasswordFieldEverFocused)
                            viewModel.onHasPasswordFieldEverUnFocusedChange(true)
                    }.fillMaxWidth(),
                    isError = viewModel.passwordError.isNotEmpty() && viewModel.hasPasswordFieldEverUnFocused,
                    supportingText = {
                        if (viewModel.hasPasswordFieldEverUnFocused)
                            PasswordErrorTextBuilder(viewModel.passwordError)
                    },
                    leadingIcon = Icons.Outlined.Password,
                    trailingIcon = {
                        TrialingIconBehaviour(
                            onPasswordVisibleChange = viewModel::onPasswordVisibleChange,
                            passwordVisible = viewModel.passwordVisible
                        )
                    },
                )
                MediumSpacer()

                GenericOutlinedTextField(
                    value = viewModel.confirmPassword,
                    onValueChange = viewModel::onConfirmPasswordChange,
                    labelText = stringResource(R.string.confirm_password_field_name),
                    isError = viewModel.confirmPasswordError != null,
                    supportingText = {
                        ErrorTextBuilder(viewModel.confirmPasswordError)
                    },
                    leadingIcon = Icons.Outlined.Password,
                    trailingIcon = {
                        TrialingIconBehaviour(
                            onPasswordVisibleChange = viewModel::onConfirmPasswordVisibleChange,
                            passwordVisible = viewModel.confirmPasswordVisible
                        )
                    },
                    visualTransformation = passwordHide(viewModel.confirmPasswordVisible)
                )

                MediumSpacer()

                SubmitButton(
                    onClick = { viewModel.onSubmit() },
                    isLoading = viewModel.isLoading,
                    text = stringResource(R.string.register_button)
                )

                ErrorTextBuilder(viewModel.submitError)

                MediumSpacer()

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(R.string.already_have_an_account))
                    TextButton(onClick = {
                        navController.navigate(LOGIN_ROUTE) {
                            popUpTo(LOGIN_ROUTE) {
                                inclusive = true
                            }
                        }
                    }) {
                        Text(stringResource(R.string.login_button))
                    }
                }

                XLSpacer()

            }
        }
    }
}


fun getAppModifier(windowSizeClass: WindowSizeClass, contentPadding: PaddingValues, scrollState: ScrollState, ): Modifier{
    if(windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact || windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact){
        return AppPadding
            .verticalScroll(scrollState)
            .padding(contentPadding)
            .padding(screenPadding)
            .fillMaxHeight()
    }
    else{
        return AppPadding
            .padding(contentPadding)
            .padding(screenPadding)
            .fillMaxHeight()
            .verticalScroll(scrollState)

    }
}


