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

import com.example.bankapp.entities.AuthorizationctionState
import kotlinx.coroutines.launch
import java.util.UUID

class ResultViewModel(
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
                    authorizationViewModel.authorizationActionState = AuthorizationctionState.FAILURE
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
                        authorizationViewModel.authorizationActionState = AuthorizationctionState.SUCCESS
                    }

                    is TransactionResult.Error -> {
                        val reason = (transactionResult as TransactionResult.Error).message
                        authorizationViewModel.authorizationActionState = AuthorizationctionState.FAILURE
                        authorizationViewModel.failureReason = reason
                    }
                    else -> {}
                }
            }catch (e: Exception){
                e.printStackTrace()
                authorizationViewModel.authorizationActionState = AuthorizationctionState.FAILURE
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
                    authorizationViewModel.authorizationActionState = AuthorizationctionState.FAILURE
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
                        authorizationViewModel.authorizationActionState = AuthorizationctionState.SUCCESS
                    }
                    is TransactionResult.Error -> {
                        val reason = (transactionResult as TransactionResult.Error).message
                        authorizationViewModel.authorizationActionState = AuthorizationctionState.FAILURE
                        authorizationViewModel.failureReason = reason
                    }
                    else -> {
                        authorizationViewModel.authorizationActionState = AuthorizationctionState.FAILURE
                    }
                }
            }
            catch (e: Exception){
                e.printStackTrace()
                authorizationViewModel.authorizationActionState = AuthorizationctionState.FAILURE
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
                    exchangeRate = data.exchangeRate,
                    idempotencyKey = idempotencyKey
                )

                if (transactionResult is TransactionResult.Success) {
                    authorizationViewModel.assignTransactionId((transactionResult as TransactionResult.Success).transactionId)
                    authorizationViewModel.authorizationActionState = AuthorizationctionState.SUCCESS
                } else {
                    val reason = (transactionResult as TransactionResult.Error).message
                    authorizationViewModel.authorizationActionState = AuthorizationctionState.FAILURE
                    authorizationViewModel.failureReason = reason
                }
            } catch (e: Exception) {
                authorizationViewModel.authorizationActionState = AuthorizationctionState.FAILURE
            } finally {
                idempotencyKey = UUID.randomUUID().toString()
            }
        }
    }

    fun addBeneficiary(addBeneficiaryAuthorizationIntent: AuthorizationIntent.AddBeneficiary){
        viewModelScope.launch {
            try {
              val result =  beneficiaryRepository.addBeneficiary(userId = addBeneficiaryAuthorizationIntent.myUserId, beneficiaryUserId = addBeneficiaryAuthorizationIntent.otherUserId, nickname = addBeneficiaryAuthorizationIntent.nickname)
              if(result== (-1).toLong()){
                  authorizationViewModel.authorizationActionState = AuthorizationctionState.FAILURE
              }
              else{
                  authorizationViewModel.authorizationActionState = AuthorizationctionState.SUCCESS
              }
            }
            catch (_: Exception){
                authorizationViewModel.authorizationActionState = AuthorizationctionState.FAILURE
            }

        }
    }
}

