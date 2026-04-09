package com.example.bankapp.repositories

import com.example.bankapp.daos.LedgerDao
import com.example.bankapp.entities.dbtables.Ledger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.math.BigDecimal


class LedgerRepository(private val ledgerDao: LedgerDao) {

    suspend fun insertAll(entries: List<Ledger>): List<Long>{
        return withContext(Dispatchers.IO){
            ledgerDao.insertAll(entries)
        }
    }

    fun getTotalSpentInUsdSince(accNo: Long, timestamp: Long): Flow<BigDecimal?> {
        return ledgerDao.getTotalSpentInUsdSince(accNo, timestamp)
    }

    fun getTransactionCountSince(accNo: Long, timestamp: Long): Flow<Int> {
           return ledgerDao.getTransactionCountSince(accNo, timestamp)
    }
}