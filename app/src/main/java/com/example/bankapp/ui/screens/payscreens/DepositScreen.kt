package com.example.bankapp.ui.screens.payscreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bankapp.R
import com.example.bankapp.di.viewmodelfactory.DepositViewModelFactory
import com.example.bankapp.ui.components.ErrorTextBuilder
import com.example.bankapp.ui.components.XLSpacer
import com.example.bankapp.ui.components.buttons.SubmitButton
import com.example.bankapp.entities.types.transaction.TransactionType
import com.example.bankapp.ui.components.BalanceStatusCard
import com.example.bankapp.ui.components.LargeSpacer
import com.example.bankapp.ui.components.textfields.UnifiedOutlinedTextField
import com.example.bankapp.ui.theme.DeviceSpec
import com.example.bankapp.ui.theme.LocalDeviceSpec
import com.example.bankapp.entities.uientities.uidata.AmountFieldStrategy
import com.example.bankapp.utilities.CurrencyUtils
import com.example.bankapp.viewmodels.DepositViewModel
import kotlinx.coroutines.launch
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepositStepContent(
    depositViewModelFactory: DepositViewModelFactory,
    onSuccess: () -> Unit,
) {
    val viewModel: DepositViewModel = viewModel(factory = depositViewModelFactory)
    val scrollState = rememberScrollState()
    val deviceSpec = LocalDeviceSpec.current
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    val textFieldColumnWidth = deviceSpec.textFieldWidth

    LaunchedEffect(viewModel.isVerifySuccessful) {
        if (viewModel.isVerifySuccessful) {
            onSuccess()
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
            text = stringResource(R.string.enter_transaction_details),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.ExtraBold
        )

        LargeSpacer()

        BalanceStatusCard(
            modifier = Modifier.fillMaxWidth(textFieldColumnWidth),
            balanceValue = CurrencyUtils.formatCurrency(
                viewModel.account.balance,
                viewModel.user.countryCode
            ) + " " + CurrencyUtils.getCurrencyCode(viewModel.user.countryCode),
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
                supportingText = {
                    ErrorTextBuilder(viewModel.amountError)
                },
                strategy = AmountFieldStrategy(TransactionType.DEPOSIT),
                modifier = Modifier
                    .bringIntoViewRequester(bringIntoViewRequester)
                    .onFocusEvent {
                        if (it.isFocused && deviceSpec is DeviceSpec.MobileLandscape) {
                            coroutineScope.launch { bringIntoViewRequester.bringIntoView() }
                        }
                    }
            )

            XLSpacer()

            SubmitButton(
                onClick = { viewModel.onSubmit() },
                isLoading = viewModel.isLoading,
                enabled = viewModel.amount.isNotEmpty() && viewModel.amountError == null && !viewModel.isLoading
            )

            ErrorTextBuilder(viewModel.submitError)

            if (deviceSpec is DeviceSpec.MobileLandscape) {
                Spacer(modifier = Modifier.height(400.dp))
            }
        }
    }
}