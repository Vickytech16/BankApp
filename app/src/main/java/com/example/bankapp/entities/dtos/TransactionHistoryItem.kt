package com.example.bankapp.entities.dtos

data class TransactionHistoryItemDto(
    val transaction_type: String,
    val transaction_date: String,
    val direction: String,
    val amount: Double,
    val counterparty_name: String?,
    val counterparty_pfp: String?
)
