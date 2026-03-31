package com.example.bankapp.entities.dtos

import com.example.bankapp.core.datecompatability.BankDateFactory

data class TransactionExportDto(
    val date: String,
    val description: String,
    val type: String,
    val direction: String,
    val amount: String,
    val balanceAfter: String,
    val status: String,
    val reference: String
)

data class ExportMetadata(
    val userName: String,
    val accountNo: String,
    val dateGenerated: String = BankDateFactory.now().toFullDisplay()
)

