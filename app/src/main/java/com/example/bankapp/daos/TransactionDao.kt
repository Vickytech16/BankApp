package com.example.bankapp.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.bankapp.entities.Transaction
import com.example.bankapp.entities.dtos.TransactionHistoryItemDto
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(transaction: Transaction): Long

    @Query("select * from transactions where transactionId = :transactionId")
    suspend fun getTransactionById(transactionId: String): Transaction?

    @Update
    suspend fun update(transaction: Transaction)

    @Query("select * from transactions where idempotencyKey = :idempotencyKey")
    suspend fun getTransactionByIdempotencyKey(idempotencyKey: String): Transaction?

    @Query("""
        SELECT
    t.transactionType AS transaction_type,
    t.createdAt AS transaction_date,

    te.direction,
    te.amount,

    COALESCE(u.username, 'Bank') AS counterparty_name,
    u.pfpURL AS counterparty_pfp

FROM ledger_entries te

JOIN transactions t
    ON te.transactionId= t.transactionId

LEFT JOIN ledger_entries te2
    ON te.transactionId = te2.transactionId
    AND te.accNo <> te2.accNo

LEFT JOIN accounts a2
    ON te2.accNo = a2.accNo

LEFT JOIN users u
    ON a2.userId= u.userId

WHERE te.accNo = :accNo
ORDER BY t.createdAt DESC;
    """)
    fun getAllTransactionsForAccount(accNo: Long): Flow<List<TransactionHistoryItemDto>>
}