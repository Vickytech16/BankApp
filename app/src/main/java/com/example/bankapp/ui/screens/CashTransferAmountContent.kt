package com.example.bankapp.ui.screens

import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bankapp.R
import com.example.bankapp.entities.types.account.AccountStatus
import com.example.bankapp.entities.types.transaction.TransactionType
import com.example.bankapp.entities.uientities.uidata.AmountFieldStrategy
import com.example.bankapp.entities.uientities.uidata.BalanceCardExchangeDisplayData
import com.example.bankapp.ui.components.*
import com.example.bankapp.ui.components.buttons.SubmitButton
import com.example.bankapp.ui.components.textfields.UnifiedOutlinedTextField
import com.example.bankapp.ui.theme.LocalDeviceSpec
import com.example.bankapp.utilities.CurrencyUtils
import com.example.bankapp.viewmodels.CashTransferViewModel
import kotlinx.coroutines.launch

@Composable
fun AmountStepContent(
    viewModel: CashTransferViewModel,
    onNext: () -> Unit
) {
    val user by viewModel.user.collectAsStateWithLifecycle()
    val account by viewModel.account.collectAsStateWithLifecycle()
    val deviceSpec = LocalDeviceSpec.current
    val textFieldColumnWidth = deviceSpec.textFieldWidth
    val scrollState = rememberScrollState()
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(viewModel.isVerifySuccessful) {
        if (viewModel.isVerifySuccessful) {
            onNext()
        }
    }

    LaunchedEffect(Unit) {
        if (viewModel.accountNumber.isNotEmpty() && viewModel.accountExistsStatus == AccountStatus.EXISTS) {
            viewModel.onRecipientSubmit(onSuccess = {})
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
        val exchangeInfo = if (viewModel.isInternational &&
            viewModel.amount.isNotEmpty() &&
            viewModel.accountExistsStatus == AccountStatus.EXISTS) {
            BalanceCardExchangeDisplayData(
                convertedAmount = viewModel.convertedAmountDisplay,
                lastUpdated = viewModel.lastUpdatedTime
            )
        } else null

        BalanceStatusCard(
            modifier = Modifier.fillMaxWidth(textFieldColumnWidth),
            balanceValue = CurrencyUtils.formatCurrency(account.balance, user.countryCode) + " " + CurrencyUtils.getCurrencyCode(user.countryCode),
            exchangeInfo = exchangeInfo
        )
        LargeSpacer()
        Column(
            modifier = Modifier.fillMaxWidth(textFieldColumnWidth),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            UnifiedOutlinedTextField(
                value = viewModel.amount,
                onValueChange = viewModel::onAmountChange,
                labelText = stringResource(R.string.amount_field_name),
                isError = viewModel.amountError != null,
                supportingText = { ErrorTextBuilder(viewModel.amountError) },
                strategy = AmountFieldStrategy(TransactionType.CASH_TRANSFER),
                modifier = Modifier
                    .bringIntoViewRequester(bringIntoViewRequester)
                    .onFocusEvent { if (it.isFocused) coroutineScope.launch { bringIntoViewRequester.bringIntoView() } }
            )
            XLSpacer()
            SubmitButton(
                onClick = { viewModel.onCredentialsSubmit() },
                isLoading = viewModel.isLoading
            )
            ErrorTextBuilder(viewModel.submitError)
        }
    }
}