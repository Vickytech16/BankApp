package com.example.bankapp.entities.types.ui

import com.example.bankapp.entities.types.transaction.TransactionStatus
import com.example.bankapp.entities.types.transaction.TransactionType

data class FilterState(
    val selectedStatus: Set<TransactionStatus> = emptySet(),
    val selectedTypes: Set<TransactionType> = emptySet(),
    val selectedDirection: UiLedgerDirection = UiLedgerDirection.BOTH,
)