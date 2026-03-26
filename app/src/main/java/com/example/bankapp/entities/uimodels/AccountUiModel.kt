package com.example.bankapp.entities.uimodels

import com.example.bankapp.core.datecompatability.BankDateTime
import com.example.bankapp.entities.types.account.AccountType
import java.math.BigDecimal

data class AccountUiModel (
    val accNo: Long = 0,
    val userId: Long,
    val ifscCode: String="VANGI00001",
    val accountType: AccountType,
    val balance: BigDecimal = BigDecimal.ZERO,
    val createdAt: BankDateTime,
    var updatedAt: BankDateTime
)