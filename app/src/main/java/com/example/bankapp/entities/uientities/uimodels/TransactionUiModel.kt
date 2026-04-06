package com.example.bankapp.entities.uientities.uimodels

import com.example.bankapp.core.datecompatability.BankDateTime
import com.example.bankapp.entities.types.transaction.TransactionFailureType
import com.example.bankapp.entities.types.transaction.TransactionStatus
import com.example.bankapp.entities.types.transaction.TransactionType

data class TransactionUiModel (
    val transactionId: String,
    val referenceNumber: String,
    val transactionType: TransactionType,
    val transactionStatus: TransactionStatus,
    val idempotencyKey: String,
    val createdAt: BankDateTime,
    val updatedAt: BankDateTime,
    val failureType: TransactionFailureType? = null
)