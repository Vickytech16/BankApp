package com.example.bankapp.entities.dtos

import androidx.room.ColumnInfo
import com.example.bankapp.core.datecompatability.BankDateFactory
import com.example.bankapp.core.datecompatability.BankDateTime
import com.example.bankapp.entities.types.transaction.TransactionFailureType
import com.example.bankapp.entities.types.transaction.TransactionStatus
import com.example.bankapp.entities.types.transaction.TransactionType
import com.example.bankapp.entities.types.transaction.LedgerDirection

data class TransactionHistoryItemDto(

    @ColumnInfo(name = "transaction_type")
    val transactionType: TransactionType,

    @ColumnInfo(name = "transaction_date")
    val transactionDate: BankDateTime,

    val amount: String,

    @ColumnInfo(name = "counterparty_nickname")
    val counterpartyNickname: String? = null,

    @ColumnInfo(name = "counterparty_name")
    val counterpartyName: String?,

    @ColumnInfo(name = "counterparty_pfp")
    val counterpartyPfpUrl: String?,

    @ColumnInfo(name = "transaction_id")
    val transactionId: String = "",

    @ColumnInfo(name = "reference_number")
    val referenceNumber: String = "",

    @ColumnInfo(name = "transaction_status")
    val transactionStatus: TransactionStatus? = null,

    @ColumnInfo(name = "updated_at")
    val updatedAt: BankDateTime = transactionDate,

    @ColumnInfo(name = "failure_type")
    val failureType: TransactionFailureType? = null,

    @ColumnInfo(name = "ledger_direction")
    val ledgerDirection: LedgerDirection? = null,

    @ColumnInfo(name = "balance_after")
    val balanceAfter: String = "",

    @ColumnInfo(name = "my_account_no")
    val myAccountNo: Long = 0,

    @ColumnInfo(name = "my_ifsc_code")
    val myIfscCode: String = "",

    @ColumnInfo(name = "my_user_name")
    val myUserName: String = "",

    @ColumnInfo(name = "my_pfp_url")
    val myPfpUrl: String? = null,

    @ColumnInfo(name = "counterparty_account_no")
    val counterpartyAccountNo: Long? = null,

    @ColumnInfo(name = "counterparty_ifsc_code")
    val counterpartyIfscCode: String? = null,
)