package com.example.bankapp.temp

/*
package com.example.bankapp.ui.screens

import AmountOutlinedTextField
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bankapp.R
import com.example.bankapp.di.viewmodelfactory.CashTransferViewModelFactory
import com.example.bankapp.entities.types.AccountStatus
import com.example.bankapp.ui.components.appbar.Appbar
import com.example.bankapp.ui.components.ErrorTextBuilder
import com.example.bankapp.ui.components.MediumSpacer
import com.example.bankapp.ui.components.PasswordVerificationDialog
import com.example.bankapp.ui.components.XLSpacer
import com.example.bankapp.ui.components.XSSpacer
import com.example.bankapp.ui.components.buttons.SubmitButton
import com.example.bankapp.ui.components.navigators.CASH_TRANSFER_ROUTE
import com.example.bankapp.ui.components.navigators.HOME_OTP
import com.example.bankapp.ui.components.textfields.AccountNumberOutlinedTextField
import com.example.bankapp.ui.theme.AppPadding
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.ui.theme.screenPadding
import com.example.bankapp.viewmodels.CashTransferViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashTransferScreen(
    windowSizeClass: WindowSizeClass,
    cashTransferViewModelFactory: CashTransferViewModelFactory,
    navController: NavController,
) {
    val viewModel: CashTransferViewModel = viewModel(factory = cashTransferViewModelFactory)
    val scrollState = rememberScrollState()

    val textFieldColumnWidth =
        when (windowSizeClass.widthSizeClass) {
            WindowWidthSizeClass.Compact -> 0.9f
            WindowWidthSizeClass.Medium -> 0.6f
            WindowWidthSizeClass.Expanded -> 0.5f
            else -> 0.8f
        }

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle

    // Handle OTP result coming back
    LaunchedEffect(Unit) {
        savedStateHandle
            ?.getStateFlow<Boolean?>("otp_result", null)
            ?.collect { result ->
                if (result == true) {
                    viewModel.onShowPasswordDialogChange(true)
                    viewModel.onIsVerifySuccessfulChange(false)
                    savedStateHandle.remove<Boolean>("otp_result")
                }
            }
    }

    // Navigate to OTP when credentials are submitted
    LaunchedEffect(viewModel.isVerifySuccessful) {
        if (viewModel.isVerifySuccessful) {
            navController.navigate("$HOME_OTP/$CASH_TRANSFER_ROUTE")
        }
    }

    // Cleanup when leaving screen
    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetScreenState()
        }
    }

    if (viewModel.showPasswordDialog) {
        PasswordVerificationDialog(
            onDismiss = {
                viewModel.onShowPasswordDialogChange(false)
            },
            onSubmit = { password ->
                viewModel.onSubmit(password)
            },
            isLoading = viewModel.isLoading,
            password = viewModel.password,
            onPasswordChange = viewModel::onPasswordChange,
            passwordVisible = viewModel.passwordVisible,
            passwordError = viewModel.passwordError,
            onPasswordVisibleChange = viewModel::onPasswordVisibleChange
        )
    }

    if (viewModel.showCancelDialog) {
        ShowAlertDialog(
            showCancelDialog = viewModel.showCancelDialog,
            onShowCancelDialogChange = viewModel::onShowCancelDialogChange,
            navBehaviour = {
                navController.navigate(CASH_TRANSFER_ROUTE) {
                    popUpTo(0) { inclusive = true }
                }
            }
        )
    }

    Scaffold(
        topBar = {
            Appbar(
                stringResource(R.string.transfer_to_account_label),
                { navController.popBackStack() },
                null
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        Column(
            modifier = AppPadding
                .padding(screenPadding)
                .verticalScroll(scrollState),
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

            XLSpacer()

            Column(
                modifier = Modifier.fillMaxWidth(textFieldColumnWidth),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AccountNumberOutlinedTextField(
                    accountNumber = viewModel.accountNumber,
                    onAccountNumberChange = viewModel::onAccountNumberChange,
                    accountNumberError = viewModel.accountNumberError,
                )

                XSSpacer()

                AccountVerificationMessage(viewModel.accountExistsStatus)

                MediumSpacer()

                AmountOutlinedTextField(
                    amount = viewModel.amount.toString(),
                    onAmountChange = viewModel::onAmountChange,
                    fieldName = stringResource(R.string.amount_field_name),
                    amountError = viewModel.amountError
                )

                MediumSpacer()

                XLSpacer()

                SubmitButton(
                    onClick = { viewModel.onCredentialsSubmit() },
                    isLoading = viewModel.isLoading
                )

                ErrorTextBuilder(viewModel.submitError)

                val transactionResult = viewModel.transactionResult
                if (transactionResult != null) {
                    Text(stringResource(transactionResult.message))
                }
            }
        }
    }
}

@Composable
private fun AccountVerificationMessage(accountExistsStatus: AccountStatus?) {
    if (accountExistsStatus != null) {
        Row(
            modifier = Modifier.padding(top = AppSpacing.sm),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)
        ) {
            Icon(
                imageVector = when (accountExistsStatus) {
                    AccountStatus.EXISTS -> Icons.Default.Check
                    AccountStatus.NOT_FOUND -> Icons.Default.Close
                    AccountStatus.ERROR -> Icons.Default.Close
                    AccountStatus.SAME_ACCOUNT -> Icons.Default.Close
                },
                contentDescription = null,
                tint = when (accountExistsStatus) {
                    AccountStatus.EXISTS -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.error
                },
                modifier = Modifier.size(18.dp)
            )

            Text(
                text = when (accountExistsStatus) {
                    AccountStatus.EXISTS -> stringResource(R.string.account_found)
                    AccountStatus.NOT_FOUND -> stringResource(R.string.account_not_found)
                    AccountStatus.ERROR -> stringResource(R.string.unable_to_verify)
                    AccountStatus.SAME_ACCOUNT -> stringResource(R.string.same_account)
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

@Composable
fun ShowAlertDialog(
    navBehaviour: () -> Unit,
    showCancelDialog: Boolean,
    onShowCancelDialogChange: (Boolean) -> Unit
) {
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { onShowCancelDialogChange(false) },
            title = { Text(stringResource(R.string.cancel_transaction)) },
            text = { Text(stringResource(R.string.dismissing_Cancel_transaction_message)) },
            confirmButton = {
                Button(
                    onClick = {
                        onShowCancelDialogChange(false)
                        navBehaviour()
                    }
                ) {
                    Text(stringResource(R.string.yes_cancel_confirmation))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    onShowCancelDialogChange(false)
                }) {
                    Text(stringResource(R.string.continue_label))
                }
            }
        )
    }
}
 */