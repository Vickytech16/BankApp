package com.example.bankapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
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
import com.example.bankapp.entities.types.account.AccountType
import com.example.bankapp.entities.types.transaction.TransactionType
import com.example.bankapp.ui.components.ErrorTextBuilder
import com.example.bankapp.ui.components.XLSpacer
import com.example.bankapp.ui.components.MediumSpacer
import com.example.bankapp.ui.components.RadioButtonSelector
import com.example.bankapp.ui.components.buttons.SubmitButton
import com.example.bankapp.ui.components.textfields.UnifiedOutlinedTextField
import com.example.bankapp.ui.screens.authscreens.getAppModifier
import com.example.bankapp.utilities.AmountFieldStrategy
import com.example.bankapp.utilities.PasswordFieldStrategy
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
            modifier = getAppModifier(windowSizeClass, contentPadding, scrollState),
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
                RadioButtonSelector(
                    title = stringResource(R.string.select_your_account_type_label),
                    options = AccountType.entries.toList(),
                    selected = accountCreationViewModel.accountType,
                    onSelectionChange = accountCreationViewModel::onAccountTypeChange,
                    labelFor =  { accountType ->
                        accountType.name.lowercase().replaceFirstChar { it.uppercase() }
                    }
                )

                MediumSpacer()

               UnifiedOutlinedTextField(
                    value = accountCreationViewModel.amount,
                    onValueChange = accountCreationViewModel::onAmountChange,
                    labelText = stringResource(R.string.initial_balance),
                    isError = accountCreationViewModel.amountError != null,
                    supportingText = {
                        ErrorTextBuilder(accountCreationViewModel.amountError)
                    },
                    strategy = AmountFieldStrategy(TransactionType.DEPOSIT),
                )

                MediumSpacer()

               UnifiedOutlinedTextField(
                    value = accountCreationViewModel.password,
                    onValueChange  = accountCreationViewModel::onPasswordChange,
                    labelText = stringResource(R.string.password_field_name),
                    isError = accountCreationViewModel.passwordError != null,
                    supportingText = {
                       ErrorTextBuilder(accountCreationViewModel.passwordError)
                   },
                   strategy = PasswordFieldStrategy(accountCreationViewModel.passwordVisible, accountCreationViewModel::onPasswordVisibleChange)
                )

                XLSpacer()

                SubmitButton(
                    onClick = {accountCreationViewModel.onSubmit()},
                    text = stringResource(R.string.create_account),
                    isLoading = accountCreationViewModel.isLoading)

                ErrorTextBuilder(accountCreationViewModel.submitError)

                XLSpacer()

                LaunchedEffect(accountCreationViewModel.isSubmitSuccessful) {
                    if(accountCreationViewModel.isSubmitSuccessful)
                        restoreSession()
                }
            }
        }
    }

}