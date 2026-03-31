package com.example.bankapp.viewmodels

import com.example.bankapp.entities.AuthorizationIntent
import AuthorizationViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.entities.errors.TransactionResult
import com.example.bankapp.repositories.BeneficiaryRepository
import com.example.bankapp.repositories.TransactionRepository

import com.example.bankapp.entities.types.ActionState
import kotlinx.coroutines.launch
import java.util.UUID

class TransactionResultViewModel(
    private val transactionRepository: TransactionRepository,
    private val beneficiaryRepository: BeneficiaryRepository,
    private val authorizationViewModel: AuthorizationViewModel
): ViewModel() {
    var transactionResult by mutableStateOf<TransactionResult?>(null)
        private set
    var actionExecuted by mutableStateOf(false)
    private var idempotencyKey by mutableStateOf(UUID.randomUUID().toString())

    fun cashTransfer(cashTransferAuthorizationIntent: AuthorizationIntent.CashTransfer){


        viewModelScope.launch {
            try{
                if(cashTransferAuthorizationIntent.toAccNo==0.toLong()){
                    transactionResult = TransactionResult.Error.UnKnown
                    authorizationViewModel.actionState = ActionState.FAILURE
                    return@launch
                }

                transactionResult = transactionRepository.cashTransfer(
                    cashTransferAuthorizationIntent.fromAccNo,
                    cashTransferAuthorizationIntent.toAccNo,
                    cashTransferAuthorizationIntent.amount,
                    idempotencyKey
                )

                when (transactionResult) {
                    is TransactionResult.Success -> {
                        authorizationViewModel.assignTransactionId((transactionResult as TransactionResult.Success).transactionId)
                        authorizationViewModel.actionState = ActionState.SUCCESS
                    }

                    is TransactionResult.Error -> {
                        val reason = (transactionResult as TransactionResult.Error).message
                        authorizationViewModel.actionState = ActionState.FAILURE
                        authorizationViewModel.failureReason = reason
                    }
                    else -> {}
                }
            }catch (e: Exception){
                e.printStackTrace()
                authorizationViewModel.actionState = ActionState.FAILURE
            }finally {
                idempotencyKey = UUID.randomUUID().toString()
            }
        }
    }

    fun Deposit(depositAuthorizationIntent: AuthorizationIntent.Deposit){
        viewModelScope.launch {
            try {

                if(depositAuthorizationIntent.userAccNo == 0.toLong()){
                    transactionResult = TransactionResult.Error.UnKnown
                    authorizationViewModel.actionState = ActionState.FAILURE
                    return@launch
                }

                transactionResult = transactionRepository.deposit(
                    accountNo = depositAuthorizationIntent.userAccNo,
                    amount = depositAuthorizationIntent.amount,
                    idempotencyKey = idempotencyKey
                )

                when(transactionResult){
                    is TransactionResult.Success -> {
                        authorizationViewModel.assignTransactionId((transactionResult as TransactionResult.Success).transactionId)
                        authorizationViewModel.actionState = ActionState.SUCCESS
                    }
                    is TransactionResult.Error -> {
                        val reason = (transactionResult as TransactionResult.Error).message
                        authorizationViewModel.actionState = ActionState.FAILURE
                        authorizationViewModel.failureReason = reason
                    }
                    else -> {
                        authorizationViewModel.actionState = ActionState.FAILURE
                    }
                }
            }
            catch (e: Exception){
                e.printStackTrace()
                authorizationViewModel.actionState = ActionState.FAILURE
            }
            finally {
                idempotencyKey = UUID.randomUUID().toString()
            }
        }
    }

    fun InternationalTransfer(data: AuthorizationIntent.InternationalTransfer) {
        viewModelScope.launch {
            try {
                transactionResult = transactionRepository.internationalTransfer(
                    fromAccountNo = data.fromAccNo,
                    toAccountNo = data.toAccNo,
                    amount = data.baseAmount,
                    targetCurrency = data.targetCurrency,
                    exchangeRate = data.exchangeRate,
                    idempotencyKey = idempotencyKey
                )

                if (transactionResult is TransactionResult.Success) {
                    authorizationViewModel.assignTransactionId((transactionResult as TransactionResult.Success).transactionId)
                    authorizationViewModel.actionState = ActionState.SUCCESS
                } else {
                    val reason = (transactionResult as TransactionResult.Error).message
                    authorizationViewModel.actionState = ActionState.FAILURE
                    authorizationViewModel.failureReason = reason
                }
            } catch (e: Exception) {
                authorizationViewModel.actionState = ActionState.FAILURE
            } finally {
                idempotencyKey = UUID.randomUUID().toString()
            }
        }
    }

    fun AddBeneficiary(addBeneficiaryAuthorizationIntent: AuthorizationIntent.AddBeneficiary){
        viewModelScope.launch {
            try {
              val result =  beneficiaryRepository.addBeneficiary(userId = addBeneficiaryAuthorizationIntent.myUserId, beneficiaryUserId = addBeneficiaryAuthorizationIntent.otherUserId,)
              if(result== (-1).toLong()){
                  authorizationViewModel.actionState = ActionState.FAILURE
              }
              else{
                  authorizationViewModel.actionState = ActionState.SUCCESS
              }
            }
            catch (_: Exception){
                authorizationViewModel.actionState = ActionState.FAILURE
            }

        }
    }
}

