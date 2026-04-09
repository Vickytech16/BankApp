package com.example.bankapp.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bankapp.R
import com.example.bankapp.core.datecompatability.BankDateFactory
import com.example.bankapp.core.datecompatability.BankDateTime
import com.example.bankapp.di.viewmodelfactory.CashTransferViewModelFactory
import com.example.bankapp.di.viewmodelfactory.OtpViewModelFactory
import com.example.bankapp.di.viewmodelfactory.PasswordConfirmationViewModelFactory
import com.example.bankapp.entities.types.account.AccountStatus
import com.example.bankapp.ui.components.BackHandlerWithWarning
import com.example.bankapp.ui.components.appbar.FlowAppBar
import com.example.bankapp.ui.components.homeitems.AmountStepContent
import com.example.bankapp.ui.components.navigators.CASH_TRANSFER_ROUTE
import com.example.bankapp.ui.components.navigators.HOME_ROUTE
import com.example.bankapp.ui.components.navigators.MAIN_ROUTE
import com.example.bankapp.ui.components.navigators.TRANSACTION_RESULT_ROUTE
import com.example.bankapp.viewmodels.CashTransferViewModel
import com.example.bankapp.viewmodels.SessionViewModel
import com.example.bankapp.ui.theme.LocalDeviceSpec
import com.example.bankapp.ui.theme.DeviceSpec
import com.example.bankapp.utilities.CurrencyUtils
import com.example.bankapp.viewmodels.AuthorizationViewModel
import com.example.bankapp.viewmodels.FlowType
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashTransferFlowHost(
    navController: NavController,
    authorizationViewModel: AuthorizationViewModel,
    cashTransferViewModelFactory: CashTransferViewModelFactory,
    otpViewModelFactory: OtpViewModelFactory,
    passwordConfirmationViewModelFactory: PasswordConfirmationViewModelFactory,
    sessionViewModel: SessionViewModel,
    origin: String
) {
    val cashTransferViewModel: CashTransferViewModel = viewModel(factory = cashTransferViewModelFactory)

    val currentStep = authorizationViewModel.currentStep
    val totalSteps = authorizationViewModel.totalSteps
    val focusManager = LocalFocusManager.current
    val deviceSpec = LocalDeviceSpec.current

    val user by cashTransferViewModel.user.collectAsState()
    val velocityStatus by cashTransferViewModel.velocityStatus.collectAsState()

    var showExitWarning by remember { mutableStateOf(false) }
    var isExiting by remember { mutableStateOf(false) }

    val scrollBehavior = if (deviceSpec is DeviceSpec.MobileLandscape) {
        TopAppBarDefaults.enterAlwaysScrollBehavior()
    } else {
        TopAppBarDefaults.pinnedScrollBehavior()
    }

    val friendAccNo = remember(navController.currentBackStackEntry) {
        navController.currentBackStackEntry?.arguments?.getString("friendAccNo")
    }

    val onExitFlow = {
        focusManager.clearFocus()
        authorizationViewModel.clearAuthorization()
        navController.popBackStack()
    }

    val isLimitReached = remember(velocityStatus) {
        velocityStatus?.let {
            it.transactionsToday >= it.maxTransactions ||
                    it.moneySpentToday >= it.dailySpendLimit
        } ?: false
    }

    LaunchedEffect(Unit) {
        if(authorizationViewModel.activeFlow == FlowType.NONE) {
            authorizationViewModel.startFlow(FlowType.CASH_TRANSFER)
            if (friendAccNo != null) {
                authorizationViewModel.isQuickPay = true
                cashTransferViewModel.onFriendPay(friendAccNo)
            } else {
                authorizationViewModel.isQuickPay = false
            }
        }
    }

    BackHandlerWithWarning(
        showDialog = showExitWarning,
        onShowDialogConfirm = {
            if (currentStep > 1) showExitWarning = true
            else {
                onExitFlow()
            }
        },
        onConfirm = {
            isExiting = true
            showExitWarning = false
            onExitFlow()
        },
        onDismiss = { showExitWarning = false }
    )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            FlowAppBar(
                title = stringResource(R.string.cash_transfer_label),
                currentStep = if (isLimitReached) 0 else currentStep,
                totalSteps = if (isLimitReached) 0 else totalSteps,
                scrollBehavior = scrollBehavior,
                onBack = {
                    if (currentStep > 1) showExitWarning = true
                    else {
                        focusManager.clearFocus()
                        authorizationViewModel.clearAuthorization()
                        navController.popBackStack()
                    }
                },
                expandedContent = {
                    if (cashTransferViewModel.recipientName.isNotEmpty() && !isLimitReached) {
                        Column {
                            Text(
                                text = "To: ${cashTransferViewModel.recipientName}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            val securityStartStep = if (authorizationViewModel.isQuickPay) 2 else 3
                            if (currentStep >= securityStartStep && cashTransferViewModel.amount.isNotEmpty()) {
                                Text(
                                    text = "${cashTransferViewModel.amount} ${CurrencyUtils.getCurrencySymbol(user.countryCode)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLimitReached) {
            LimitReachedScreen(
                nextResetMillis = velocityStatus?.nextResetMillis ?: 0L,
                onBack = {onExitFlow()}
            )
        }
           else if (authorizationViewModel.activeFlow != FlowType.NONE && !isExiting) {
                AnimatedContent(
                    targetState = currentStep,
                    transitionSpec = {
                        if (targetState > initialState) {
                            slideInHorizontally { it } + fadeIn() togetherWith
                                    slideOutHorizontally { -it } + fadeOut()
                        } else {
                            EnterTransition.None togetherWith ExitTransition.None
                        }.using(SizeTransform(clip = false))
                    },
                    label = "StepTransition"
                ) { targetStep ->
                    val actualStep = if (authorizationViewModel.isQuickPay) targetStep + 1 else targetStep

                    when (actualStep) {
                        1 -> RecipientStepContent(
                            viewModel = cashTransferViewModel,
                            onNext = { authorizationViewModel.moveToNextStep() }
                        )
                        2 -> AmountStepContent(
                            viewModel = cashTransferViewModel,
                            onNext = { authorizationViewModel.moveToNextStep() }
                        )
                        3 -> OtpStepContent(
                            otpViewModelFactory = otpViewModelFactory,
                            onOtpSuccess = { authorizationViewModel.proceedAfterOtp(navController, origin) }
                        )
                        4 -> PasswordStepContent(
                            passwordConfirmationViewModelFactory = passwordConfirmationViewModelFactory,
                            sessionViewModel = sessionViewModel,
                            onPasswordVerificationSuccess = {
                                focusManager.clearFocus()
                                isExiting = true
                                authorizationViewModel.proceedAfterPassword(
                                    navController,
                                    "$TRANSACTION_RESULT_ROUTE?origin=$origin",
                                    "$CASH_TRANSFER_ROUTE?origin=$origin"
                                )
                            }
                        )
                    }
                }
            } else {
                Box(Modifier.fillMaxSize())
            }

            if (deviceSpec is DeviceSpec.MobileLandscape) {
                Spacer(modifier = Modifier.height(400.dp))
            }
        }
    }
}

@Composable
private fun LimitReachedScreen(
    nextResetMillis: Long,
    onBack: () -> Unit
) {
    val resetTime = remember(nextResetMillis) {
        if (nextResetMillis == 0L) ""
        else BankDateFactory.fromMillis(nextResetMillis).toFullDateTimeDisplay()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Block,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Daily Limit Reached",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "You have reached your transaction limit for today. Please try again after $resetTime.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Go Back")
        }
    }
}