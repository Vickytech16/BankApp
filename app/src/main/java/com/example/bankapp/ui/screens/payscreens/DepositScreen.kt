package com.example.bankapp.ui.screens.payscreens

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bankapp.R
import com.example.bankapp.di.viewmodelfactory.DepositViewModelFactory
import com.example.bankapp.ui.components.appbar.RegularAppBar
import com.example.bankapp.ui.components.ErrorTextBuilder
import com.example.bankapp.ui.components.XLSpacer
import com.example.bankapp.ui.components.buttons.SubmitButton
import com.example.bankapp.ui.components.navigators.DEPOSIT_ROUTE
import com.example.bankapp.ui.components.navigators.HOME_OTP
import com.example.bankapp.entities.types.transaction.TransactionType
import com.example.bankapp.ui.components.BalanceStatusCard
import com.example.bankapp.ui.components.LargeSpacer
import com.example.bankapp.ui.components.screenModifier
import com.example.bankapp.ui.components.textfields.UnifiedOutlinedTextField
import com.example.bankapp.ui.theme.DeviceSpec
import com.example.bankapp.ui.theme.LocalDeviceSpec
import com.example.bankapp.entities.uientities.uidata.AmountFieldStrategy
import com.example.bankapp.utilities.CurrencyUtils
import com.example.bankapp.viewmodels.DepositViewModel
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepositScreen(
    depositViewModelFactory: DepositViewModelFactory,
    navController: NavController,
) {
    val viewModel: DepositViewModel = viewModel(factory = depositViewModelFactory)

    val scrollState = rememberScrollState()

    dimensionResource(R.dimen.illustration_height).value.toInt()

    val deviceSpec = LocalDeviceSpec.current

    val balanceAfter = viewModel.account.balance.toString()

    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    LaunchedEffect(deviceSpec) {
        if (deviceSpec is DeviceSpec.MobileLandscape) {
            bringIntoViewRequester.bringIntoView()
        }
    }

    val textFieldColumnWidth =
        deviceSpec.textFieldWidth

    LaunchedEffect(viewModel.isVerifySuccessful) {
        if (viewModel.isVerifySuccessful) {
            navController.navigate("$HOME_OTP/$DEPOSIT_ROUTE")
        }
    }

    Scaffold(
        topBar = {
            RegularAppBar(
                stringResource(R.string.deposit_label),
                { navController.popBackStack() },
                null
            )
        },
    )
    {
        contentPadding ->
        Column(
            modifier = Modifier.screenModifier(contentPadding, scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
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
                balanceValue = CurrencyUtils.formatCurrency(balanceAfter.toBigDecimalOrNull() ?: BigDecimal.ZERO,viewModel. user.countryCode) + " " + CurrencyUtils.getCurrencyCode(viewModel.user.countryCode),
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
                )

                XLSpacer()

                SubmitButton(
                    onClick = { viewModel.onSubmit() },
                    isLoading = viewModel.isLoading,
                    modifier = Modifier.bringIntoViewRequester(bringIntoViewRequester)
                )

                ErrorTextBuilder(viewModel.submitError)

                XLSpacer()
            }
        }

    }
}