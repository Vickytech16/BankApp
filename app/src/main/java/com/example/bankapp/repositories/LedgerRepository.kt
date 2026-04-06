package com.example.bankapp.repositories

import com.example.bankapp.daos.LedgerDao
import com.example.bankapp.entities.dbtables.Ledger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.math.BigDecimal


class LedgerRepository(private val ledgerDao: LedgerDao) {

    suspend fun insertAll(entries: List<Ledger>): List<Long>{
        return withContext(Dispatchers.IO){
            ledgerDao.insertAll(entries)
        }
    }

    suspend fun getTotalSpentInUsdSince(accNo: Long, timestamp: Long): BigDecimal? {
        return withContext(Dispatchers.IO){
            ledgerDao.getTotalSpentInUsdSince(accNo, timestamp)
        }
    }

    suspend fun getTransactionCountSince(accNo: Long, timestamp: Long): Int {
        return withContext(Dispatchers.IO) {
            ledgerDao.getTransactionCountSince(accNo, timestamp)
        }
    }
}