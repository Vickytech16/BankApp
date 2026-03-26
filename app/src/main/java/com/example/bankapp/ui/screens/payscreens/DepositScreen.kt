package com.example.bankapp.ui.screens.payscreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bankapp.R
import com.example.bankapp.di.viewmodelfactory.DepositViewModelFactory
import com.example.bankapp.ui.components.appbar.Appbar
import com.example.bankapp.ui.components.ErrorTextBuilder
import com.example.bankapp.ui.components.XLSpacer
import com.example.bankapp.ui.components.buttons.SubmitButton
import com.example.bankapp.ui.components.navigators.DEPOSIT_ROUTE
import com.example.bankapp.ui.components.navigators.HOME_OTP
import com.example.bankapp.di.providers.HomeSessionHandlerProvider
import com.example.bankapp.entities.types.transaction.TransactionType
import com.example.bankapp.ui.components.LargeSpacer
import com.example.bankapp.ui.components.MediumSpacer
import com.example.bankapp.ui.components.screenModifier
import com.example.bankapp.ui.components.textfields.UnifiedOutlinedTextField
import com.example.bankapp.ui.theme.AppPadding
import com.example.bankapp.ui.theme.DeviceSpecProvider
import com.example.bankapp.usecases.CurrentSessionIntent
import com.example.bankapp.utilities.AmountFieldStrategy
import com.example.bankapp.viewmodels.DepositViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepositScreen(
    depositViewModelFactory: DepositViewModelFactory,
    windowSizeClass: WindowSizeClass,
    navController: NavController,
) {
    val viewModel: DepositViewModel = viewModel(factory = depositViewModelFactory)

    val scrollState = rememberScrollState()

    val illustrationHeight = dimensionResource(R.dimen.illustration_height).value.toInt()

    LaunchedEffect(windowSizeClass.heightSizeClass) {
        if (windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact) {
            scrollState.animateScrollTo(illustrationHeight + 300)
        }
    }

    val deviceSpec = DeviceSpecProvider.getCurrentDeviceSpec(windowSizeClass)

    val textFieldColumnWidth =
        deviceSpec.textFieldWidth

    LaunchedEffect(viewModel.isVerifySuccessful) {
        if (viewModel.isVerifySuccessful) {
            navController.navigate("$HOME_OTP/$DEPOSIT_ROUTE")
        }
    }

    Scaffold(
        topBar = {
            Appbar(
                stringResource(R.string.deposit_label),
                { navController.popBackStack() },
                null
            )
        },
    )
    {
        contentPadding ->
        Column(
            modifier = Modifier.fillMaxHeight().screenModifier(windowSizeClass, contentPadding, scrollState),
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
                    isLoading = viewModel.isLoading
                )

                ErrorTextBuilder(viewModel.submitError)

                XLSpacer()
            }
        }

    }
}