package com.example.bankapp.ui.screens

import com.example.bankapp.entities.AuthorizationIntent
import AuthorizationViewModel
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.bankapp.R
import com.example.bankapp.di.viewmodelfactory.TransactionResultViewModelFactory
import com.example.bankapp.ui.components.BackButtonHandler
import com.example.bankapp.ui.components.LargeSpacer
import com.example.bankapp.ui.components.XLSpacer
import com.example.bankapp.ui.components.buttons.SubmitButton
import com.example.bankapp.ui.components.navigators.HOME_ROUTE
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.entities.AuthorizationctionState
import com.example.bankapp.entities.uientities.uidata.ResultContent
import com.example.bankapp.ui.components.navigators.ADD_BENEFICIARY_ROUTE
import com.example.bankapp.ui.components.navigators.CASH_TRANSFER_ROUTE
import com.example.bankapp.ui.components.navigators.DEPOSIT_ROUTE
import com.example.bankapp.ui.components.navigators.INDIVIDUAL_TRANSACTION_LOG_ROUTE
import com.example.bankapp.ui.components.navigators.MAIN_ROUTE
import com.example.bankapp.ui.components.navigators.PAY_ROUTE

import com.example.bankapp.viewmodels.ResultViewModel
import kotlinx.coroutines.delay


@Composable
fun TransactionResultScreen(
    transactionResultViewModelFactory: TransactionResultViewModelFactory,
    navController: NavController,
    authorizationViewModel: AuthorizationViewModel
) {
    val viewModel: ResultViewModel = viewModel(factory = transactionResultViewModelFactory)

    val actionState = authorizationViewModel.authorizationActionState

    LaunchedEffect(Unit) {
        if(!viewModel.actionExecuted) {
            viewModel.actionExecuted = true
            when(authorizationViewModel.authorizationIntent){
                is AuthorizationIntent.CashTransfer -> viewModel.cashTransfer(authorizationViewModel.authorizationIntent as AuthorizationIntent.CashTransfer)
                is AuthorizationIntent.AddBeneficiary -> viewModel.addBeneficiary(authorizationViewModel.authorizationIntent as AuthorizationIntent.AddBeneficiary)
                is AuthorizationIntent.Deposit -> viewModel.Deposit(authorizationViewModel.authorizationIntent as AuthorizationIntent.Deposit)
                is AuthorizationIntent.InternationalTransfer -> viewModel.InternationalTransfer(authorizationViewModel.authorizationIntent as AuthorizationIntent.InternationalTransfer)
                else -> {}
            }
        }
    }

    val resultContent: ResultContent = when(authorizationViewModel.authorizationIntent) {
        is AuthorizationIntent.CashTransfer -> {
            val authorizationIntent = authorizationViewModel.authorizationIntent as AuthorizationIntent.CashTransfer
            authorizationViewModel.getResultContent(
                onDone = {
                    val transactionId = authorizationIntent.transactionId
                    if (transactionId != null) {
                        navController.navigate("$INDIVIDUAL_TRANSACTION_LOG_ROUTE/$transactionId?origin=$HOME_ROUTE") {
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
                },
                onRetry = {
                    navController.navigate(CASH_TRANSFER_ROUTE) {
                        popUpTo(HOME_ROUTE) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        is AuthorizationIntent.AddBeneficiary -> {
            authorizationViewModel.getResultContent(
                onDone = {
                    navController.navigate(HOME_ROUTE) {
                        popUpTo(HOME_ROUTE){
                            inclusive = false
                        }
                    }
                },
                onRetry = {
                    navController.navigate(ADD_BENEFICIARY_ROUTE) {
                        popUpTo(PAY_ROUTE){
                            inclusive = false
                        }
                    }
                }
            )
        }
        is AuthorizationIntent.Deposit -> {
            val authorizationIntent = authorizationViewModel.authorizationIntent as AuthorizationIntent.Deposit
            authorizationViewModel.getResultContent(
                onDone = {
                    val transactionId = authorizationIntent.transactionId
                    if (transactionId != null) {
                        navController.navigate("$INDIVIDUAL_TRANSACTION_LOG_ROUTE/$transactionId?origin=$HOME_ROUTE") {
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
                },
                onRetry = {
                    navController.navigate(DEPOSIT_ROUTE) {
                        popUpTo(HOME_ROUTE) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        else ->
            authorizationViewModel.getResultContent(
                onDone = {
                    navController.navigate(HOME_ROUTE)
                },
                onRetry = {
                    navController.navigate(PAY_ROUTE)
                }
            )
    }

    val showContent = remember { mutableStateOf(false) }

    BackButtonHandler(navController, HOME_ROUTE)

    val successComposition = rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.success_tick)
    )
    val failureComposition = rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.fail)
    )

    LaunchedEffect(actionState) {
        if (actionState == AuthorizationctionState.SUCCESS || actionState == AuthorizationctionState.FAILURE) {
            delay(800)
            showContent.value = true
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            XLSpacer()
            XLSpacer()

            when (actionState) {
                AuthorizationctionState.LOADING -> {
                    CircularProgressIndicator()
                }
                AuthorizationctionState.SUCCESS,
                AuthorizationctionState.FAILURE -> {
                    val isSuccess = actionState == AuthorizationctionState.SUCCESS
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            LottieAnimation(
                                composition = if (isSuccess)
                                    successComposition.value
                                else
                                    failureComposition.value,
                                modifier = Modifier.fillMaxWidth(0.8f)
                            )
                        }

                        XLSpacer()
                        LargeSpacer()

                        AnimatedVisibility(
                            visible = showContent.value,
                            enter = fadeIn()
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(AppSpacing.lg),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                listOfNotNull(
                                    resultContent.text1,
                                    resultContent.text2,
                                    resultContent.text3,
                                    resultContent.text4,
                                    resultContent.text5
                                ).forEachIndexed { index, text ->
                                    val (style, color) = when (index) {
                                        0 -> Pair(
                                            MaterialTheme.typography.headlineSmall,
                                            if (isSuccess) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                        )
                                        1 -> Pair(
                                            MaterialTheme.typography.titleMedium,
                                            MaterialTheme.colorScheme.onSurface
                                        )
                                        else -> Pair(
                                            MaterialTheme.typography.bodyMedium,
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Text(
                                        text = text.asString(),
                                        style = style,
                                        color = color,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth(0.9f)
                                    )
                                }

                                XLSpacer()

                                if (resultContent.secondaryButton == null && resultContent.primaryButton != null) {
                                    resultContent.primaryButton.let { button ->
                                        SubmitButton(
                                            onClick = button.onClick,
                                            text = button.text.asString(),
                                            modifier = Modifier.fillMaxWidth(0.7f)
                                        )
                                    }
                                } else if (resultContent.primaryButton != null || resultContent.secondaryButton != null) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth(0.9f)
                                            .padding(horizontal = AppSpacing.md),
                                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
                                    ) {
                                        resultContent.secondaryButton?.let { button ->
                                            SubmitButton(
                                                onClick = button.onClick,
                                                text = button.text.asString(),
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                        resultContent.primaryButton?.let { button ->
                                            SubmitButton(
                                                onClick = button.onClick,
                                                text = button.text.asString(),
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }
                                }

                                XLSpacer()
                                XLSpacer()
                                XLSpacer()
                                XLSpacer()
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.weight(1f))
        }
    }
}