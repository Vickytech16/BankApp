package com.example.bankapp.ui.screens.authscreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bankapp.R
import com.example.bankapp.di.viewmodelfactory.RegisterViewModelFactory
import com.example.bankapp.entities.uientities.uidata.EmailFieldStrategy
import com.example.bankapp.entities.uientities.uidata.PasswordFieldStrategy
import com.example.bankapp.entities.uientities.uidata.PhoneNumberFieldStrategy
import com.example.bankapp.entities.uientities.uidata.UserNameFieldStrategy
import com.example.bankapp.ui.components.*
import com.example.bankapp.ui.components.buttons.SubmitButton
import com.example.bankapp.ui.components.navigators.LOGIN_ROUTE
import com.example.bankapp.ui.components.navigators.RECOVERY_KEY_DISPLAY_ROUTE
import com.example.bankapp.ui.components.textfields.UnifiedOutlinedTextField
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.ui.theme.DeviceSpec
import com.example.bankapp.ui.theme.LocalDeviceSpec
import com.example.bankapp.ui.theme.submitButtonModifier
import com.example.bankapp.ui.theme.textFieldFontSize
import com.example.bankapp.utilities.EMAIL_MAX_SIZE
import com.example.bankapp.utilities.PASSWORD_MAX_SIZE
import com.example.bankapp.utilities.PHONE_NUMBER_MAX_SIZE
import com.example.bankapp.utilities.USERNAME_MAX_SIZE
import com.example.bankapp.viewmodels.authviewmodels.RegisterViewModel


