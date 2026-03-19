package com.example.bankapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.entities.SessionState
import com.example.bankapp.entities.errors.TransactionResult
import com.example.bankapp.repositories.TransactionRepository

import com.example.bankapp.ui.components.navigators.TransactionSessionManager
import com.example.bankapp.usecases.ActionState
import com.example.bankapp.usecases.CurrentSessionIntent
import com.example.bankapp.usecases.CurrentTransactionStatus
import com.example.bankapp.usecases.HomeSessionHandler
import kotlinx.coroutines.launch
import java.util.UUID

class TransactionResultViewModel(
    private val sessionState: SessionState,
    private val transactionRepository: TransactionRepository
): ViewModel() {
    var transactionResult by mutableStateOf<TransactionResult?>(null)
        private set

    var actionExecuted by mutableStateOf(false)


    private var idempotencyKey by mutableStateOf(UUID.randomUUID().toString())

    fun cashTransfer(){

        val homeSessionHandler = TransactionSessionManager.currentHandler

        val cashTransfer = homeSessionHandler as HomeSessionHandler.CashTransfer

        viewModelScope.launch {
            try{

                if(cashTransfer.toAccNo==0.toLong()){
                    transactionResult = TransactionResult.Error.UnKnown
                    homeSessionHandler.onActionStateChange(ActionState.FAILURE)
                    return@launch
                }

                transactionResult = transactionRepository.cashTransfer(
                    cashTransfer.fromAccNo,
                    cashTransfer.toAccNo,
                    cashTransfer.amount,
                    idempotencyKey
                )

                when (transactionResult) {
                    is TransactionResult.Success -> {
                        cashTransfer.buildResultContent(true)
                        homeSessionHandler.onActionStateChange(ActionState.SUCCESS)
                        cashTransfer.transactionId = (transactionResult as TransactionResult.Success).transactionId
                    }

                    is TransactionResult.Error -> {
                        val reason = (transactionResult as TransactionResult.Error).toString()
                        cashTransfer.buildResultContent(
                            isSuccess = false,
                            reason = reason
                        )
                        homeSessionHandler.onActionStateChange(ActionState.FAILURE)
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
}

