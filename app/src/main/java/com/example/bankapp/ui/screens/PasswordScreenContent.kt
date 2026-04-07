package com.example.bankapp.ui.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bankapp.R
import com.example.bankapp.di.viewmodelfactory.PasswordConfirmationViewModelFactory
import com.example.bankapp.entities.uientities.uidata.AlertButtonConfig
import com.example.bankapp.entities.uientities.uidata.PasswordFieldStrategy
import com.example.bankapp.ui.components.AlertDialogBox
import com.example.bankapp.ui.components.ErrorTextBuilder
import com.example.bankapp.ui.components.LargeSpacer
import com.example.bankapp.ui.components.XLSpacer
import com.example.bankapp.ui.components.buttons.SubmitButton
import com.example.bankapp.ui.components.textfields.UnifiedOutlinedTextField
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.ui.theme.LocalDeviceSpec
import com.example.bankapp.viewmodels.PasswordConfirmationViewModel
import com.example.bankapp.viewmodels.SessionViewModel

@Composable
fun PasswordStepContent(
    passwordConfirmationViewModelFactory: PasswordConfirmationViewModelFactory,
    onPasswordVerificationSuccess: () -> Unit,
    sessionViewModel: SessionViewModel
) {
    val viewModel: PasswordConfirmationViewModel =
        viewModel(factory = passwordConfirmationViewModelFactory)

    val deviceSpec = LocalDeviceSpec.current
    val textFieldColumnWidth = deviceSpec.textFieldWidth
    val scrollState = rememberScrollState()

    val ok = stringResource(R.string.ok)

    if (sessionViewModel.shouldLogoutOnPasswordFailure) {
        AlertDialogBox(
            onDismissRequest = { sessionViewModel.logout() },
            confirmButton = AlertButtonConfig(ok, { sessionViewModel.logout() }),
            content = { Text(stringResource(R.string.password_security_error)) },
            title = stringResource(R.string.password_security_breach)
        )
    }

    LaunchedEffect(viewModel.isPasswordVerified) {
        if (viewModel.isPasswordVerified) {
            onPasswordVerificationSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        XLSpacer()

        Text(
            text = stringResource(R.string.verify_pass),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.ExtraBold
        )

        LargeSpacer()

        Column(
            modifier = Modifier.fillMaxWidth(textFieldColumnWidth),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
        ) {
            UnifiedOutlinedTextField(
                value = viewModel.password,
                onValueChange = viewModel::onPasswordChange,
                labelText = stringResource(R.string.password_field_name),
                isError = viewModel.passwordError != null,
                supportingText = {
                    ErrorTextBuilder(viewModel.passwordError)
                },
                strategy = PasswordFieldStrategy(viewModel.passwordVisible, viewModel::onPasswordVisibleChange),
            )

            ErrorTextBuilder(viewModel.submitError)

            XLSpacer()

            SubmitButton(
                onClick = { viewModel.onSubmit(viewModel.password) },
                isLoading = viewModel.isLoading,
                enabled = viewModel.password.isNotEmpty() && !viewModel.isLoading,
                text = stringResource(R.string.submit_button)
            )

            XLSpacer()
        }
    }
}