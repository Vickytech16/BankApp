package com.example.bankapp.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.bankapp.entities.dbtables.Ledger
import java.math.BigDecimal

@Dao
interface LedgerDao {

    @Insert
    suspend fun insertAll(entries: List<Ledger>): List<Long>

    @Query("""
    SELECT SUM(le.amountInUsd) 
    FROM ledger_entries le
    INNER JOIN transactions t ON le.transactionId = t.transactionId
    WHERE le.accNo = :accNo 
      AND le.direction = 'DEBIT' 
      AND t.transactionStatus = 'COMPLETED'
      AND le.createdAt > :timestamp
""")
    suspend fun getTotalSpentInUsdSince(accNo: Long, timestamp: Long): BigDecimal?

    @Query("""
    SELECT COUNT(le.ledgerId) 
    FROM ledger_entries le
    INNER JOIN transactions t ON le.transactionId = t.transactionId
    WHERE le.accNo = :accNo 
      AND le.direction = 'DEBIT' 
      AND t.transactionStatus = 'COMPLETED'
      AND le.createdAt > :timestamp
""")
    suspend fun getTransactionCountSince(accNo: Long, timestamp: Long): Int
}