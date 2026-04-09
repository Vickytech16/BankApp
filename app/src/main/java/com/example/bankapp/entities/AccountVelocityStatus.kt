package com.example.bankapp.entities

import com.example.bankapp.entities.types.account.AccountType
import java.math.BigDecimal

data class AccountVelocityStatus(
    val moneySpentToday: BigDecimal,
    val dailySpendLimit: BigDecimal,
    val transactionsToday: Int,
    val maxTransactions: Int,
    val accountType: AccountType,
    val nextResetMillis: Long
) {
    val dailyLimitProgress: Float
        get() = if (dailySpendLimit > BigDecimal.ZERO) {
            moneySpentToday.divide(dailySpendLimit, 4, java.math.RoundingMode.HALF_UP).toFloat()
        } else 0f

    val countProgress: Float
        get() = if (maxTransactions > 0) {
            transactionsToday.toFloat() / maxTransactions.toFloat()
        } else 0f
}
