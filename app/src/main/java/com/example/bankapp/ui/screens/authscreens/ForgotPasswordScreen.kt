package com.example.bankapp.ui.screens.authscreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bankapp.R
import com.example.bankapp.di.viewmodelfactory.ForgotPasswordViewModelFactory
import com.example.bankapp.ui.components.appbar.RegularAppBar
import com.example.bankapp.ui.components.BackButtonHandler
import com.example.bankapp.ui.components.ErrorTextBuilder
import com.example.bankapp.ui.components.XLSpacer
import com.example.bankapp.ui.components.MediumSpacer
import com.example.bankapp.ui.components.buttons.SubmitButton
import com.example.bankapp.ui.components.screenModifier
import com.example.bankapp.ui.components.navigators.AUTH_ROUTE
import com.example.bankapp.ui.components.navigators.LOGIN_ROUTE
import com.example.bankapp.ui.components.textfields.UnifiedOutlinedTextField
import com.example.bankapp.ui.theme.DeviceSpec
import com.example.bankapp.ui.theme.LocalDeviceSpec
import com.example.bankapp.entities.uientities.uidata.EmailFieldStrategy
import com.example.bankapp.ui.components.IllustrationComponent
import com.example.bankapp.ui.components.navigators.AUTH_OTP
import com.example.bankapp.ui.components.navigators.FORGOT_PASSWORD_ROUTE_AUTH
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.ui.theme.submitButtonModifier
import com.example.bankapp.utilities.EMAIL_MAX_SIZE
import com.example.bankapp.viewmodels.authviewmodels.ForgotPasswordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgetPasswordScreen(
    forgotPasswordViewModelFactory: ForgotPasswordViewModelFactory,
    navController: NavController,
) {
    val viewModel: ForgotPasswordViewModel = viewModel(factory = forgotPasswordViewModelFactory)
    val scrollState = rememberScrollState()
    val deviceSpec = LocalDeviceSpec.current
    val isAnyFieldFocused = remember { mutableStateOf(false) }
    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    val identifierFocus = remember { FocusRequester() }

    val scrollBehavior = if (deviceSpec is DeviceSpec.MobileLandscape) {
        TopAppBarDefaults.enterAlwaysScrollBehavior()
    }
    else {
        null
    }

    BackButtonHandler(navController, LOGIN_ROUTE)

    LaunchedEffect(viewModel.isVerificationSuccessful) {
        if (viewModel.isVerificationSuccessful) {
            navController.navigate("$AUTH_OTP/$FORGOT_PASSWORD_ROUTE_AUTH")
        }
    }

    LaunchedEffect(viewModel.submitError, viewModel.isLoading) {
        if (viewModel.submitError != null && !viewModel.isLoading) {
            if (viewModel.userIdentifierError != null || viewModel.userIdentifier.isBlank()) {
                identifierFocus.requestFocus()
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
        topBar = {
            RegularAppBar(
                title = stringResource(R.string.forgot_password),
                navBehaviour = { navController.navigate(AUTH_ROUTE) },
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.then(
            if (scrollBehavior != null) {
                Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
            }
            else {
                Modifier
            }
        ),
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
            IllustrationComponent(R.drawable.forgot_password_illustration)

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
                modifier = Modifier.fillMaxWidth(deviceSpec.textFieldWidth),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                UnifiedOutlinedTextField(
                    value = viewModel.userIdentifier,
                    onValueChange = viewModel::onIdentifierChange,
                    labelText = stringResource(R.string.email_or_phone_number_label),
                    isError = viewModel.userIdentifierError != null,
                    showTickCondition = { false },
                    leadingContent = {
                        val icon = when {
                            viewModel.userIdentifier.contains("@") -> Icons.Outlined.Email
                            viewModel.userIdentifier.isNotEmpty() && (viewModel.userIdentifier.all { it.isDigit() } || viewModel.userIdentifier.startsWith("+")) -> Icons.Outlined.Phone
                            else -> Icons.AutoMirrored.Outlined.Login
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (viewModel.userIdentifierError != null) {
                                MaterialTheme.colorScheme.error
                            }
                            else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    },
                    supportingText = {
                        Column {
                            if (viewModel.userIdentifier.isNotEmpty() && viewModel.userIdentifier.all { it.isDigit() } && !viewModel.userIdentifier.startsWith("+")) {
                                Text(
                                    text = stringResource(R.string.phone_number_tip),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(bottom = AppSpacing.xs)
                                )
                            }
                            ErrorTextBuilder(viewModel.userIdentifierError)
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

                XLSpacer()

                SubmitButton(
                    onClick = { viewModel.onSubmit() },
                    text = stringResource(R.string.submit_button),
                    isLoading = viewModel.isLoading,
                    modifier = Modifier.submitButtonModifier(deviceSpec).bringIntoViewRequester(bringIntoViewRequester)
                )

                ErrorTextBuilder(viewModel.submitError)

                XLSpacer()
                XLSpacer()
            }
        }
    }
}