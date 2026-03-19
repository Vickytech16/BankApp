package com.example.bankapp.ui.screens


import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bankapp.R
import com.example.bankapp.di.viewmodelfactory.AddBeneficiaryViewModelFactory
import com.example.bankapp.entities.SessionState
import com.example.bankapp.repositories.UserRepository
import com.example.bankapp.repositories.BeneficiaryRepository
import com.example.bankapp.ui.components.appbar.Appbar
import com.example.bankapp.ui.components.BackButtonHandler
import com.example.bankapp.ui.components.ErrorTextBuilder
import com.example.bankapp.ui.components.XLSpacer
import com.example.bankapp.ui.components.MediumSpacer
import com.example.bankapp.ui.components.buttons.SubmitButton
import com.example.bankapp.ui.components.navigators.ADD_BENEFICIARY_RESULT_ROUTE
import com.example.bankapp.ui.components.navigators.ADD_BENEFICIARY_ROUTE
import com.example.bankapp.ui.components.navigators.HOME_OTP
import com.example.bankapp.ui.components.navigators.HOME_ROUTE
import com.example.bankapp.ui.components.navigators.PAY_ROUTE
import com.example.bankapp.ui.components.textfields.GenericOutlinedTextField
import com.example.bankapp.ui.theme.AppPadding
import com.example.bankapp.ui.theme.screenPadding
import com.example.bankapp.usecases.CurrentSessionIntent
import com.example.bankapp.usecases.TransactionSessionHolder
import com.example.bankapp.viewmodels.AddBeneficiaryViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBeneficiaryScreen(
    windowSizeClass: WindowSizeClass,
    navController: NavController,
    beneficiaryViewModelFactory: AddBeneficiaryViewModelFactory,
    transactionSessionHolder: TransactionSessionHolder
) {

    val viewModel: AddBeneficiaryViewModel = viewModel(factory = beneficiaryViewModelFactory)

    val scrollState = rememberScrollState()

    val textFieldColumnWidth = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 0.9f
        WindowWidthSizeClass.Medium -> 0.6f
        WindowWidthSizeClass.Expanded -> 0.5f
        else -> 0.8f
    }

    BackButtonHandler(navController, PAY_ROUTE)

    Scaffold(
        topBar = { Appbar(stringResource(R.string.add_beneficiary), { navController.navigate(HOME_ROUTE) }, null) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        Column(
            modifier = AppPadding
                .padding(screenPadding)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            XLSpacer()

            Text(
                text = stringResource(R.string.add_beneficiary_headline),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.ExtraBold
            )

            XLSpacer()

            Text(
                text = stringResource(R.string.add_beneficiary_description),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            XLSpacer()

            Column(
                modifier = Modifier.fillMaxWidth(textFieldColumnWidth),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GenericOutlinedTextField(
                    value = viewModel.email,
                    onValueChange = viewModel::onEmailChange,
                    labelText = stringResource(R.string.email_field_name),
                    isError = viewModel.emailError != null,
                    supportingText = {
                        ErrorTextBuilder(viewModel.emailError)
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                    ),
                    leadingIcon = Icons.Outlined.Email
                )

                MediumSpacer()

                GenericOutlinedTextField(
                    value = viewModel.phoneNumber,
                    onValueChange = viewModel::onPhoneNumberChange,
                    labelText = stringResource(R.string.phone_number_field_name),
                    isError = viewModel.phoneNumberError != null,
                    supportingText = {
                        ErrorTextBuilder(viewModel.phoneNumberError)
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    leadingIcon = Icons.Outlined.Phone
                )

                XLSpacer()

                SubmitButton(
                    onClick = { viewModel.onSubmit() },
                    text = stringResource(R.string.submit_button),
                    isLoading = viewModel.isLoading,
                )

                ErrorTextBuilder(viewModel.submitError)

                MediumSpacer()

                LaunchedEffect(viewModel.isVerificationSuccessful) {
                    if (viewModel.isVerificationSuccessful) {

                                transactionSessionHolder.onTransactionTypeChange(
                                    CurrentSessionIntent.BENEFICIARY_ADDITION)
                                transactionSessionHolder.onPasswordVerificationNavigation = {
                                    navController.navigate(ADD_BENEFICIARY_RESULT_ROUTE)
                                }
                                navController.navigate("$HOME_OTP/$ADD_BENEFICIARY_ROUTE")

                    }
                }
            }
        }
    }
}