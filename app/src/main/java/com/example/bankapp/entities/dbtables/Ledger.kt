package com.example.bankapp.entities.dbtables

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.bankapp.core.datecompatability.BankDateFactory
import com.example.bankapp.entities.types.transaction.LedgerDirection
import java.math.BigDecimal

@Entity(tableName = "ledger_entries",
    foreignKeys = [
        ForeignKey(
            entity = Transaction::class,
            parentColumns = ["transactionId"],
            childColumns = ["transactionId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = Account::class,
            parentColumns = ["accNo"],
            childColumns = ["accNo"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index("transactionId"),
        Index("accNo")
    ]
)
data class Ledger(
    @PrimaryKey(autoGenerate = true)
    val ledgerId: Long = 0,
    val transactionId: String,
    val accNo: Long,
    val direction: LedgerDirection,
    val amount: BigDecimal,
    val balanceAfter: BigDecimal,
    val amountInUsd: BigDecimal,
    val createdAt: Long = BankDateFactory.now().epochMillis
)