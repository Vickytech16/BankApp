package com.example.bankapp.entities.types.account

import com.example.bankapp.R
import com.example.bankapp.entities.types.transaction.TransactionType
import java.math.BigDecimal

sealed class AccountType(val code: String) {
    abstract val dailyTransactionLimit: BigDecimal
    abstract val dailyTransactionCount: Int

    abstract val maxBalanceLimit: BigDecimal

    abstract val nameRes: Int

    abstract fun getMaxPerTransaction(transactionType: TransactionType): BigDecimal

    data object Savings : AccountType("SAVINGS") {
        override val dailyTransactionLimit = BigDecimal("5000.00")
        override val dailyTransactionCount = 10
        override val maxBalanceLimit = BigDecimal("50000.00")

        override fun getMaxPerTransaction(transactionType: TransactionType): BigDecimal {
            return when (transactionType) {
                TransactionType.CASH_TRANSFER -> BigDecimal("2500.00")
                TransactionType.INTERNATIONAL_TRANSFER -> BigDecimal("1000.00")
                TransactionType.DEPOSIT -> maxBalanceLimit
                else -> BigDecimal.valueOf(Long.MAX_VALUE)
            }
        }
        override val nameRes: Int = R.string.savings_account
    }

    data object Current : AccountType("CURRENT") {
        override val dailyTransactionLimit = BigDecimal("25000.00")
        override val dailyTransactionCount = 50
        override val maxBalanceLimit = BigDecimal("50000.00")

        override fun getMaxPerTransaction(transactionType: TransactionType): BigDecimal {
            return when (transactionType) {
                TransactionType.CASH_TRANSFER -> BigDecimal("12500.00")
                TransactionType.INTERNATIONAL_TRANSFER -> BigDecimal("5000.00")
                TransactionType.DEPOSIT -> maxBalanceLimit
                else -> BigDecimal.valueOf(Long.MAX_VALUE)
            }
        }
        override val nameRes: Int = R.string.current_account
    }

}