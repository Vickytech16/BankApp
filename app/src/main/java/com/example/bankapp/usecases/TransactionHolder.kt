package com.example.bankapp.usecases

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.bankapp.entities.dbtables.User
import com.example.bankapp.entities.types.transaction.TransactionType
import java.math.BigDecimal



enum class CurrentTransactionStatus {
    IDLE,
    LOADING,
    SUCCESS,
    FAILURE
}

enum class CurrentSessionIntent {
    DEPOSIT,
    CASH_TRANSFER,
    SCHEDULED_TRANSFER,
    BENEFICIARY_ADDITION
}


class TransactionSessionHolder {
    var currentTransactionType: CurrentSessionIntent? = null
    var currentTransactionStatus by mutableStateOf(
        CurrentTransactionStatus.IDLE
    )

    var transactionId by mutableStateOf<String?>(null)

    var accNo: String = ""
    var toAccNo: String? = null
    var amount: BigDecimal = BigDecimal.ZERO
    var isPasswordNeeded: Boolean = false
    var isOtpVerified: Boolean = false

    var onOtpSuccess: (() -> Unit)? = null

    fun onTransactionCompletion() {

    }

    var onPasswordVerification: (() -> Unit)? = null

    var onPasswordVerificationNavigation: (() -> Unit)? = null

    fun onCurrentTransactionStatusChange(
        newValue: CurrentTransactionStatus,
        transactionId: String? = null
    ) {
        currentTransactionStatus = newValue
        this.transactionId = transactionId
    }

    fun onTransactionTypeChange(newValue: CurrentSessionIntent) {
        currentTransactionType = newValue
    }

    var successMessage: String = ""
    var failureMessage: String = ""
    var onSuccessAction: (() -> Unit)? = null
    var onFailureRetry: (() -> Unit)? = null

    var currentUser: User? = null

    var friend: User? = null

    fun reset() {
        currentTransactionType = null
        currentTransactionStatus = CurrentTransactionStatus.IDLE
        accNo = ""
        amount = BigDecimal.ZERO
        isPasswordNeeded = false
        isOtpVerified = false
        toAccNo = null
        transactionId = null
        onSuccessAction = null
        onFailureRetry = null
        successMessage = ""
        failureMessage = ""
        currentUser = null
        friend = null

    }

    fun onOtpSuccessChange(toDo: () -> Unit) {
        onOtpSuccess = toDo
    }

    fun onCashTransferVerification(
        accNo: String,
        amount: BigDecimal,
        toAccNo: String? = null
    ) {
        this.accNo = accNo
        this.amount = amount
        this.toAccNo = toAccNo
    }

}

