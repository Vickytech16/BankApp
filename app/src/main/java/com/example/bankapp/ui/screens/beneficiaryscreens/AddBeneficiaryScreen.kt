package com.example.bankapp.ui.screens.beneficiaryscreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.bankapp.R
import com.example.bankapp.entities.uientities.uidata.EmailFieldStrategy
import com.example.bankapp.entities.uientities.uidata.UserNameFieldStrategy
import com.example.bankapp.ui.components.*
import com.example.bankapp.ui.components.buttons.SubmitButton
import com.example.bankapp.ui.components.textfields.UnifiedOutlinedTextField
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.ui.theme.LocalDeviceSpec
import com.example.bankapp.ui.theme.DeviceSpec
import com.example.bankapp.viewmodels.AddBeneficiaryViewModel
import kotlinx.coroutines.launch

@Composable
fun AddBeneficiaryStepContent(
    viewModel: AddBeneficiaryViewModel,
    onNext: () -> Unit
) {
    val scrollState = rememberScrollState()
    val deviceSpec = LocalDeviceSpec.current
    val textFieldColumnWidth = deviceSpec.textFieldWidth
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(viewModel.isVerificationSuccessful) {
        if (viewModel.isVerificationSuccessful) {
            onNext()
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
            text = stringResource(R.string.add_beneficiary_headline),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.ExtraBold
        )

        LargeSpacer()

        Text(
            text = stringResource(R.string.add_beneficiary_description),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth(0.8f),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        XLSpacer()

        Column(
            modifier = Modifier.fillMaxWidth(textFieldColumnWidth),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            UnifiedOutlinedTextField(
                value = viewModel.userIdentifier,
                onValueChange = viewModel::onIdentifierChange,
                labelText = stringResource(R.string.email_or_phone_number_label),
                isError = viewModel.userIdentifierError != null,
                showTickCondition = {false},
                leadingContent = {
                    val icon = when {
                        viewModel.userIdentifier.contains("@") -> Icons.Outlined.Email
                        viewModel.userIdentifier.isNotEmpty() && (viewModel.userIdentifier.any { it.isDigit() } || viewModel.userIdentifier.startsWith("+")) -> Icons.Outlined.Phone
                        else -> Icons.AutoMirrored.Outlined.Login
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (viewModel.userIdentifierError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                supportingText = {
                    Column {
                        if (viewModel.showPhoneTip) {
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
                    .bringIntoViewRequester(bringIntoViewRequester)
                    .onFocusEvent {
                        if (it.isFocused) {
                            coroutineScope.launch { bringIntoViewRequester.bringIntoView() }
                        }
                    }
            )

            LargeSpacer()

            UnifiedOutlinedTextField(
                value = viewModel.nickname,
                onValueChange = viewModel::onNickNameChange,
                labelText = stringResource(R.string.nickname_optional_field_name),
                isError = viewModel.nicknameError != null,
                supportingText = { ErrorTextBuilder(viewModel.nicknameError) },
                showTickCondition = {false},
                strategy = UserNameFieldStrategy,
                modifier = Modifier
                    .bringIntoViewRequester(bringIntoViewRequester)
                    .onFocusEvent {
                        if (it.isFocused) {
                            coroutineScope.launch { bringIntoViewRequester.bringIntoView() }
                        }
                    }
            )

            XLSpacer()

            SubmitButton(
                onClick = { viewModel.onSubmit() },
                text = stringResource(R.string.submit_button),
                isLoading = viewModel.isLoading
            )

            ErrorTextBuilder(viewModel.submitError)

            XLSpacer()
        }
    }
}