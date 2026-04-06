package com.example.bankapp.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.entities.dtos.TransactionHistoryItemDto
import com.example.bankapp.repositories.TransactionRepository
import com.example.bankapp.services.TransactionExportService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TransactionDetailsViewModel(
   private val transactionRepository: TransactionRepository,
    private val transactionExportService: TransactionExportService

): ViewModel() {

    private var _transaction = MutableStateFlow<TransactionHistoryItemDto?>(null)
    val transaction =_transaction.asStateFlow()

    fun loadTransaction(transactionId: String, accNo: Long) {
        viewModelScope.launch {

            _transaction.value  = transactionRepository.getTransactionHistoryItemByTransactionId(transactionId, accNo)
        }
    }

    fun shareAction(bitmap: Bitmap){
        viewModelScope.launch {
            transactionExportService.shareBitmap(bitmap)
        }
    }
}