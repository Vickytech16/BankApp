package com.example.bankapp.viewmodels

import FlowData
import SharedTransactionViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.entities.SessionState
import com.example.bankapp.entities.errors.TransactionResult
import com.example.bankapp.repositories.BeneficiaryRepository
import com.example.bankapp.repositories.TransactionRepository

import com.example.bankapp.di.providers.HomeSessionHandlerProvider
import com.example.bankapp.entities.types.ActionState
import com.example.bankapp.usecases.HomeSessionHandler
import kotlinx.coroutines.launch
import java.util.UUID

class TransactionResultViewModel(
    private val transactionRepository: TransactionRepository,
    private val beneficiaryRepository: BeneficiaryRepository,
    private val sharedTransactionViewModel: SharedTransactionViewModel
): ViewModel() {
    var transactionResult by mutableStateOf<TransactionResult?>(null)
        private set
    var actionExecuted by mutableStateOf(false)
    private var idempotencyKey by mutableStateOf(UUID.randomUUID().toString())

    fun cashTransfer(cashTransferFlowData: FlowData.CashTransfer){

        val homeSessionHandler = HomeSessionHandlerProvider.currentHandler

        viewModelScope.launch {
            try{
                if(cashTransferFlowData.toAccNo==0.toLong()){
                    transactionResult = TransactionResult.Error.UnKnown
                    homeSessionHandler.onActionStateChange(ActionState.FAILURE)
                    return@launch
                }

                transactionResult = transactionRepository.cashTransfer(
                    cashTransferFlowData.fromAccNo,
                    cashTransferFlowData.toAccNo,
                    cashTransferFlowData.amount,
                    idempotencyKey
                )

                when (transactionResult) {
                    is TransactionResult.Success -> {
                        sharedTransactionViewModel.assignTransactionId((transactionResult as TransactionResult.Success).transactionId)
                        sharedTransactionViewModel.actionState = ActionState.SUCCESS
                    }

                    is TransactionResult.Error -> {
                        val reason = (transactionResult as TransactionResult.Error).message
                        sharedTransactionViewModel.actionState = ActionState.FAILURE
                        sharedTransactionViewModel.failureReason = reason
                    }
                    else -> {
                        homeSessionHandler.onActionStateChange(ActionState.FAILURE)
                    }
                }
            }catch (e: Exception){
                e.printStackTrace()
                homeSessionHandler.onActionStateChange(ActionState.FAILURE)
            }finally {
                idempotencyKey = UUID.randomUUID().toString()
            }
        }
    }

    fun Deposit(depositFlowData: FlowData.Deposit){
        viewModelScope.launch {
            try {

                if(depositFlowData.userAccNo == 0.toLong()){
                    transactionResult = TransactionResult.Error.UnKnown
                    sharedTransactionViewModel.actionState = ActionState.FAILURE
                    return@launch
                }

                transactionResult = transactionRepository.deposit(
                    accountNo = depositFlowData.userAccNo,
                    amount = depositFlowData.amount,
                    idempotencyKey = idempotencyKey
                )

                when(transactionResult){
                    is TransactionResult.Success -> {
                        sharedTransactionViewModel.assignTransactionId((transactionResult as TransactionResult.Success).transactionId)
                        sharedTransactionViewModel.actionState = ActionState.SUCCESS
                    }
                    is TransactionResult.Error -> {
                        val reason = (transactionResult as TransactionResult.Error).message
                        sharedTransactionViewModel.actionState = ActionState.FAILURE
                        sharedTransactionViewModel.failureReason = reason
                    }
                    else -> {
                        sharedTransactionViewModel.actionState = ActionState.FAILURE
                    }
                }
            }
            catch (e: Exception){
                e.printStackTrace()
                sharedTransactionViewModel.actionState = ActionState.FAILURE
            }
            finally {
                idempotencyKey = UUID.randomUUID().toString()
            }
        }
    }

    fun AddBeneficiary(addBeneficiaryFlowData: FlowData.AddBeneficiary){
        viewModelScope.launch {
            try {
              val result =  beneficiaryRepository.addBeneficiary(userId = addBeneficiaryFlowData.myUserId, beneficiaryUserId = addBeneficiaryFlowData.otherUserId,)
              if(result== (-1).toLong()){
                  sharedTransactionViewModel.actionState = ActionState.FAILURE
              }
              else{
                  sharedTransactionViewModel.actionState = ActionState.SUCCESS
              }
            }
            catch (_: Exception){
                sharedTransactionViewModel.actionState = ActionState.FAILURE
            }

        }

    }
}

