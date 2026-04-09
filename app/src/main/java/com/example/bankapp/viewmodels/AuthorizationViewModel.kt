package com.example.bankapp.viewmodels

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.bankapp.R
import com.example.bankapp.core.datecompatability.BankDateFactory
import com.example.bankapp.entities.AuthorizationIntent
import com.example.bankapp.entities.AuthorizationctionState
import com.example.bankapp.entities.uientities.uidata.ResultButton
import com.example.bankapp.entities.uientities.uidata.ResultContent
import com.example.bankapp.entities.uientities.uidata.ResultUiText
import com.example.bankapp.ui.components.navigators.HOME_ROUTE
import com.example.bankapp.ui.components.navigators.PAY_TO_BENEFICIARY_ROUTE
import com.example.bankapp.ui.components.navigators.TRANSACTION_RESULT_ROUTE
import com.example.bankapp.utilities.CurrencyUtils
import java.math.BigDecimal

class AuthorizationViewModel : ViewModel() {
    var authorizationIntent by mutableStateOf<AuthorizationIntent?>(null)
        private set

    var currentStep by mutableIntStateOf(1)
        private set

    var activeFlow by mutableStateOf(FlowType.NONE)
        private set

    var isQuickPay by mutableStateOf(false)

    val totalSteps: Int
        get() = when (activeFlow) {
            FlowType.DEPOSIT, FlowType.ADD_BENEFICIARY -> 3
            FlowType.CASH_TRANSFER, FlowType.INTERNATIONAL -> {
                val isFriend = when (val intent = authorizationIntent) {
                    is AuthorizationIntent.CashTransfer -> intent.isFriend
                    is AuthorizationIntent.InternationalTransfer -> intent.isFriend
                    else -> isQuickPay
                }

                when {
                    isQuickPay && isFriend -> 2
                    isQuickPay && !isFriend -> 3
                    !isQuickPay && isFriend -> 3
                    else -> 4
                }
            }
            else -> 0
        }

    fun startFlow(type: FlowType) {
        if(activeFlow==type) return
        resetSteps()
        activeFlow = type
        authorizationIntent = null
    }

    fun moveToNextStep() {
        if (currentStep < totalSteps) {
            currentStep++
        }
    }

    fun moveToPreviousStep() {
        if (currentStep > 1) {
            currentStep--
        }
    }

    fun resetSteps() {
        currentStep = 1
        activeFlow = FlowType.NONE
    }

    fun clearAuthorization() {
        authorizationIntent = null
        activeFlow = FlowType.NONE
        currentStep = 1
        authorizationActionState = AuthorizationctionState.LOADING
        isQuickPay = false
    }

    var authorizationActionState by mutableStateOf(AuthorizationctionState.LOADING)

    fun initializeCashTransfer(fromAccNo: Long, toAccNo: Long, amount: BigDecimal, friend: Boolean, countryCode: String) {
        authorizationIntent = AuthorizationIntent.CashTransfer(
            fromAccNo = fromAccNo,
            toAccNo = toAccNo,
            amount = amount,
            isFriend = friend,
            countryCode = countryCode
        )
    }

    fun initializeAddBeneficiary(fromUserId: Long, toUserId: Long, nickname: String) {
        authorizationIntent = AuthorizationIntent.AddBeneficiary(
            fromUserId,
            toUserId,
            nickname
        )
    }

    fun initializeDeposit(accNo: Long, amount: BigDecimal, countryCode: String) {
        authorizationIntent = AuthorizationIntent.Deposit(
            userAccNo = accNo,
            amount = amount,
            countryCode = countryCode
        )
    }

    fun initializeInternationalTransfer(fromAccNo: Long, toAccNo: Long, amount: BigDecimal, isFriend: Boolean, baseCurrency: String, targetCurrency: String, rate: BigDecimal) {
        authorizationIntent = AuthorizationIntent.InternationalTransfer(
            fromAccNo,
            toAccNo,
            isFriend,
            amount,
            baseCurrency,
            targetCurrency,
            rate
        )
    }

    fun assignTransactionId(transactionId: String) {
        when (val intent = authorizationIntent) {
            is AuthorizationIntent.CashTransfer -> {
                authorizationIntent = intent.copy(transactionId = transactionId)
            }
            is AuthorizationIntent.Deposit -> {
                authorizationIntent = intent.copy(transactionId = transactionId)
            }
            is AuthorizationIntent.InternationalTransfer -> {
                authorizationIntent = intent.copy(transactionId = transactionId)
            }
            else -> {}
        }
    }

    fun proceedAfterOtp(navController: NavController, origin: String? = null ) {
        val intent = authorizationIntent

        if(intent is AuthorizationIntent.Deposit || intent is AuthorizationIntent.AddBeneficiary) {
            moveToNextStep()
            return
        }

        val isFriend = when (intent) {
            is AuthorizationIntent.CashTransfer -> intent.isFriend
            is AuthorizationIntent.InternationalTransfer -> intent.isFriend
            else -> false
        }

        val targetOrigin = origin ?: HOME_ROUTE

        if (isFriend) {
            navController.navigate("$TRANSACTION_RESULT_ROUTE?origin=$targetOrigin")
        } else if (currentStep < totalSteps) {
            moveToNextStep()
        } else {
            navController.navigate("$TRANSACTION_RESULT_ROUTE?origin=$targetOrigin")
        }
    }

