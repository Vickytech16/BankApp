package com.example.bankapp.entities.uimodels

import com.example.bankapp.entities.types.transaction.LedgerDirection
import java.math.BigDecimal

data class Ledger(
    val ledgerId: Long = 0,
    val transactionId: String,
    val accNo: Long,
    val direction: LedgerDirection,
    val amount: BigDecimal,
    val balanceAfter: BigDecimal
)
