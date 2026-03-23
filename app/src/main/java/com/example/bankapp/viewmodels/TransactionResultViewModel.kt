package com.example.bankapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.entities.SessionState
import com.example.bankapp.entities.errors.TransactionResult
import com.example.bankapp.repositories.BeneficiaryRepository
import com.example.bankapp.repositories.TransactionRepository

import com.example.bankapp.di.HomeSessionHandlerProvider
import com.example.bankapp.usecases.ActionState
import com.example.bankapp.usecases.HomeSessionHandler
import kotlinx.coroutines.launch
import java.util.UUID

class TransactionResultViewModel(
    private val sessionState: SessionState,
    private val transactionRepository: TransactionRepository,
    private val beneficiaryRepository: BeneficiaryRepository
): ViewModel() {
    var transactionResult by mutableStateOf<TransactionResult?>(null)
        private set
    var actionExecuted by mutableStateOf(false)
    private var idempotencyKey by mutableStateOf(UUID.randomUUID().toString())

    fun cashTransfer(){

        val homeSessionHandler = HomeSessionHandlerProvider.currentHandler

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
                        cashTransfer.transactionId = (transactionResult as TransactionResult.Success).transactionId
                        cashTransfer.buildResultContent(true)
                        homeSessionHandler.onActionStateChange(ActionState.SUCCESS)
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

    fun Deposit(){

        val homeSessionHandler = HomeSessionHandlerProvider.currentHandler

        val deposit = HomeSessionHandlerProvider.currentHandler as HomeSessionHandler.Deposit

        viewModelScope.launch {
            try {

                if(deposit.userAccNo == 0.toLong() || deposit.userId == 0.toLong() ){
                    transactionResult = TransactionResult.Error.UnKnown
                    homeSessionHandler.onActionStateChange(ActionState.FAILURE)
                    return@launch
                }

                transactionResult = transactionRepository.deposit(
                    accountNo = deposit.userAccNo,
                    amount = deposit.amount,
                    idempotencyKey = idempotencyKey
                )

                when(transactionResult){
                    is TransactionResult.Success -> {
                        deposit.transactionId = (transactionResult as TransactionResult.Success).transactionId
                        deposit.buildResultContent(true)
                        homeSessionHandler.onActionStateChange(ActionState.SUCCESS)
                    }
                    is TransactionResult.Error -> {
                        deposit.buildResultContent(false)
                        homeSessionHandler.onActionStateChange(ActionState.FAILURE)
                    }
                    else -> {
                        homeSessionHandler.onActionStateChange(ActionState.FAILURE)
                    }
                }
            }
            catch (_: Exception){
                homeSessionHandler.onActionStateChange(ActionState.FAILURE)
            }
            finally {
                idempotencyKey = UUID.randomUUID().toString()
            }
        }
    }

    fun AddBeneficiary(){

        val homeSessionHandler = HomeSessionHandlerProvider.currentHandler
        val addBeneficiary = homeSessionHandler as HomeSessionHandler.AddBeneficiary

        viewModelScope.launch {
            try {
              val result =  beneficiaryRepository.addBeneficiary(userId = addBeneficiary.currentUserId, beneficiaryUserId = addBeneficiary.otherUserId,)
              if(result== (-1).toLong()){
                  addBeneficiary.buildContent(false)
                  homeSessionHandler.onActionStateChange(ActionState.FAILURE)
              }
              else{
                  addBeneficiary.buildContent(true)
                  homeSessionHandler.onActionStateChange(ActionState.SUCCESS)
              }
            }
            catch (_: Exception){
                addBeneficiary.buildContent(false)
                homeSessionHandler.onActionStateChange(ActionState.FAILURE)
            }
            finally {

            }
        }

    }
}

