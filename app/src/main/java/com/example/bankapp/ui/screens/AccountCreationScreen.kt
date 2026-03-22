package com.example.bankapp.ui.screens

import AmountOutlinedTextField
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bankapp.R
import com.example.bankapp.di.viewmodelfactory.AccountCreationViewModelFactory
import com.example.bankapp.ui.components.textfields.AccountTypeRadioButton
import com.example.bankapp.ui.components.ErrorTextBuilder
import com.example.bankapp.ui.components.XLSpacer
import com.example.bankapp.ui.components.MediumSpacer
import com.example.bankapp.ui.components.buttons.SubmitButton
import com.example.bankapp.ui.components.textfields.PasswordVerificationOutlinedTextField
import com.example.bankapp.ui.screens.authscreens.getModifier
import com.example.bankapp.ui.theme.AppPadding
import com.example.bankapp.ui.theme.screenPadding
import com.example.bankapp.viewmodels.AccountCreationViewModel


@Composable
fun AccountCreationScreen(navController: NavController, accountCreationViewModelFactory: AccountCreationViewModelFactory, windowSizeClass: WindowSizeClass, restoreSession: ()-> Unit)
{
    val accountCreationViewModel: AccountCreationViewModel = viewModel(factory = accountCreationViewModelFactory)


    val scrollState = rememberScrollState()

    val textFieldColumnWidth =
        when (windowSizeClass.widthSizeClass) {
            WindowWidthSizeClass.Compact -> 0.9f
            WindowWidthSizeClass.Medium -> 0.6f
            WindowWidthSizeClass.Expanded -> 0.5f
            else -> 0.8f
        }

    Scaffold() {
        contentPadding ->
        Column(
            modifier = getModifier(windowSizeClass, contentPadding, scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(R.string.create_account),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.ExtraBold
            )

            Text(stringResource(R.string.create_account_description),
                textAlign = TextAlign.Center)

            XLSpacer()

            Column(
                modifier = Modifier.fillMaxWidth(textFieldColumnWidth),
                horizontalAlignment = Alignment.CenterHorizontally
            ){

                AccountTypeRadioButton(
                    stringResource(R.string.select_your_account_type_label),
                    accountCreationViewModel.accountType,
                    accountCreationViewModel::onAccountTypeChange
                    )

                MediumSpacer()

                AmountOutlinedTextField(
                    amount = accountCreationViewModel.amount.toString(),
                    onAmountChange = accountCreationViewModel::onAmountChange,
                    fieldName = stringResource(R.string.initial_balance),
                    amountError = accountCreationViewModel.amountError
                )

                MediumSpacer()

                PasswordVerificationOutlinedTextField(
                    password = accountCreationViewModel.password,
                    onPasswordChange = accountCreationViewModel::onPasswordChange,
                    passwordVisible = accountCreationViewModel.passwordVisible,
                    passwordError = accountCreationViewModel.passwordError,
                    onPasswordVisibleChange = accountCreationViewModel::onPasswordVisibleChange
                )

                XLSpacer()

                SubmitButton(
                    onClick = {accountCreationViewModel.onSubmit()},
                    text = stringResource(R.string.create_account),
                    isLoading = accountCreationViewModel.isLoading)

                ErrorTextBuilder(accountCreationViewModel.submitError)

//                if(accountCreationViewModel.isSubmitSuccessful)
//                    Text("Submitted")

                LaunchedEffect(accountCreationViewModel.isSubmitSuccessful) {
                    if(accountCreationViewModel.isSubmitSuccessful)
                        restoreSession()
                }
            }
        }
    }

}