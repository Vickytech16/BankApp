package com.example.bankapp.entities.errors

import com.example.bankapp.R
import java.math.BigDecimal

sealed class TransactionResult(val message: Int) {

    data class Success(val transactionId: String) : TransactionResult(R.string.transaction_success)

    sealed class Error(errorMessage: Int) : TransactionResult(message = errorMessage) {

        object AccountNotFound : Error(R.string.account_not_found_error)

        object InsufficientBalance : Error(R.string.insufficient_balance_error)

        object SameAccountTransfer : Error(R.string.same_account_transfer_error)

        object InvalidAmount : Error(R.string.invalid_amount_error)

        object RepeatedTransaction : Error(R.string.repeated_transaction)

        object UnKnown : Error(R.string.generic_transaction_error)

        object ExchangeRatesNotFound: Error(R.string.exchange_rates_not_found)

        sealed class LimitExceeded(msg: Int) : Error(msg) {
            data class DailyLimitExceeded(val limit: BigDecimal) : LimitExceeded(R.string.daily_limit_exceeded)
            data class DailyCountExceeded(val maxCount: Int) : LimitExceeded(R.string.daily_count_exceeded)
            data class SingleTransactionLimitExceeded(val maxAmount: BigDecimal) : LimitExceeded(R.string.single_tx_limit_exceeded)
        }

    }
}