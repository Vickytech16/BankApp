package com.example.bankapp.ui.screens

import AmountOutlinedTextField
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
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
import com.example.bankapp.ui.components.XLSpacer
import com.example.bankapp.ui.components.XSSpacer
import com.example.bankapp.ui.components.buttons.SubmitButton
import com.example.bankapp.ui.components.navigators.CASH_TRANSFER_ROUTE
import com.example.bankapp.ui.components.navigators.HOME_OTP
import com.example.bankapp.ui.components.navigators.HOME_ROUTE
import com.example.bankapp.ui.components.navigators.INDIVIDUAL_TRANSACTION_LOG_ROUTE
import com.example.bankapp.ui.components.navigators.MAIN_ROUTE
import com.example.bankapp.ui.components.navigators.PASSWORD_CONFIRMATION_ROUTE
import com.example.bankapp.ui.components.navigators.TRANSACTION_RESULT_ROUTE
import com.example.bankapp.di.HomeSessionHandlerProvider
import com.example.bankapp.ui.components.textfields.AccountNumberOutlinedTextField
import com.example.bankapp.ui.theme.AppPadding
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.usecases.CurrentSessionIntent
import com.example.bankapp.usecases.HomeSessionHandler
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

    LaunchedEffect(Unit) {
        HomeSessionHandlerProvider.setHandlerByIntent(CurrentSessionIntent.CASH_TRANSFER)
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

        if (viewModel.isVerifySuccessful && !viewModel.isNavigationSet) {

            viewModel.isNavigationSet = true

            val homeSessionHandler = HomeSessionHandlerProvider.currentHandler
            val cashTransfer = homeSessionHandler as HomeSessionHandler.CashTransfer

            cashTransfer.navigationLocked = false

            cashTransfer.onActionSuccessPrimaryAction = {
                if (!cashTransfer.navigationLocked) {
                    cashTransfer.navigationLocked = true
                    val transactionId = cashTransfer.transactionId

                    if (transactionId != null) {
                        navController.navigate("$INDIVIDUAL_TRANSACTION_LOG_ROUTE/$transactionId") {
                            popUpTo(HOME_ROUTE) { inclusive = false }
                        }
                    } else {
                        navController.navigate(HOME_ROUTE) {
                            popUpTo(MAIN_ROUTE) {
                                inclusive = true
                            }
                        }
                    }
                }
            }

            cashTransfer.onActionFailurePrimaryAction = {
                if (!cashTransfer.navigationLocked) {
                    cashTransfer.navigationLocked= true
                    navController.navigate(CASH_TRANSFER_ROUTE) {
                        popUpTo(HOME_ROUTE) {
                            inclusive = false
                        }
                    }
                }
            }

            homeSessionHandler.onOtpSuccess = {
                if (!cashTransfer.navigationLocked) {
//                    cashTransfer.navigationLocked = true
                    if(!cashTransfer.isFriend) {
                        navController.navigate("$PASSWORD_CONFIRMATION_ROUTE/$CASH_TRANSFER_ROUTE")
                    } else {
                        homeSessionHandler.onPasswordSuccess.invoke()
                    }
                }
            }

            homeSessionHandler.onPasswordSuccess = {
                if (!cashTransfer.navigationLocked) {
//                    cashTransfer.navigationLocked = true
                    navController.navigate(TRANSACTION_RESULT_ROUTE)
                }
            }

            navController.navigate("$HOME_OTP/$CASH_TRANSFER_ROUTE")
        }

    }

    val textFieldColumnWidth =
        when (windowSizeClass.widthSizeClass) {
            WindowWidthSizeClass.Compact -> 0.8f
            WindowWidthSizeClass.Medium -> 0.6f
            WindowWidthSizeClass.Expanded -> 0.5f
            else -> 0.8f
        }



