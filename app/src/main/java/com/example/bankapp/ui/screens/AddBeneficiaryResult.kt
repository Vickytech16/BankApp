package com.example.bankapp.ui.screens


import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.bankapp.R
import com.example.bankapp.di.viewmodelfactory.AddBeneficiaryResultViewModelFactory
import com.example.bankapp.ui.components.buttons.SubmitButton
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.usecases.CurrentTransactionStatus
import com.example.bankapp.usecases.TransactionSessionHolder
import com.example.bankapp.viewmodels.AddBeneficiaryResultViewModel
import kotlinx.coroutines.delay

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBeneficiaryResultScreen(
    navController: NavController,
    transactionSessionHolder: TransactionSessionHolder,
    addBeneficiaryResultViewModelFactory: AddBeneficiaryResultViewModelFactory
) {
    val viewModel: AddBeneficiaryResultViewModel = viewModel(factory = addBeneficiaryResultViewModelFactory)
    val showContent = remember { mutableStateOf(false) }

    val successComposition = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.success_tick))
    val failureComposition = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.fail))

    LaunchedEffect(transactionSessionHolder.currentTransactionStatus) {
        if (transactionSessionHolder.currentTransactionStatus == CurrentTransactionStatus.SUCCESS ||
            transactionSessionHolder.currentTransactionStatus == CurrentTransactionStatus.FAILURE) {
            delay(1000)
            showContent.value = true
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.screen_padding)),
            contentAlignment = Alignment.Center
        ) {
            when (transactionSessionHolder.currentTransactionStatus) {
                CurrentTransactionStatus.LOADING -> {
                    CircularProgressIndicator()
                }

                CurrentTransactionStatus.SUCCESS -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        LottieAnimation(
                            composition = successComposition.value,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                        )

                        AnimatedVisibility(
                            visible = showContent.value,
                            enter = fadeIn()
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)
                            ) {
                                Text(
                                    text = transactionSessionHolder.successMessage,
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                SubmitButton(
                                    onClick = {
                                        transactionSessionHolder.reset()
                                        navController.navigate("com.example.bankapp.HOME_ROUTE") {
                                            popUpTo("com.example.bankapp.HOME_ROUTE") {
                                                inclusive = false
                                            }
                                        }
                                    },
                                    text = "Done"
                                )
                            }
                        }
                    }
                }

                CurrentTransactionStatus.FAILURE -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        LottieAnimation(
                            composition = failureComposition.value,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                        )

                        AnimatedVisibility(
                            visible = showContent.value,
                            enter = fadeIn()
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)
                            ) {
                                Text(
                                    text = transactionSessionHolder.failureMessage,
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.error
                                )

                                SubmitButton(
                                    onClick = {
                                        transactionSessionHolder.currentTransactionStatus = CurrentTransactionStatus.IDLE
                                        navController.popBackStack()
                                    },
                                    text = "Try Again"
                                )
                            }
                        }
                    }
                }

                CurrentTransactionStatus.IDLE -> {
                }
            }
        }
    }
}