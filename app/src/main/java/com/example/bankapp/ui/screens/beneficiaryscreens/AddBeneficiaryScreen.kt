package com.example.bankapp.ui.screens.beneficiaryscreens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Phone
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bankapp.R
import com.example.bankapp.di.viewmodelfactory.AddBeneficiaryViewModelFactory
import com.example.bankapp.ui.components.appbar.Appbar
import com.example.bankapp.ui.components.BackButtonHandler
import com.example.bankapp.ui.components.ErrorTextBuilder
import com.example.bankapp.ui.components.LargeSpacer
import com.example.bankapp.ui.components.XLSpacer
import com.example.bankapp.ui.components.MediumSpacer
import com.example.bankapp.ui.components.buttons.SubmitButton

import com.example.bankapp.ui.components.navigators.ADD_BENEFICIARY_ROUTE
import com.example.bankapp.ui.components.navigators.HOME_OTP
import com.example.bankapp.ui.components.navigators.HOME_ROUTE
import com.example.bankapp.ui.components.navigators.PAY_ROUTE
import com.example.bankapp.ui.components.textfields.UnifiedOutlinedTextField
import com.example.bankapp.ui.theme.AppPadding
import com.example.bankapp.ui.theme.DeviceSpecProvider
import com.example.bankapp.usecases.CurrentSessionIntent
import com.example.bankapp.utilities.EmailFieldStrategy
import com.example.bankapp.utilities.UserNameFieldStrategy
import com.example.bankapp.viewmodels.AddBeneficiaryViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBeneficiaryScreen(
    windowSizeClass: WindowSizeClass,
    navController: NavController,
    beneficiaryViewModelFactory: AddBeneficiaryViewModelFactory,
) {

    val viewModel: AddBeneficiaryViewModel = viewModel(factory = beneficiaryViewModelFactory)

    val scrollState = rememberScrollState()

    LaunchedEffect(windowSizeClass.heightSizeClass) {
        if (windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact) {
            scrollState.animateScrollTo(400)
        }
    }

    LaunchedEffect(viewModel.isVerificationSuccessful) {
        if(viewModel.isVerificationSuccessful) {
            navController.navigate("$HOME_OTP/$ADD_BENEFICIARY_ROUTE")
        }
    }

    val deviceSpec = DeviceSpecProvider.getCurrentDeviceSpec(windowSizeClass)

    val textFieldColumnWidth =
        deviceSpec.textFieldWidth

    BackButtonHandler(navController, PAY_ROUTE)

    Scaffold(
        topBar = { Appbar(stringResource(R.string.add_beneficiary), {
            navController.navigate(HOME_ROUTE)
        }, null)
     },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        contentPadding ->
        Column(
            modifier = AppPadding.padding(contentPadding).fillMaxHeight().verticalScroll(scrollState),
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

                UnifiedOutlinedTextField(
                    value = viewModel.userIdentifier,
                    onValueChange = viewModel::onIdentifierChange,
                    labelText = stringResource(R.string.email_or_phone_number_label),
                    isError = viewModel.userIdentifierError != null,
                    supportingText = {
                        ErrorTextBuilder(viewModel.userIdentifierError)
                    },
                    strategy = EmailFieldStrategy,
                    leadingIcon = Icons.AutoMirrored.Outlined.Login,
                )

                LargeSpacer()

                UnifiedOutlinedTextField(
                    value = viewModel.nickname,
                    onValueChange = viewModel::onNickNameChange,
                    labelText = stringResource(R.string.nickname_optional_field_name),
                    isError = viewModel.nicknameError != null,
                    supportingText = {
                        ErrorTextBuilder(viewModel.nicknameError)
                    },
                    strategy = UserNameFieldStrategy
                )

                XLSpacer()

                SubmitButton(
                    onClick = { viewModel.onSubmit() },
                    text = stringResource(R.string.submit_button),
                    isLoading = viewModel.isLoading,
                )

                ErrorTextBuilder(viewModel.submitError)

                MediumSpacer()
            }
        }
    }
}