package com.example.bankapp.entities

import java.math.BigDecimal

sealed interface AuthorizationIntent {
    data class CashTransfer(
        val fromAccNo: Long,
        val toAccNo: Long,
        val amount: BigDecimal,
        val isFriend: Boolean,
        val transactionId: String? = null,
        val countryCode: String
    ) : AuthorizationIntent

    data class Deposit(
        val userAccNo: Long,
        val amount: BigDecimal,
        val transactionId: String? = null,
        val countryCode: String
    ) : AuthorizationIntent

    data class AddBeneficiary(
        val myUserId: Long,
        val otherUserId: Long,
        val nickname: String
    ) : AuthorizationIntent

    data class InternationalTransfer(
        val fromAccNo: Long,
        val toAccNo: Long,
        val isFriend: Boolean,
        val baseAmount: BigDecimal,
        val baseCurrency: String,
        val targetCurrency: String,
        val exchangeRate: BigDecimal,
        val transactionId: String? = null
    ): AuthorizationIntent
}

enum class AuthorizationctionState {
    LOADING,
    SUCCESS,
    FAILURE
}