@Composable
fun RegisterScreen(
    registerViewModelFactory: RegisterViewModelFactory,
    navController: NavController
) {
    val viewModel: RegisterViewModel = viewModel(factory = registerViewModelFactory)
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val deviceSpec = LocalDeviceSpec.current

    val userNameFocus = remember { FocusRequester() }
    val emailFocus = remember { FocusRequester() }
    val phoneFocus = remember { FocusRequester() }
    val passwordFocus = remember { FocusRequester() }
    val confirmPasswordFocus = remember { FocusRequester() }

    BackButtonHandler(navController, LOGIN_ROUTE)

    LaunchedEffect(uiState.isSubmitSuccessful) {
        if (uiState.isSubmitSuccessful) {
            navController.navigate("$RECOVERY_KEY_DISPLAY_ROUTE/${uiState.recoveryKey}")
            viewModel.resetSubmitStatus()
        }
    }

    LaunchedEffect(uiState.validationTrigger) {
        if (uiState.validationTrigger > 0) {

            when {
                uiState.userNameError != null -> userNameFocus.requestFocus()
                uiState.emailError != null -> emailFocus.requestFocus()

                uiState.countryError != null -> {
                    viewModel.onCountrySheetToggle(true)
                }

                uiState.isTimezoneFieldVisible && uiState.timezoneError != null -> {
                    viewModel.onTimeZoneSheetToggle(true)
                }

                uiState.phoneNumberError != null -> phoneFocus.requestFocus()
                uiState.passwordError.isNotEmpty() -> passwordFocus.requestFocus()
                uiState.confirmPasswordError != null -> confirmPasswordFocus.requestFocus()
            }
        }
    }

    Scaffold(
        contentWindowInsets = if (deviceSpec is DeviceSpec.MobileLandscape) {
            WindowInsets(0.dp, 0.dp, 0.dp, 0.dp)
        } else {
            ScaffoldDefaults.contentWindowInsets
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier.screenModifier(contentPadding, scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            IllustrationComponent(R.drawable.signup_illustration)
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
                modifier = Modifier.fillMaxWidth(deviceSpec.textFieldWidth),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                UnifiedOutlinedTextField(
                    value = uiState.userName,
                    onValueChange = viewModel::onUserNameChange,
                    labelText = stringResource(R.string.username_field_name),
                    isError = uiState.userNameError != null && uiState.hasUserNameUnFocused,
                    supportingText = { if (uiState.hasUserNameUnFocused) ErrorTextBuilder(uiState.userNameError) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(userNameFocus)
                        .onFocusChanged { viewModel.onHasUserNameFocusChange(it.isFocused) },
                    strategy = UserNameFieldStrategy,
                    showTickCondition = { viewModel.isUserNameValid() },
                    maxCharLimit = USERNAME_MAX_SIZE,
                    showCharCount = true
                )

                MediumSpacer()

                UnifiedOutlinedTextField(
                    value = uiState.email,
                    onValueChange = viewModel::onEmailChange,
                    labelText = stringResource(R.string.email_field_name),
                    isError = uiState.emailError != null && uiState.hasEmailUnFocused,
                    supportingText = { if (uiState.hasEmailUnFocused) ErrorTextBuilder(uiState.emailError) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(emailFocus)
                        .onFocusChanged { viewModel.onEmailFocusChange(it.isFocused) },
                    strategy = EmailFieldStrategy,
                    showTickCondition = { viewModel.isEmailValid() },
                    maxCharLimit = EMAIL_MAX_SIZE,
                    showCharCount = true
                )

                MediumSpacer()

                DropDownPickerField(
                    label = stringResource(R.string.country_field_name),
                    selectedValue = uiState.selectedCountry?.name ?: "",
                    placeholder = stringResource(R.string.select_your_country_placeholder),
                    items = viewModel.countries.collectAsState().value,
                    onItemSelected = { viewModel.onCountrySelected(it) },
                    itemLabel = { it.name },
                    itemSecondaryLabel = { it.countryCode },
                    itemLeadingIcon = { Text(it.emoji, fontSize = textFieldFontSize) },
                    showSheet = uiState.isCountrySheetVisible,
                    onShowSheetChange = viewModel::onCountrySheetToggle,
                    searchQuery = uiState.countrySearchQuery,
                    onSearchQueryChange = viewModel::onCountrySearchQueryChange,
                    isError = uiState.countryError != null,
                    errorBehaviour = { ErrorTextBuilder(uiState.countryError) },
                    searchPlaceholder = stringResource(R.string.country_field_name),
                    leadingIcon = {
                        uiState.selectedCountry?.let {
                            Text(it.emoji, fontSize = textFieldFontSize)
                        } ?: Icon(Icons.Outlined.Public, null)
                    }
                )

                if (uiState.isTimezoneFieldVisible) {
                    XLSpacer()
                    DropDownPickerField(
                        label = stringResource(R.string.timezone_label),
                        selectedValue = uiState.selectedTimezone ?: "",
                        placeholder = stringResource(R.string.local_time_zone),
                        items = uiState.selectedCountry?.timezones ?: emptyList(),
                        onItemSelected = { viewModel.onTimeZoneSelected(it) },
                        itemLabel = { it },
                        itemLeadingIcon = { Icon(Icons.Outlined.Schedule, null) },
                        showSheet = uiState.isTimezoneSheetVisible,
                        onShowSheetChange = viewModel::onTimeZoneSheetToggle,
                        searchQuery = uiState.timezoneSearchQuery,
                        onSearchQueryChange = viewModel::onTimeZoneSearchQueryChange,
                        isError = uiState.timezoneError != null,
                        errorBehaviour = { ErrorTextBuilder(uiState.timezoneError) },
                        skipPartiallyExpanded = true,
                        searchPlaceholder = stringResource(R.string.timezone_label),
                        leadingIcon = { Icon(Icons.Outlined.Schedule, null) }
                    )
                }

                XLSpacer()

                UnifiedOutlinedTextField(
                    value = uiState.phoneNumber,
                    onValueChange = viewModel::onPhoneNumberChange,
                    enabled = uiState.selectedCountry != null,
                    labelText = stringResource(R.string.phone_number_field_name),
                    isError = uiState.phoneNumberError != null && uiState.hasPhoneUnFocused,
                    placeholderText = if (uiState.selectedCountry == null) stringResource(R.string.select_country_first) else "",
                    leadingContent = {
                        uiState.selectedCountry?.let {
                            Text(it.emoji, fontSize = textFieldFontSize, modifier = Modifier.padding(start = AppSpacing.md))
                        } ?: Icon(Icons.Outlined.Phone, null)
                    },
                    prefix = {
                        uiState.selectedCountry?.let {
                            Text(text = "${it.phonePrefix} ", style = TextStyle(fontSize = textFieldFontSize, fontWeight = FontWeight.SemiBold))
                        }
                    },
                    supportingText = { if (uiState.hasPhoneUnFocused) ErrorTextBuilder(uiState.phoneNumberError) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(phoneFocus)
                        .onFocusChanged { viewModel.onHasPhoneFocusChange(it.isFocused) },
                    strategy = PhoneNumberFieldStrategy,
                    showTickCondition = { viewModel.isPhoneValid() },
                    maxCharLimit = PHONE_NUMBER_MAX_SIZE,
                    showCharCount = true
                )

                MediumSpacer()

                UnifiedOutlinedTextField(
                    value = uiState.password,
                    onValueChange = viewModel::onPasswordChange,
                    labelText = stringResource(R.string.password_field_name),
                    isError = uiState.passwordError.isNotEmpty() && uiState.hasPasswordUnFocused,
                    supportingText = { if (uiState.hasPasswordUnFocused) PasswordErrorTextBuilder(uiState.passwordError) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(passwordFocus) // Linked
                        .onFocusChanged { viewModel.onHasPasswordFocusChange(it.isFocused) },
                    strategy = PasswordFieldStrategy(uiState.isPasswordVisible, viewModel::onPasswordVisibleToggle),
                    showTickCondition = { viewModel.isPasswordValid() },
                    maxCharLimit = PASSWORD_MAX_SIZE,
                    showCharCount = true
                )

                MediumSpacer()

                UnifiedOutlinedTextField(
                    value = uiState.confirmPassword,
                    onValueChange = viewModel::onConfirmPasswordChange,
                    labelText = stringResource(R.string.confirm_password_field_name),
                    isError = uiState.confirmPasswordError != null,
                    supportingText = { ErrorTextBuilder(uiState.confirmPasswordError) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(confirmPasswordFocus),
                    strategy = PasswordFieldStrategy(uiState.isConfirmPasswordVisible, viewModel::onConfirmPasswordVisibleToggle),
                    showTickCondition = { viewModel.isConfirmPasswordValid() },
                    maxCharLimit = PASSWORD_MAX_SIZE,
                    showCharCount = true
                )

                MediumSpacer()
                XLSpacer()

                SubmitButton(
                    onClick = { viewModel.onSubmit() },
                    isLoading = uiState.isLoading,
                    text = stringResource(R.string.register_button),
                    modifier = Modifier.submitButtonModifier(deviceSpec)
                )

                ErrorTextBuilder(uiState.submitError)

                XLSpacer()

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = stringResource(R.string.already_have_an_account))
                    TextButton(onClick = {
                        navController.navigate(LOGIN_ROUTE) {
                            popUpTo(LOGIN_ROUTE) { inclusive = true }
                        }
                    }) { Text(stringResource(R.string.login_button)) }
                }

                XLSpacer()
            }
        }
    }
}