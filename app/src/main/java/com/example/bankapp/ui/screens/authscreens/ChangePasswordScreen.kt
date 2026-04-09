package com.example.bankapp.ui.screens.authscreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bankapp.R
import com.example.bankapp.di.viewmodelfactory.ChangePasswordViewModelFactory
import com.example.bankapp.ui.components.appbar.RegularAppBar
import com.example.bankapp.ui.components.ErrorTextBuilder
import com.example.bankapp.ui.components.XLSpacer
import com.example.bankapp.ui.components.MediumSpacer
import com.example.bankapp.ui.components.PasswordErrorTextBuilder
import com.example.bankapp.ui.components.buttons.SubmitButton
import com.example.bankapp.ui.components.screenModifier
import com.example.bankapp.ui.components.textfields.UnifiedOutlinedTextField
import com.example.bankapp.ui.theme.DeviceSpec
import com.example.bankapp.ui.theme.LocalDeviceSpec
import com.example.bankapp.entities.uientities.uidata.PasswordFieldStrategy
import com.example.bankapp.ui.components.BackHandlerWithWarning
import com.example.bankapp.ui.components.IllustrationComponent
import com.example.bankapp.ui.theme.submitButtonModifier
import com.example.bankapp.viewmodels.authviewmodels.ChangePasswordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    navController: NavController,
    viewModelFactory: ChangePasswordViewModelFactory,
    backRoute: String,
    popUpRoute: String,
    onSuccess: () -> Unit
) {
    val viewModel: ChangePasswordViewModel = viewModel(factory = viewModelFactory)
    val scrollState = rememberScrollState()
    val deviceSpec = LocalDeviceSpec.current
    var showExitDialog by remember { mutableStateOf(false) }
    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    val scrollBehavior = if (deviceSpec is DeviceSpec.MobileLandscape) {
        TopAppBarDefaults.enterAlwaysScrollBehavior()
    }
    else {
        null
    }


    BackHandlerWithWarning(
        onConfirm = {
            navController.navigate(backRoute) {
                popUpTo(popUpRoute) {
                    inclusive = false
                }
            }
        },
        onShowDialogConfirm = { showExitDialog = true },
        onDismiss = { showExitDialog = false },
        showDialog = showExitDialog
    )


    LaunchedEffect(viewModel.isSubmitSuccessful) {
        if (viewModel.isSubmitSuccessful) {
            onSuccess()
        }
    }


    LaunchedEffect(deviceSpec) {
        if (deviceSpec is DeviceSpec.MobileLandscape) {
            bringIntoViewRequester.bringIntoView()
        }
    }


    Scaffold(
        topBar = {
            RegularAppBar(
                title = stringResource(R.string.reset_password),
                navBehaviour = { showExitDialog = true },
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
        )
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
                    value = viewModel.password,
                    onValueChange = viewModel::onPasswordChange,
                    labelText = stringResource(R.string.password_field_name),
                    modifier = Modifier
                        .onFocusChanged {
                            if (it.isFocused) {
                                viewModel.onHasPasswordFieldEverFocusedChange(true)
                            }
                            else if (viewModel.hasPasswordFieldEverFocused) {
                                viewModel.onHasPasswordFieldEverUnFocusedChange(true)
                            }
                        }
                        .fillMaxWidth(),
                    isError = viewModel.passwordError.isNotEmpty() && viewModel.hasPasswordFieldEverUnFocused,
                    supportingText = {
                        if (viewModel.hasPasswordFieldEverUnFocused) {
                            PasswordErrorTextBuilder(viewModel.passwordError)
                        }
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
                    strategy = PasswordFieldStrategy(viewModel.confirmPasswordVisible, viewModel::onConfirmPasswordVisibleChange),
                    modifier = Modifier.fillMaxWidth()
                )

                XLSpacer()

                SubmitButton(
                    onClick = { viewModel.onSubmit() },
                    isLoading = viewModel.isLoading,
                    text = stringResource(R.string.submit_button),
                    modifier = Modifier.submitButtonModifier(deviceSpec).bringIntoViewRequester(bringIntoViewRequester)
                )

                ErrorTextBuilder(viewModel.submitError)

                XLSpacer()
                XLSpacer()
            }
        }
    }
}