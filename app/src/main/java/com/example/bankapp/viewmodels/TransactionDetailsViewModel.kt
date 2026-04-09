package com.example.bankapp.viewmodels

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.entities.dtos.TransactionHistoryItemDto
import com.example.bankapp.repositories.TransactionRepository
import com.example.bankapp.services.TransactionExportService
import com.example.bankapp.services.TransactionReceiptPainter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TransactionDetailsViewModel(
    private val transactionRepository: TransactionRepository,
    private val transactionExportService: TransactionExportService
) : ViewModel() {

    var previewBitmap by mutableStateOf<Bitmap?>(null)
        private set

    var showSharePreview by mutableStateOf(false)
        private set

    var showEnlargeImage by mutableStateOf(false)

    private var _transaction = MutableStateFlow<TransactionHistoryItemDto?>(null)
    val transaction = _transaction.asStateFlow()


    fun loadTransaction(transactionId: String, accNo: Long) {
        viewModelScope.launch {
            _transaction.value = transactionRepository.getTransactionHistoryItemByTransactionId(transactionId, accNo)
        }
    }


    fun generateReceipt(
        painter: TransactionReceiptPainter,
        item: TransactionHistoryItemDto,
        countryCode: String,
        timezone: String
    ) {
        viewModelScope.launch {
            val bitmap = painter.draw(item, countryCode, timezone)
            previewBitmap = bitmap
            showSharePreview = true
        }
    }


    fun dismissPreview() {
        showSharePreview = false
        previewBitmap = null
    }


    fun confirmShare() {
        val bitmap = previewBitmap ?: return
        viewModelScope.launch {
            transactionExportService.shareBitmap(bitmap)
            dismissPreview()
        }
    }
}