package com.example.bankapp.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bankapp.R
import com.example.bankapp.di.viewmodelfactory.AddBeneficiaryViewModelFactory
import com.example.bankapp.di.viewmodelfactory.OtpViewModelFactory
import com.example.bankapp.di.viewmodelfactory.PasswordConfirmationViewModelFactory
import com.example.bankapp.ui.components.BackHandlerWithWarning
import com.example.bankapp.ui.components.appbar.FlowAppBar
import com.example.bankapp.ui.components.navigators.ADD_BENEFICIARY_ROUTE
import com.example.bankapp.ui.components.navigators.HOME_ROUTE
import com.example.bankapp.ui.components.navigators.MAIN_ROUTE
import com.example.bankapp.ui.components.navigators.TRANSACTION_RESULT_ROUTE
import com.example.bankapp.ui.screens.OtpStepContent
import com.example.bankapp.ui.screens.PasswordStepContent
import com.example.bankapp.ui.theme.LocalDeviceSpec
import com.example.bankapp.ui.theme.DeviceSpec
import com.example.bankapp.viewmodels.AuthorizationViewModel
import com.example.bankapp.viewmodels.FlowType
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bankapp.ui.components.navigators.PAY_ROUTE
import com.example.bankapp.ui.screens.beneficiaryscreens.AddBeneficiaryStepContent
import com.example.bankapp.viewmodels.AddBeneficiaryViewModel
import com.example.bankapp.viewmodels.SessionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBeneficiaryFlowHost(
    navController: NavController,
    authorizationViewModel: AuthorizationViewModel,
    addBeneficiaryViewModelFactory: AddBeneficiaryViewModelFactory,
    otpViewModelFactory: OtpViewModelFactory,
    passwordConfirmationViewModelFactory: PasswordConfirmationViewModelFactory,
    sessionViewModel: SessionViewModel
) {
    val addBeneficiaryViewModel: AddBeneficiaryViewModel = viewModel(factory = addBeneficiaryViewModelFactory)

    val currentStep = authorizationViewModel.currentStep
    val totalSteps = authorizationViewModel.totalSteps
    val focusManager = LocalFocusManager.current
    val deviceSpec = LocalDeviceSpec.current

    var showExitWarning by remember { mutableStateOf(false) }
    var isExiting by remember { mutableStateOf(false) }

    val scrollBehavior = if (deviceSpec is DeviceSpec.MobileLandscape) {
        TopAppBarDefaults.enterAlwaysScrollBehavior()
    } else {
        TopAppBarDefaults.pinnedScrollBehavior()
    }

    val onExitFlow = {
        focusManager.clearFocus()
        addBeneficiaryViewModel.clearState()
        authorizationViewModel.clearAuthorization()
        navController.popBackStack()
    }

    LaunchedEffect(Unit) {
        if (authorizationViewModel.activeFlow == FlowType.NONE) {
            authorizationViewModel.startFlow(FlowType.ADD_BENEFICIARY)
        }
    }

    BackHandlerWithWarning(
        showDialog = showExitWarning,
        onShowDialogConfirm = {
            if (currentStep > 1) showExitWarning = true
            else onExitFlow()
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
                title = stringResource(R.string.add_beneficiary),
                currentStep = currentStep,
                totalSteps = totalSteps,
                scrollBehavior = scrollBehavior,
                onBack = {
                    if (currentStep > 1) showExitWarning = true
                    else {
                        focusManager.clearFocus()
                        addBeneficiaryViewModel.clearState()
                        authorizationViewModel.clearAuthorization()
                        navController.popBackStack()
                    }
                },
                showStepMeter = deviceSpec !is DeviceSpec.MobileLandscape
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (authorizationViewModel.activeFlow != FlowType.NONE && !isExiting) {
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
                    when (targetStep) {
                        1 -> AddBeneficiaryStepContent(
                            viewModel = addBeneficiaryViewModel,
                            onNext = { authorizationViewModel.moveToNextStep() }
                        )
                        2 -> OtpStepContent(
                            otpViewModelFactory = otpViewModelFactory,
                            onOtpSuccess = { authorizationViewModel.proceedAfterOtp(navController, PAY_ROUTE) }
                        )
                        3 -> PasswordStepContent(
                            passwordConfirmationViewModelFactory = passwordConfirmationViewModelFactory,
                            sessionViewModel = sessionViewModel,
                            onPasswordVerificationSuccess = {
                                focusManager.clearFocus()
                                isExiting = true
                                authorizationViewModel.proceedAfterPassword(
                                    navController,
                                    "$TRANSACTION_RESULT_ROUTE?origin=$PAY_ROUTE",
                                    ADD_BENEFICIARY_ROUTE
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