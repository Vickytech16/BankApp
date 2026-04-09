package com.example.bankapp.viewmodels

import com.example.bankapp.entities.AuthorizationIntent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.entities.errors.TransactionResult
import com.example.bankapp.repositories.BeneficiaryRepository
import com.example.bankapp.repositories.TransactionRepository
import com.example.bankapp.entities.AuthorizationctionState
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.UUID

class ResultViewModel(
    private val transactionRepository: TransactionRepository,
    private val beneficiaryRepository: BeneficiaryRepository,
    private val authorizationViewModel: AuthorizationViewModel
) : ViewModel() {

    var transactionResult by mutableStateOf<TransactionResult?>(null)
        private set

    var actionExecuted by mutableStateOf(false)
    private var idempotencyKey by mutableStateOf(UUID.randomUUID().toString())

    fun cashTransfer(cashTransferAuthorizationIntent: AuthorizationIntent.CashTransfer) {

        if (cashTransferAuthorizationIntent.amount <= BigDecimal.ZERO) return

        viewModelScope.launch {
            try {
                if (cashTransferAuthorizationIntent.toAccNo == 0L) {
                    transactionResult = TransactionResult.Error.UnKnown
                    authorizationViewModel.authorizationActionState = AuthorizationctionState.FAILURE
                    return@launch
                }

                val result = transactionRepository.cashTransfer(
                    cashTransferAuthorizationIntent.fromAccNo,
                    cashTransferAuthorizationIntent.toAccNo,
                    cashTransferAuthorizationIntent.amount,
                    idempotencyKey
                )
                transactionResult = result

                when (result) {
                    is TransactionResult.Success -> {
                        authorizationViewModel.assignTransactionId(result.transactionId)
                        authorizationViewModel.authorizationActionState = AuthorizationctionState.SUCCESS
                    }
                    is TransactionResult.Error -> {
                        authorizationViewModel.failureReason = result.message
                        authorizationViewModel.authorizationActionState = AuthorizationctionState.FAILURE
                    }
                    else -> {
                        authorizationViewModel.authorizationActionState = AuthorizationctionState.FAILURE
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                authorizationViewModel.authorizationActionState = AuthorizationctionState.FAILURE
            }
        }
    }

    fun Deposit(depositAuthorizationIntent: AuthorizationIntent.Deposit) {
        viewModelScope.launch {
            try {
                if (depositAuthorizationIntent.userAccNo == 0L) {
                    transactionResult = TransactionResult.Error.UnKnown
                    authorizationViewModel.authorizationActionState = AuthorizationctionState.FAILURE
                    return@launch
                }

                val result = transactionRepository.deposit(
                    accountNo = depositAuthorizationIntent.userAccNo,
                    amount = depositAuthorizationIntent.amount,
                    idempotencyKey = idempotencyKey
                )
                transactionResult = result

                when (result) {
                    is TransactionResult.Success -> {
                        authorizationViewModel.assignTransactionId(result.transactionId)
                        authorizationViewModel.authorizationActionState = AuthorizationctionState.SUCCESS
                    }
                    is TransactionResult.Error -> {
                        if(result is TransactionResult.Error.LimitExceeded.MaxBalanceLimitExceededDeposit){
                            authorizationViewModel.additionalFailureMessage = result.maxAmount.toString() + "$"
                        }
                          authorizationViewModel.failureReason = result.message
                        authorizationViewModel.authorizationActionState = AuthorizationctionState.FAILURE
                    }
                    else -> {
                        authorizationViewModel.authorizationActionState = AuthorizationctionState.FAILURE
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                authorizationViewModel.authorizationActionState = AuthorizationctionState.FAILURE
            }
        }
    }

    fun InternationalTransfer(data: AuthorizationIntent.InternationalTransfer) {

        if (data.baseAmount <= BigDecimal.ZERO) return

        viewModelScope.launch {
            try {
                val result = transactionRepository.internationalTransfer(
                    fromAccountNo = data.fromAccNo,
                    toAccountNo = data.toAccNo,
                    amount = data.baseAmount,
                    exchangeRate = data.exchangeRate,
                    idempotencyKey = idempotencyKey
                )
                transactionResult = result

                if (result is TransactionResult.Success) {
                    authorizationViewModel.assignTransactionId(result.transactionId)
                    authorizationViewModel.authorizationActionState = AuthorizationctionState.SUCCESS
                } else if (result is TransactionResult.Error) {
                    authorizationViewModel.failureReason = result.message
                    authorizationViewModel.authorizationActionState = AuthorizationctionState.FAILURE
                } else {
                    authorizationViewModel.authorizationActionState = AuthorizationctionState.FAILURE
                }
            } catch (e: Exception) {
                authorizationViewModel.authorizationActionState = AuthorizationctionState.FAILURE
            }
        }
    }

    fun addBeneficiary(addBeneficiaryAuthorizationIntent: AuthorizationIntent.AddBeneficiary) {
        viewModelScope.launch {
            try {
                val result = beneficiaryRepository.addBeneficiary(
                    userId = addBeneficiaryAuthorizationIntent.myUserId,
                    beneficiaryUserId = addBeneficiaryAuthorizationIntent.otherUserId,
                    nickname = addBeneficiaryAuthorizationIntent.nickname
                )
                if (result == -1L) {
                    authorizationViewModel.authorizationActionState = AuthorizationctionState.FAILURE
                } else {
                    authorizationViewModel.authorizationActionState = AuthorizationctionState.SUCCESS
                }
            } catch (_: Exception) {
                authorizationViewModel.authorizationActionState = AuthorizationctionState.FAILURE
            }
        }
    }
}