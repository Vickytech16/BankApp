package com.example.bankapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.bankapp.R
import com.example.bankapp.entities.types.account.AccountStatus
import com.example.bankapp.ui.components.LargeSpacer
import com.example.bankapp.ui.components.XLSpacer
import com.example.bankapp.ui.components.XSSpacer
import com.example.bankapp.ui.components.buttons.SubmitButton
import com.example.bankapp.ui.components.textfields.AccountNumberOutlinedTextField
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.ui.theme.LocalDeviceSpec
import com.example.bankapp.viewmodels.CashTransferViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RecipientStepContent(
    viewModel: CashTransferViewModel,
    onNext: () -> Unit
) {
    val deviceSpec = LocalDeviceSpec.current
    val textFieldColumnWidth = deviceSpec.textFieldWidth
    val scrollState = rememberScrollState()
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        XLSpacer()
        Text(
            text = stringResource(R.string.transfer_to_account_label),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.ExtraBold
        )
        LargeSpacer()
        Column(
            modifier = Modifier.fillMaxWidth(textFieldColumnWidth),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AccountNumberOutlinedTextField(
                accountNumber = viewModel.accountNumber,
                onAccountNumberChange = viewModel::onAccountNumberChange,
                accountNumberError = viewModel.accountNumberError,
                modifier = Modifier
                    .fillMaxWidth(textFieldColumnWidth)
                    .bringIntoViewRequester(bringIntoViewRequester)
                    .onFocusEvent { focusState ->
                        if (focusState.isFocused) {
                            coroutineScope.launch {
                                delay(150)
                                bringIntoViewRequester.bringIntoView()
                            }
                        }
                    }
            )
            XSSpacer()
            AccountVerificationMessage(viewModel.accountExistsStatus)
            XLSpacer()
            SubmitButton(
                onClick = { viewModel.onRecipientSubmit(onSuccess = onNext) },
                enabled = viewModel.accountExistsStatus == AccountStatus.EXISTS,
                modifier = Modifier.fillMaxWidth(textFieldColumnWidth),
                text = stringResource(R.string.continue_label)
            )
        }
    }
}

@Composable
private fun AccountVerificationMessage(accountExistsStatus: AccountStatus?) {
    if (accountExistsStatus != null) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = AppSpacing.sm),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = if (accountExistsStatus == AccountStatus.EXISTS) Icons.Default.Check else Icons.Default.Close,
                contentDescription = null,
                tint = if (accountExistsStatus == AccountStatus.EXISTS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                modifier = Modifier.size(AppSpacing.xxl)
            )
            Text(
                text = when (accountExistsStatus) {
                    AccountStatus.EXISTS -> stringResource(R.string.account_found)
                    AccountStatus.NOT_FOUND -> stringResource(R.string.account_not_found)
                    AccountStatus.ERROR -> stringResource(R.string.unable_to_verify)
                    AccountStatus.SAME_ACCOUNT -> stringResource(R.string.same_account)
                    AccountStatus.EMPTY -> stringResource(R.string.empty_field_error, stringResource(R.string.account_number_label))
                },
                style = MaterialTheme.typography.bodySmall,
                color = if (accountExistsStatus == AccountStatus.EXISTS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
    }
}