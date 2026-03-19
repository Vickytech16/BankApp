package com.example.bankapp.entities.dbtables

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.bankapp.entities.types.TransactionFailureType
import com.example.bankapp.entities.types.TransactionStatus
import com.example.bankapp.entities.types.TransactionType
import java.time.LocalDateTime

@Entity(tableName = "transactions",
    indices = [
        Index(value = ["referenceNumber"], unique = true),
        Index(value = ["idempotencyKey"], unique = true)
    ]
)
data class Transaction @RequiresApi(Build.VERSION_CODES.O) constructor(
    @PrimaryKey
    val transactionId: String,
    val referenceNumber: String,
    val transactionType: TransactionType,
    val transactionStatus: TransactionStatus,
    val idempotencyKey: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val failureType: TransactionFailureType? = null
    )