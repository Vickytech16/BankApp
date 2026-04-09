package com.example.bankapp.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.bankapp.entities.dbtables.Ledger
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

@Dao
interface LedgerDao {

    @Insert
    suspend fun insertAll(entries: List<Ledger>): List<Long>

    @Query("""
    SELECT COALESCE(SUM(le.amountInUsd), 0) 
    FROM ledger_entries le
    INNER JOIN transactions t ON le.transactionId = t.transactionId
    WHERE le.accNo = :accNo 
      AND le.direction = 'DEBIT' 
      AND t.transactionStatus = 'COMPLETED'
      AND le.createdAt >= :timestamp
""")
    fun getTotalSpentInUsdSince(accNo: Long, timestamp: Long): Flow<BigDecimal>
    @Query("""
    SELECT COUNT(le.ledgerId) 
    FROM ledger_entries le
    INNER JOIN transactions t ON le.transactionId = t.transactionId
    WHERE le.accNo = :accNo 
      AND le.direction = 'DEBIT' 
      AND t.transactionStatus = 'COMPLETED'
      AND le.createdAt > :timestamp
""")
    fun getTransactionCountSince(accNo: Long, timestamp: Long): Flow<Int>
}