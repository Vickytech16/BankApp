package com.example.bankapp.entities.errors

import com.example.bankapp.R

sealed class TransactionResult(val message: Int) {
    data class Success(val transactionId: String) : TransactionResult(R.string.transaction_success)
    sealed class Error(val errorMessage: Int) : TransactionResult(message = errorMessage) {
        object AccountNotFound : Error(R.string.account_not_found_error)
        object InsufficientBalance : Error(R.string.insufficient_balance_error)
        object SameAccountTransfer : Error(R.string.same_account_transfer_error)
        object InvalidAmount : Error(R.string.invalid_amount_error)
        object RepeatedTransaction : Error(R.string.repeated_transaction)
        object unKnown : Error(R.string.generic_transaction_error)
    }
}