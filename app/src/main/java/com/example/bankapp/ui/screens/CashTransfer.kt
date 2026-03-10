package com.example.bankapp.ui.screens

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
import androidx.compose.material3.Button
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
import com.example.bankapp.di.viewmodelfactory.CashTransferViewModelFactory
import com.example.bankapp.entities.errors.FormError
import com.example.bankapp.ui.components.Appbar
import com.example.bankapp.ui.components.ErrorTextBuilder
import com.example.bankapp.ui.components.MediumSpacer
import com.example.bankapp.ui.components.XLSpacer
import com.example.bankapp.ui.components.buttons.SubmitButton
import com.example.bankapp.ui.components.textfields.AccountNumberOutlinedTextField
import com.example.bankapp.ui.components.textfields.AmountOutlinedTextField
import com.example.bankapp.ui.components.textfields.PasswordVerificationOutlinedTextField
import com.example.bankapp.ui.theme.AppPadding
import com.example.bankapp.ui.theme.screenPadding
import com.example.bankapp.ui.theme.titleFontSize
import com.example.bankapp.utilities.AMOUNTFIELDNAME
import com.example.bankapp.utilities.ENTERRECIPIENTACCOUNTDETAILSTITLENAME
import com.example.bankapp.viewmodels.CashTransferViewModel

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashTransferScreen(windowSizeClass: WindowSizeClass,
                      cashTransferViewModelFactory: CashTransferViewModelFactory,
                      navController: NavController)
{

    val viewModel: CashTransferViewModel = viewModel(factory = cashTransferViewModelFactory)
    val scrollState = rememberScrollState()

    val textFieldColumnWidth =
        when (windowSizeClass.widthSizeClass) {
            WindowWidthSizeClass.Compact -> 0.9f
            WindowWidthSizeClass.Medium -> 0.6f
            WindowWidthSizeClass.Expanded -> 0.5f
            else -> 0.8f
        }

  //  BackButtonHandler(navController, HOMEROUTE)

    Scaffold(
        topBar = { Appbar("Transfer to Account", { navController.popBackStack() }, null) },
        contentWindowInsets = WindowInsets(0,0,0,0)
    )
    {
        Column(
            modifier = AppPadding
                .padding(screenPadding)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ){

            Text(
                text = ENTERRECIPIENTACCOUNTDETAILSTITLENAME,
                fontSize = titleFontSize,
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

                MediumSpacer()

                AmountOutlinedTextField(
                    amount = viewModel.amount.toString(),
                    onAmountChange = viewModel::onAmountChange,
                    fieldName = AMOUNTFIELDNAME,
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
                   onClick = {viewModel.onSubmit()},
                   isLoading = viewModel.isLoading
               )

                ErrorTextBuilder(viewModel.submitError)


                if(viewModel.transactionResult!=null)
                    Text(stringResource(viewModel.transactionResult!!.message))

                MediumSpacer()

            }
        }
    }
}

