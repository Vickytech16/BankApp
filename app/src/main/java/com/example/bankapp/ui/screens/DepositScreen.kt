package com.example.bankapp.ui.screens

import AmountOutlinedTextField
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bankapp.R
import com.example.bankapp.di.viewmodelfactory.DepositViewModelFactory
import com.example.bankapp.ui.components.appbar.Appbar
import com.example.bankapp.ui.components.ErrorTextBuilder
import com.example.bankapp.ui.components.MediumSpacer
import com.example.bankapp.ui.components.XLSpacer
import com.example.bankapp.ui.components.buttons.SubmitButton
import com.example.bankapp.ui.components.textfields.PasswordVerificationOutlinedTextField
import com.example.bankapp.ui.theme.AppPadding
import com.example.bankapp.ui.theme.screenPadding
import com.example.bankapp.viewmodels.DepositViewModel
import com.example.bankapp.viewmodels.OtpVerificationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DepositScreen(
    depositViewModelFactory: DepositViewModelFactory,
    windowSizeClass: WindowSizeClass,
    navController: NavController,
){
  val viewModel: DepositViewModel = viewModel(factory = depositViewModelFactory)

    val scrollState = rememberScrollState()

    val textFieldColumnWidth =
        when (windowSizeClass.widthSizeClass) {
            WindowWidthSizeClass.Compact -> 0.9f
            WindowWidthSizeClass.Medium -> 0.6f
            WindowWidthSizeClass.Expanded -> 0.5f
            else -> 0.8f
        }

    Scaffold(
        topBar = { Appbar(stringResource(R.string.deposit_label), { navController.popBackStack() }, null) },
        contentWindowInsets = WindowInsets(0,0,0,0)
    )
    {
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
            AmountOutlinedTextField(
                amount = viewModel.amount,
                onAmountChange = viewModel::onAmountChange,
                fieldName = stringResource(R.string.amount_field_name),
                amountError = viewModel.amountError
            )

            MediumSpacer()

            PasswordVerificationOutlinedTextField(
                password = viewModel.password,
                onPasswordChange = viewModel::onPasswordChange,
                passwordVisible = viewModel.passwordVisible,
                passwordError = viewModel.passwordError,
                onPasswordVisibleChange = viewModel::onPasswordVisibleChange
            )

            XLSpacer()

            SubmitButton(
                onClick = { viewModel.onSubmit() },
                isLoading = viewModel.isLoading
                )

            ErrorTextBuilder(viewModel.submitError)

            val transactionResult = viewModel.transactionResult

            if(transactionResult!=null)
                Text(stringResource(transactionResult.message))

            }
        }
    }






}