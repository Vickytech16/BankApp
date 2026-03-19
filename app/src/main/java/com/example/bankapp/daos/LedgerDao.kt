package com.example.bankapp.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.bankapp.entities.dbtables.Ledger

@Dao
interface LedgerDao {
    @Insert
    suspend fun insertAll(entries: List<Ledger>): List<Long>

    @Query("select * from ledger_entries WHERE accNo = :accNo ORDER BY ledgerId DESC")
    suspend fun getLedgersForAccount(accNo: Long): List<Ledger>

    @Query("SELECT * FROM ledger_entries WHERE transactionId = :transactionId")
    suspend fun getByTransactionId(transactionId: String): List<Ledger>
}