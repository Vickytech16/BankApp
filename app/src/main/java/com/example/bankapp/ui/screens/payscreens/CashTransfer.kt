package com.example.bankapp.ui.screens.payscreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bankapp.R
import com.example.bankapp.di.viewmodelfactory.CashTransferViewModelFactory
import com.example.bankapp.entities.types.account.AccountStatus
import com.example.bankapp.ui.components.appbar.Appbar
import com.example.bankapp.ui.components.ErrorTextBuilder
import com.example.bankapp.ui.components.MediumSpacer
import com.example.bankapp.ui.components.XLSpacer
import com.example.bankapp.ui.components.XSSpacer
import com.example.bankapp.ui.components.buttons.SubmitButton
import com.example.bankapp.ui.components.navigators.CASH_TRANSFER_ROUTE
import com.example.bankapp.ui.components.navigators.HOME_OTP
import com.example.bankapp.entities.types.transaction.TransactionType
import com.example.bankapp.ui.components.AlertButtonConfig
import com.example.bankapp.ui.components.AlertDialogBox
import com.example.bankapp.ui.components.BalanceStatusCard
import com.example.bankapp.ui.components.ExchangeDisplayData
import com.example.bankapp.ui.components.LargeSpacer
import com.example.bankapp.ui.components.screenModifier
import com.example.bankapp.ui.components.textfields.AccountNumberOutlinedTextField
import com.example.bankapp.ui.components.textfields.UnifiedOutlinedTextField
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.ui.theme.LocalDeviceSpec
import com.example.bankapp.utilities.AmountFieldStrategy
import com.example.bankapp.utilities.CurrencyUtils
import com.example.bankapp.utilities.toDbAccNo
import com.example.bankapp.viewmodels.CashTransferViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashTransferScreen(
    windowSizeClass: WindowSizeClass,
    cashTransferViewModelFactory: CashTransferViewModelFactory,
    navController: NavController,
    friendAccNo: String? = null
) {

    val viewModel: CashTransferViewModel = viewModel(factory = cashTransferViewModelFactory)

    val user by viewModel.user.collectAsState()
    val account by viewModel.account.collectAsState()

    if (viewModel.showInternetAlert) {
        AlertDialogBox(
            onDismissRequest = { viewModel.showInternetAlert = false },
            title = stringResource(R.string.internet_required),
            content = { Text(text = stringResource(R.string.internet_required_content) )},
            confirmButton = AlertButtonConfig(stringResource(R.string.ok), { viewModel.showInternetAlert = false })
        )
    }

    LaunchedEffect(Unit) {
        friendAccNo?.let {
            viewModel.onFriendPay(friendAccNo)
        }
    }

    val scrollState = rememberScrollState()

    val illustrationHeight = dimensionResource(R.dimen.illustration_height).value.toInt()

    LaunchedEffect(windowSizeClass.heightSizeClass) {
        if (windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact) {
            scrollState.animateScrollTo(illustrationHeight + 400)
        }
    }

    LaunchedEffect(viewModel.isVerifySuccessful) {
        if (viewModel.isVerifySuccessful ) {
            navController.navigate("$HOME_OTP/$CASH_TRANSFER_ROUTE")
        }
    }

    val deviceSpec = LocalDeviceSpec.current

    val textFieldColumnWidth = deviceSpec.textFieldWidth

    Scaffold(
        topBar = {
            Appbar(
                stringResource(R.string.transfer_to_account_label),
                navBehaviour = {
                    navController.popBackStack()
                },
                scrollBehavior = null
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        contentPadding ->
        Column(
            modifier = Modifier.screenModifier(contentPadding, scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            val balanceValue = account.balance

            val exchangeInfo = if (viewModel.isInternational &&
                viewModel.amount.isNotEmpty() &&
                viewModel.accountExistsStatus == AccountStatus.EXISTS) {
                ExchangeDisplayData(
                    convertedAmount = viewModel.convertedAmountDisplay,
                    lastUpdated = viewModel.lastUpdatedTime
                )
            } else null

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
                balanceValue = CurrencyUtils.formatCurrency(balanceValue, user.countryCode) + " " + CurrencyUtils.getCurrencyCode(user.countryCode),
                exchangeInfo = exchangeInfo
            )

            LargeSpacer()

            Column(
                modifier = Modifier.fillMaxWidth(textFieldColumnWidth),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if (friendAccNo?.toDbAccNo() == null || friendAccNo.toDbAccNo() == 0.toLong()) {
                    AccountNumberOutlinedTextField(
                        viewModel.accountNumber,
                        viewModel::onAccountNumberChange,
                        viewModel.accountNumberError
                    )

                    XSSpacer()

                    AccountVerificationMessage(viewModel.accountExistsStatus)

                    MediumSpacer()
                }

                UnifiedOutlinedTextField(
                    value = viewModel.amount,
                    onValueChange = viewModel::onAmountChange,
                    labelText = stringResource(R.string.amount_field_name),
                    isError = viewModel.amountError != null,
                    supportingText = {
                        ErrorTextBuilder(viewModel.amountError)
                    },
                    strategy = AmountFieldStrategy(TransactionType.CASH_TRANSFER),
                )

                XLSpacer()

                SubmitButton(
                    onClick = {
                        viewModel.onCredentialsSubmit()
                    },
                    isLoading = viewModel.isLoading
                )

                ErrorTextBuilder(viewModel.submitError)
            }
        }

            XLSpacer()
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
                imageVector = when (accountExistsStatus) {
                    AccountStatus.EXISTS -> Icons.Default.Check
                    AccountStatus.NOT_FOUND -> Icons.Default.Close
                    AccountStatus.ERROR -> Icons.Default.Close
                    AccountStatus.SAME_ACCOUNT -> Icons.Default.Close
                    AccountStatus.EMPTY -> Icons.Default.Close
                },
                contentDescription = null,
                tint = when (accountExistsStatus) {
                    AccountStatus.EXISTS -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.error
                },
                modifier = Modifier.size(AppSpacing.xl)
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
                color = when (accountExistsStatus) {
                    AccountStatus.EXISTS -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.error
                }
            )
        }
    }
}

