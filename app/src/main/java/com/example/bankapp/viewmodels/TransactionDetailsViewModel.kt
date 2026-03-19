package com.example.bankapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.entities.dtos.TransactionHistoryItemDto
import com.example.bankapp.repositories.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TransactionDetailsViewModel(
   private val transactionRepository: TransactionRepository
): ViewModel() {

    private var _transaction = MutableStateFlow<TransactionHistoryItemDto?>(null)
    val transaction =_transaction.asStateFlow()

    fun loadTransaction(transactionId: String) {
        viewModelScope.launch {
            _transaction.value  = transactionRepository.getTransactionHistoryByTransactionId(transactionId)
        }
    }
}