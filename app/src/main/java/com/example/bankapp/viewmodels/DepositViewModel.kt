package com.example.bankapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.bankapp.R
import com.example.bankapp.entities.SessionState
import com.example.bankapp.entities.errors.FormError
import com.example.bankapp.entities.errors.TransactionResult
import com.example.bankapp.repositories.TransactionRepository
import com.example.bankapp.ui.components.navigators.DEPOSIT_ROUTE
import com.example.bankapp.ui.components.navigators.HOME_ROUTE
import com.example.bankapp.ui.components.navigators.INDIVIDUAL_TRANSACTION_LOG_ROUTE
import com.example.bankapp.ui.components.navigators.MAIN_ROUTE
import com.example.bankapp.ui.components.navigators.PASSWORD_CONFIRMATION_ROUTE
import com.example.bankapp.ui.components.navigators.TRANSACTION_RESULT_ROUTE
import com.example.bankapp.services.HomeSessionHandlerManager
import com.example.bankapp.usecases.CurrentSessionIntent
import com.example.bankapp.usecases.HomeSessionHandler
import com.example.bankapp.utilities.amountFieldValidator
import com.example.bankapp.utilities.amountRegex

import com.example.bankapp.utilities.emptyTextFieldErrorMessageBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class DepositViewModel(
    sessionState: SessionState.Authenticated.AccountRegistered,
    private val transactionRepository: TransactionRepository
): ViewModel() {
    private val account = sessionState.account
    private val user = sessionState.user

    private val homeSessionHandler: HomeSessionHandler
        get() = HomeSessionHandlerManager.currentHandler

    private val deposit: HomeSessionHandler.Deposit
        get() = HomeSessionHandlerManager.currentHandler as HomeSessionHandler.Deposit

    var amount by mutableStateOf<String>("")
        private set

    var amountError by mutableStateOf<FormError?>(null)
        private set

    fun onAmountChange(newAmount: String) {
        if (newAmount.isEmpty() || newAmount.matches(amountRegex))
            amount = newAmount
        amountError =
            newAmount.emptyTextFieldErrorMessageBuilder(R.string.amount_field_name)
                ?: newAmount.amountFieldValidator()
        onSubmitErrorReset()
    }

    var submitError by mutableStateOf<FormError?>(null)
        private set

    var transactionResult by mutableStateOf<TransactionResult?>(null)
        private set


    private var idempotencyKey = UUID.randomUUID().toString()

    fun onSubmitErrorReset() {
        if (amountError == null) {
            submitError = null
        }
    }

    var isLoading by mutableStateOf(false)
        private set

    fun resetUponSuccess() {
        onAmountChange("")
        amountError = null
        submitError = null
    }

    var isVerifySuccessful by mutableStateOf(false)
        private set

    private var alreadySuceeded = false

    fun onVerifySuccessful(navController: NavController) {
        if(alreadySuceeded)
            return

        alreadySuceeded = true

        deposit.onActionSuccessPrimaryAction = {
            val transactionId = deposit.transactionId

            if (transactionId != null) {
                navController.navigate("$INDIVIDUAL_TRANSACTION_LOG_ROUTE/$transactionId") {
                    popUpTo(HOME_ROUTE) { inclusive = false }
                }
            } else {
                navController.navigate(HOME_ROUTE) {
                    popUpTo(MAIN_ROUTE) {
                        inclusive = true
                    }
                }
            }
        }

        deposit.onActionFailurePrimaryAction = {
            navController.navigate(DEPOSIT_ROUTE){
                popUpTo(HOME_ROUTE) {
                    inclusive = false
                }
            }
        }

        homeSessionHandler.onOtpSuccess = {
            navController.navigate("$PASSWORD_CONFIRMATION_ROUTE/$DEPOSIT_ROUTE")
        }

        homeSessionHandler.onPasswordSuccess = {
            navController.navigate(TRANSACTION_RESULT_ROUTE)
        }
    }

            fun onSubmit() {

                if (isLoading)
                    return

                onAmountChange(amount)

                onSubmitErrorReset()

                viewModelScope.launch(Dispatchers.IO) {

                    try {
                        isLoading = true
                        if (amountError != null) {
                            submitError = FormError.InvalidData
                            return@launch
                        }

                        val convertedAmount = amount.toBigDecimalOrNull()
                        if (convertedAmount == null) {
                            amountError = FormError.InvalidAmount
                            submitError = FormError.InvalidData
                            return@launch
                        }

                        if (submitError == null) {
                            deposit.onInitialize(
                                userAccNo = account.accNo,
                                userId = account.userId,
                                amount = convertedAmount
                            )

                            deposit.intent = CurrentSessionIntent.DEPOSIT

                            isVerifySuccessful = true
                        }
                    } catch (e: Exception) {
                        println("Deposit error: ${e.message}")
                        submitError = FormError.UnknownError
                    } finally {
                        isLoading = false
                    }

                }
            }
        }



/*
if(submitError==null){
                    if(PasswordHashingService.matches(password, user.passwordHashed))
                    {
                        transactionResult = transactionRepository.deposit(
                            accountNo = account.accNo,
                            amount = convertedAmount,
                            idempotencyKey = idempotencyKey
                        )
                        if (transactionResult != TransactionResult.Error.RepeatedTransaction)
                            idempotencyKey = UUID.randomUUID().toString()
                        if(transactionResult is TransactionResult.Success)
                            resetUponSuccess()

                    }
                    else{
                        submitError = FormError.PasswordDoesntMatch
                        return@launch
                    }
                }
 */