    fun proceedAfterPassword(navController: NavController, navigateRoute: String, popupRoute: String) {
        navController.navigate(navigateRoute) {
            popUpTo(popupRoute) {
                inclusive = true
            }
        }
    }

    var failureReason by mutableStateOf(R.string.action_failed)

    var additionalFailureMessage by mutableStateOf<String?>(null)

    var successMessage by mutableStateOf(R.string.action_success)

    fun getResultContent(onDone: () -> Unit, onRetry: () -> Unit): ResultContent {
        val isSuccess = authorizationActionState == AuthorizationctionState.SUCCESS

        return when (val intent = authorizationIntent) {
            is AuthorizationIntent.CashTransfer -> {
                successMessage = R.string.transaction_success
                if (isSuccess) {
                    ResultContent(
                        text1 = ResultUiText.StringResource(successMessage),
                        text2 = ResultUiText.DynamicString(CurrencyUtils.formatCurrency(intent.amount, intent.countryCode) + " " + CurrencyUtils.getCurrencySymbol(intent.countryCode)),
                        text3 = ResultUiText.DynamicString(BankDateFactory.now().toFullDateTimeDisplay()),
                        primaryButton = ResultButton(ResultUiText.StringResource(R.string.done), onDone)
                    )
                } else {
                    ResultContent(
                        text1 = ResultUiText.StringResource(R.string.transaction_failed),
                        text2 = ResultUiText.StringResource(failureReason),
                        text3 = ResultUiText.DynamicString(BankDateFactory.now().toFullDateTimeDisplay()),
                        primaryButton = ResultButton(ResultUiText.StringResource(R.string.try_again), onRetry)
                    )
                }
            }
            is AuthorizationIntent.AddBeneficiary -> {
                successMessage = R.string.beneficiary_added_successfully
                if (isSuccess) {
                    ResultContent(
                        text1 = ResultUiText.StringResource(successMessage),
                        primaryButton = ResultButton(ResultUiText.StringResource(R.string.done), onClick = onDone)
                    )
                } else {
                    ResultContent(
                        text1 = ResultUiText.StringResource(R.string.failed_label),
                        primaryButton = ResultButton(ResultUiText.StringResource(R.string.try_again), onClick = onRetry)
                    )
                }
            }
            is AuthorizationIntent.Deposit -> {
                successMessage = R.string.transaction_success
                if (isSuccess) {
                    ResultContent(
                        text1 = ResultUiText.StringResource(successMessage),
                        text2 = ResultUiText.DynamicString(CurrencyUtils.formatCurrency(intent.amount, intent.countryCode) + " " + CurrencyUtils.getCurrencySymbol(intent.countryCode)),
                        text3 = ResultUiText.DynamicString(BankDateFactory.now().toFullDateTimeDisplay()),
                        primaryButton = ResultButton(text = ResultUiText.StringResource(R.string.done), onDone),
                    )
                } else {
                    ResultContent(
                        text1 = ResultUiText.StringResource(R.string.transaction_failed),
                        text2 = ResultUiText.StringResource(failureReason, additionalFailureMessage),
                        primaryButton = ResultButton(text = ResultUiText.StringResource(R.string.try_again), onClick = onRetry),
                    )
                }
            }
            is AuthorizationIntent.InternationalTransfer -> {
                val converted = intent.baseAmount.multiply(intent.exchangeRate)
                if (isSuccess) {
                    ResultContent(
                        text1 = ResultUiText.StringResource(R.string.transaction_success),
                        text2 = ResultUiText.DynamicString("${intent.baseAmount} ${intent.baseCurrency} -> ${CurrencyUtils.formatDecimal(converted)} ${intent.targetCurrency}"),
                        text3 = ResultUiText.DynamicString(BankDateFactory.now().toFullDateTimeDisplay()),
                        primaryButton = ResultButton(ResultUiText.StringResource(R.string.done), onDone)
                    )
                } else {
                    ResultContent(
                        text1 = ResultUiText.StringResource(R.string.transaction_failed),
                        text2 = ResultUiText.StringResource(failureReason),
                        primaryButton = ResultButton(text = ResultUiText.StringResource(R.string.try_again), onClick = onRetry),
                    )
                }
            }
            else -> ResultContent(
                text1 = ResultUiText.StringResource(R.string.transaction_failed),
                text2 = ResultUiText.StringResource(failureReason),
                primaryButton = ResultButton(ResultUiText.StringResource(R.string.try_again), onRetry)
            )
        }
    }
}

enum class FlowType {
    DEPOSIT, CASH_TRANSFER, INTERNATIONAL, ADD_BENEFICIARY, NONE
}