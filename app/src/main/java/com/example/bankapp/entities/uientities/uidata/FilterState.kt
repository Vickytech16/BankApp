package com.example.bankapp.entities.uientities.uidata

import com.example.bankapp.entities.types.transaction.TransactionStatus
import com.example.bankapp.entities.types.transaction.TransactionType
import com.example.bankapp.entities.uientities.uitypes.UiLedgerDirection

data class FilterState(
    val selectedStatus: Set<TransactionStatus> = emptySet(),
    val selectedTypes: Set<TransactionType> = emptySet(),
    val selectedDirection: UiLedgerDirection = UiLedgerDirection.BOTH,
    val startDate: Long? = null, // New
    val endDate: Long? = null
){
    val isDateFilterActive: Boolean get() = startDate != null || endDate != null
}