//    DisposableEffect(Unit) {
//        onDispose {
//            viewModel.resetScreenState()
//        }
//    }

    Scaffold(
        topBar = {
            Appbar(
                stringResource(R.string.transfer_to_account_label),
                {
                    navController.popBackStack()
                },
                null
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        contentPadding ->
        Column(
            modifier = AppPadding.padding(contentPadding).fillMaxHeight().verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.signup_illustration),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.illustration_height))
                    .padding(bottom = dimensionResource(R.dimen.illustration_bottom_padding)),
                contentScale = ContentScale.Fit
            )

            MediumSpacer()

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

                if(friendAccNo?.toDbAccNo()==null || friendAccNo.toDbAccNo()==0.toLong()) {
                    AccountNumberOutlinedTextField(
                        accountNumber = viewModel.accountNumber,
                        onAccountNumberChange = viewModel::onAccountNumberChange,
                        accountNumberError = viewModel.accountNumberError,
                    )

                    XSSpacer()

                    AccountVerificationMessage(viewModel.accountExistsStatus)

                    MediumSpacer()
                }

                AmountOutlinedTextField(
                    amount = viewModel.amount,
                    onAmountChange = viewModel::onAmountChange,
                    fieldName = stringResource(R.string.amount_field_name),
                    amountError = viewModel.amountError
                )

                MediumSpacer()

                XLSpacer()

                SubmitButton(
                    onClick = {
                        viewModel.onCredentialsSubmit()
                    },
                    isLoading = viewModel.isLoading
                )

                ErrorTextBuilder(viewModel.submitError)
            }

            XLSpacer()
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


/*
// Password dialog (for future use)
if (viewModel.showPasswordDialog) {
    PasswordVerificationDialog(
        onDismiss = { /* handle */ },
        onSubmit = { password -> /* handle */ },
        isLoading = viewModel.isLoading,
        password = viewModel.password,
        onPasswordChange = viewModel::onPasswordChange,
        passwordVisible = viewModel.passwordVisible,
        passwordError = viewModel.passwordError,
        onPasswordVisibleChange = viewModel::onPasswordVisibleChange
    )
}

// Cancel dialog (for future use)
if (viewModel.showCancelDialog) {
    AlertDialog(
        onDismissRequest = { /* handle */ },
        title = { Text(stringResource(R.string.cancel_transaction)) },
        text = { Text(stringResource(R.string.dismissing_Cancel_transaction_message)) },
        confirmButton = {
            Button(onClick = { /* handle */ }) {
                Text(stringResource(R.string.yes_cancel_confirmation))
            }
        },
        dismissButton = {
            TextButton(onClick = { /* handle */ }) {
                Text(stringResource(R.string.continue_label))
            }
        }
    )
}
*/

/*

LaunchedEffect(viewModel.isVerifySuccessful) {


        val homeSessionHandler = TransactionSessionManager.currentHandler
        val cashTransfer = homeSessionHandler as HomeSessionHandler.CashTransfer

        if (viewModel.isVerifySuccessful) {

            cashTransfer.onTransactionSuccessPrimaryAction = {
                val transactionId = cashTransfer.transactionId

                if (transactionId != null) {
                    navController.navigate("$INDIVIDUAL_TRANSACTION_LOG_ROUTE/$transactionId") {
                        popUpTo(HOME_ROUTE) {
                            inclusive = false
                        }
                    }
                } else {
                    navController.navigate(HOME_ROUTE) {
                        popUpTo(MAIN_ROUTE) {
                            inclusive = true
                        }
                    }
                }
            }

            cashTransfer.onTransactionFailurePrimaryAction = {
                navController.navigate(CASH_TRANSFER_ROUTE) {
                    popUpTo(HOME_ROUTE) {
                        inclusive = false
                    }
                }
            }

            homeSessionHandler.onOtpSuccess = {
                if(!cashTransfer.isFriend) {
                    navController.navigate("$PASSWORD_CONFIRMATION_ROUTE/$CASH_TRANSFER_ROUTE")
                } else {
                    homeSessionHandler.onPasswordSuccess.invoke()
                }
            }

            homeSessionHandler.onPasswordSuccess = {
                navController.navigate(TRANSACTION_RESULT_ROUTE)
            }

            navController.navigate("$HOME_OTP/$CASH_TRANSFER_ROUTE")
        }
    }
 */