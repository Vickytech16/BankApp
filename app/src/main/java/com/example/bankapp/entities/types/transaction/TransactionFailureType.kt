package com.example.bankapp.entities.types.transaction

enum class TransactionFailureType {

    ACCOUNT_NOT_FOUND,
    INVALID_AMOUNT,
    INSUFFICIENT_BALANCE,
    UNKNOWN_ERROR
}