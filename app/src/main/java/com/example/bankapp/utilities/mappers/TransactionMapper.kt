package com.example.bankapp.utilities.mappers

import com.example.bankapp.core.datecompatability.BankDateFactory
import com.example.bankapp.entities.dbtables.Transaction
import com.example.bankapp.entities.uimodels.TransactionUiModel

fun Transaction.toUiModel(): TransactionUiModel {
    return TransactionUiModel(
        transactionId = this.transactionId,
        referenceNumber = this.referenceNumber,
        transactionType = this.transactionType,
        transactionStatus = this.transactionStatus,
        idempotencyKey = this.idempotencyKey,
        createdAt = BankDateFactory.fromMillis(this.createdAt),
        updatedAt = BankDateFactory.fromMillis(this.updatedAt),
        failureType = this.failureType
    )
}

fun TransactionUiModel.toEntity(): Transaction {
    return Transaction(
        transactionId = this.transactionId,
        referenceNumber = this.referenceNumber,
        transactionType = this.transactionType,
        transactionStatus = this.transactionStatus,
        idempotencyKey = this.idempotencyKey,
        createdAt = this.createdAt.epochMillis,
        updatedAt = this.updatedAt.epochMillis,
        failureType = this.failureType
    )
}