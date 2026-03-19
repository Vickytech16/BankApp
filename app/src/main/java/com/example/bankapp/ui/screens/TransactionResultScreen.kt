package com.example.bankapp.ui.screens


import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.bankapp.di.viewmodelfactory.TransactionResultViewModelFactory
import com.example.bankapp.entities.types.TransactionType
import com.example.bankapp.ui.components.BackButtonHandler
import com.example.bankapp.ui.components.LargeSpacer
import com.example.bankapp.ui.components.XLSpacer
import com.example.bankapp.ui.components.buttons.SubmitButton
import com.example.bankapp.ui.components.navigators.HOME_ROUTE
import com.example.bankapp.ui.components.navigators.TransactionSessionManager
import com.example.bankapp.ui.theme.AppPadding
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.usecases.ActionState
import com.example.bankapp.usecases.CurrentSessionIntent
import com.example.bankapp.usecases.CurrentTransactionStatus
import com.example.bankapp.usecases.HomeSessionHandler
import com.example.bankapp.usecases.ResultButtonType
import com.example.bankapp.usecases.TextType
import com.example.bankapp.usecases.TransactionSessionHolder
import com.example.bankapp.viewmodels.TransactionResultViewModel
import kotlinx.coroutines.delay


@Composable
fun TransactionResultScreen(
    transactionResultViewModelFactory: TransactionResultViewModelFactory,
    navController: NavController
) {
    val viewModel: TransactionResultViewModel = viewModel(factory = transactionResultViewModelFactory)
    val homeSessionHandler = TransactionSessionManager.currentHandler
    val cashTransfer = homeSessionHandler as HomeSessionHandler.CashTransfer

    val actionState = homeSessionHandler.actionState
    val resultContent = homeSessionHandler.resultContent

    val showContent = remember { mutableStateOf(false) }
      // Add this

    BackButtonHandler(navController, HOME_ROUTE)

    val successComposition = rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.success_tick)
    )
    val failureComposition = rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.fail)
    )

    // Only execute ONCE, not on recomposition
    LaunchedEffect(Unit) {
        if(!viewModel.actionExecuted && homeSessionHandler.intent == CurrentSessionIntent.CASH_TRANSFER) {
            viewModel.actionExecuted = true
            viewModel.cashTransfer()
        }
    }

    LaunchedEffect(actionState) {
        if (actionState == ActionState.SUCCESS || actionState == ActionState.FAILURE) {
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
                ActionState.LOADING -> {
                    CircularProgressIndicator()
                }

                ActionState.SUCCESS,
                ActionState.FAILURE -> {
                    val isSuccess = actionState == ActionState.SUCCESS

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Animation higher up
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
                                resultContent?.messages?.forEach { item ->
                                    val color = when (item.textType) {
                                        TextType.TITLE -> {
                                            if (isSuccess)
                                                MaterialTheme.colorScheme.primary
                                            else
                                                MaterialTheme.colorScheme.error
                                        }
                                        else -> MaterialTheme.colorScheme.onSurface
                                    }

                                    val style = when (item.textType) {
                                        TextType.TITLE -> MaterialTheme.typography.headlineSmall
                                        TextType.SUBTITLE -> MaterialTheme.typography.titleMedium
                                        TextType.MESSAGE -> MaterialTheme.typography.bodySmall
                                    }

                                    Text(
                                        text = item.text.asString(),
                                        style = style,
                                        color = color,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth(0.9f)
                                    )
                                }

                                XLSpacer()
                                LargeSpacer()

                                resultContent?.buttons
                                    ?.firstOrNull { it.role == ResultButtonType.PRIMARY }
                                    ?.let { button ->
                                        SubmitButton(
                                            onClick = button.onClick,
                                            text = button.text.asString(),
                                            modifier = Modifier.fillMaxWidth(0.7f)
                                        )
                                    }

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

//@Composable
//fun TransactionResultScreen(
//    transactionResultViewModelFactory: TransactionResultViewModelFactory
//) {
//    val viewModel: TransactionResultViewModel = viewModel(factory = transactionResultViewModelFactory)
//
//    val homeSessionHandler = TransactionSessionManager.currentHandler
//
//    val cashTransfer = homeSessionHandler as HomeSessionHandler.CashTransfer
//
//    val actionState = homeSessionHandler.actionState
//    val resultContent = homeSessionHandler.resultContent
//
//    println("DEBUG SCREEN: actionState = $actionState")
//    println("DEBUG SCREEN: resultContent = $resultContent")
//    println("DEBUG SCREEN: resultContent?.messages?.size = ${resultContent?.messages?.size}")
//
//    val showContent = remember { mutableStateOf(false) }
//
//    val successComposition = rememberLottieComposition(
//        LottieCompositionSpec.RawRes(R.raw.success_tick)
//    )
//    val failureComposition = rememberLottieComposition(
//        LottieCompositionSpec.RawRes(R.raw.fail)
//    )
//
//    LaunchedEffect(Unit) {
//        if(cashTransfer.toAccNo != 0L) {
//            viewModel.cashTransfer()
//        }
//    }
//
//    LaunchedEffect(actionState) {
//        println("DEBUG SCREEN: LaunchedEffect actionState=$actionState")
//        if (actionState == ActionState.SUCCESS ||
//            actionState == ActionState.FAILURE
//        ) {
//            delay(800)
//            showContent.value = true
//            println("DEBUG SCREEN: showContent set to true")
//        }
//    }
//
//    Box(
//        modifier = Modifier.fillMaxSize(),
//        contentAlignment = Alignment.Center
//    ) {
//        println("DEBUG SCREEN: In Box, actionState=$actionState, showContent=${showContent.value}")
//
//        when (actionState) {
//
//            ActionState.LOADING -> {
//                println("DEBUG SCREEN: Showing LOADING")
//                CircularProgressIndicator()
//            }
//
//            ActionState.SUCCESS,
//            ActionState.FAILURE -> {
//                println("DEBUG SCREEN: Showing SUCCESS/FAILURE")
//
//                val isSuccess = actionState == ActionState.SUCCESS
//
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    verticalArrangement = Arrangement.Center
//                ) {
//
//                    LottieAnimation(
//                        composition = if (isSuccess)
//                            successComposition.value
//                        else
//                            failureComposition.value
//                    )
//
//                    println("DEBUG SCREEN: About to show AnimatedVisibility, visible=${showContent.value}")
//
//                    AnimatedVisibility(
//                        visible = showContent.value,
//                        enter = fadeIn()
//                    ) {
//                        println("DEBUG SCREEN: Inside AnimatedVisibility")
//
//                        Column(
//                            horizontalAlignment = Alignment.CenterHorizontally,
//                            verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)
//                        ) {
//                            println("DEBUG SCREEN: resultContent?.messages = ${resultContent?.messages}")
//                            println("DEBUG SCREEN: messages count = ${resultContent?.messages?.size}")
//
//                            resultContent?.messages?.forEach { item ->
//                                println("DEBUG SCREEN: Rendering message: ${item.text}")
//
//                                val color = when (item.textType) {
//                                    TextType.TITLE -> {
//                                        if (isSuccess)
//                                            MaterialTheme.colorScheme.primary
//                                        else
//                                            MaterialTheme.colorScheme.error
//                                    }
//                                    else -> MaterialTheme.colorScheme.onSurface
//                                }
//
//                                val style = when (item.textType) {
//                                    TextType.TITLE -> MaterialTheme.typography.headlineSmall
//                                    TextType.SUBTITLE -> MaterialTheme.typography.titleMedium
//                                    TextType.MESSAGE -> MaterialTheme.typography.bodySmall
//                                }
//
//                                Text(
//                                    text = item.text.asString(),
//                                    style = style,
//                                    color = color,
//                                    textAlign = TextAlign.Center
//                                )
//                            }
//
//                            println("DEBUG SCREEN: resultContent?.buttons = ${resultContent?.buttons}")
//
//                            resultContent?.buttons
//                                ?.firstOrNull { it.role == ResultButtonType.PRIMARY }
//                                ?.let { button ->
//                                    println("DEBUG SCREEN: Rendering button")
//
//                                    SubmitButton(
//                                        onClick = button.onClick,
//                                        text = button.text.asString()
//                                    )
//                                }
//                        }
//                    }
//                }
//            }
//        }
//    }
