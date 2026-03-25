package com.example.bankapp.entities.dbtables

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.bankapp.entities.types.transaction.TransactionFailureType
import com.example.bankapp.entities.types.transaction.TransactionStatus
import com.example.bankapp.entities.types.transaction.TransactionType
import java.time.LocalDateTime

@Entity(tableName = "transactions",
    indices = [
        Index(value = ["referenceNumber"], unique = true),
        Index(value = ["idempotencyKey"], unique = true)
    ]
)
data class Transaction(
    @PrimaryKey
    val transactionId: String,
    val referenceNumber: String,
    val transactionType: TransactionType,
    val transactionStatus: TransactionStatus,
    val idempotencyKey: String,
    val createdAt: Long,
    val updatedAt: Long,
    val failureType: TransactionFailureType? = null
)