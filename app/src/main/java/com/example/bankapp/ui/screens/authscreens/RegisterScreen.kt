package com.example.bankapp.ui.screens.authscreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.bankapp.viewmodels.RegisterViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bankapp.R
import com.example.bankapp.di.viewmodelfactory.RegisterViewModelFactory
import com.example.bankapp.entities.dtos.Country
import com.example.bankapp.ui.components.BackButtonHandler
import com.example.bankapp.ui.components.DropDownPickerField
import com.example.bankapp.ui.components.ErrorTextBuilder
import com.example.bankapp.ui.components.XLSpacer
import com.example.bankapp.ui.components.MediumSpacer
import com.example.bankapp.ui.components.PasswordErrorTextBuilder
import com.example.bankapp.ui.components.buttons.SubmitButton
import com.example.bankapp.ui.components.screenModifier
import com.example.bankapp.ui.components.navigators.LOGIN_ROUTE
import com.example.bankapp.ui.components.textfields.UnifiedOutlinedTextField
import com.example.bankapp.ui.theme.DeviceSpecProvider
import com.example.bankapp.utilities.EmailFieldStrategy
import com.example.bankapp.utilities.PasswordFieldStrategy
import com.example.bankapp.utilities.PhoneNumberFieldStrategy
import com.example.bankapp.utilities.UserNameFieldStrategy


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

    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    LaunchedEffect(windowSizeClass.heightSizeClass) {
        if (windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact) {
            bringIntoViewRequester.bringIntoView()
        }
    }

    val deviceSpec = DeviceSpecProvider.getCurrentDeviceSpec(windowSizeClass)

    Scaffold {
        contentPadding ->
        Column(
            modifier = Modifier.screenModifier(windowSizeClass,contentPadding, scrollState),
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

                UnifiedOutlinedTextField(
                    value = viewModel.userName,
                    onValueChange = viewModel::onUserNameChange,
                    labelText = stringResource(R.string.username_field_name),
                    isError = viewModel.userNameError != null,
                    supportingText = {
                        ErrorTextBuilder(viewModel.userNameError)
                    },
                    strategy = UserNameFieldStrategy
                )

                MediumSpacer()

                UnifiedOutlinedTextField(
                    value = viewModel.email,
                    onValueChange = viewModel::onEmailChange,
                    labelText = stringResource(R.string.email_field_name),
                    isError = viewModel.emailError != null && !viewModel.emailFieldSelected,
                    supportingText = {
                        if (!viewModel.emailFieldSelected)
                            ErrorTextBuilder(viewModel.emailError)
                    },
                    modifier = Modifier.onFocusChanged {
                        if (it.isFocused)
                            viewModel.onEmailFieldSelectedChange(true)
                        else
                            viewModel.onEmailFieldSelectedChange(false)
                    }.fillMaxWidth(),
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
                    strategy = PhoneNumberFieldStrategy,
                    modifier = Modifier.bringIntoViewRequester(bringIntoViewRequester)
                )

                MediumSpacer()

                DropDownPickerField(
                    label = "Country",
                    selectedValue = viewModel.selectedCountry?.name ?: "",
                    placeholder = stringResource(R.string.select_your_country_placeholder),
                    items = viewModel.countries.collectAsState().value,
                    onItemSelected = { viewModel.onCountrySelected(it) },
                    itemLabel = { it.name },
                    itemSecondaryLabel = { it.countryCode },
                    itemLeadingIcon = { Text(it.emoji, fontSize = 20.sp) },
                    showSheet = viewModel.isCountrySheetVisible,
                    onShowSheetChange = { viewModel.isCountrySheetVisible = it },
                    searchQuery = viewModel.countrySearchQuery,
                    onSearchQueryChange = { viewModel.countrySearchQuery = it },
                    isError = viewModel.countryError != null,
                    errorBehaviour = { ErrorTextBuilder(viewModel.countryError) }
                )

                MediumSpacer()

                if (viewModel.isTimezoneFieldVisible) {
                    DropDownPickerField(
                        label = stringResource(R.string.timezone_label),
                        selectedValue = viewModel.selectedTimezone ?: "",
                        placeholder = stringResource(R.string.local_time_zone),
                        items = viewModel.selectedCountry?.timezones ?: emptyList(),
                        onItemSelected = { viewModel.onTimeZoneChange(it) },
                        itemLabel = { it },
                        itemLeadingIcon = { Icon(Icons.Outlined.Schedule, null) },
                        showSheet = viewModel.isTimezoneSheetVisible,
                        onShowSheetChange = { viewModel.isTimezoneSheetVisible = it },
                        searchQuery = viewModel.timezoneSearchQuery,
                        onSearchQueryChange = { viewModel.timezoneSearchQuery = it },
                        isError = viewModel.timezoneError != null,
                        errorBehaviour = { ErrorTextBuilder(viewModel.timezoneError) }

                    )
                }

                MediumSpacer()

                UnifiedOutlinedTextField(
                    value = viewModel.password,
                    onValueChange = viewModel::onPasswordChange,
                    labelText = stringResource(R.string.password_field_name),
                    isError = viewModel.passwordError.isNotEmpty() && viewModel.hasPasswordFieldEverUnFocused,
                    supportingText = {
                        if (viewModel.hasPasswordFieldEverUnFocused)
                            PasswordErrorTextBuilder(viewModel.passwordError)
                    },
                    modifier = Modifier.onFocusChanged {
                        if (it.isFocused)
                            viewModel.onHasPasswordFieldEverFocusedChange(true)
                        else if (viewModel.hasPasswordFieldEverFocused)
                            viewModel.onHasPasswordFieldEverUnFocusedChange(true)
                    }.fillMaxWidth(),
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
                    strategy = PasswordFieldStrategy(viewModel.confirmPasswordVisible, viewModel::onConfirmPasswordVisibleChange)
                )

                MediumSpacer()

                XLSpacer()

                SubmitButton(
                    onClick = { viewModel.onSubmit() },
                    isLoading = viewModel.isLoading,
                    text = stringResource(R.string.register_button),
                )

                ErrorTextBuilder(viewModel.submitError)


                XLSpacer()

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
