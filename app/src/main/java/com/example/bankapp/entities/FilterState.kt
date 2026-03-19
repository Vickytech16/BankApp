package com.example.bankapp.entities

import com.example.bankapp.entities.types.LedgerDirection
import com.example.bankapp.entities.types.SortOptions
import com.example.bankapp.entities.types.TransactionStatus
import com.example.bankapp.entities.types.TransactionType
import com.example.bankapp.entities.types.UiLedgerDirection


data class FilterState(
    val selectedStatus: Set<TransactionStatus> = emptySet(),
    val selectedTypes: Set<TransactionType> = emptySet(),
    val selectedDirection: UiLedgerDirection = UiLedgerDirection.BOTH,
)


