package com.example.bankapp.repositories

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.bankapp.daos.AccountDao
import com.example.bankapp.entities.Account

import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    suspend fun getAccountByUserId(userId: Long):List<Account>

    suspend fun getAccountByAccountNumber(accNo: Long): Account?

    fun getAccountFlowByAccountNumber(accNo: Long): Flow<Account?>

    suspend fun createAccount(account: Account) : Long
}

class AccountRepositoryImpl(
    private val accountDao: AccountDao
): AccountRepository {
    override suspend fun getAccountByUserId(userId:Long): List<Account> {
        return  accountDao.getAccountByUserId(userId)
    }

    override suspend fun getAccountByAccountNumber(accNo: Long): Account? {
        return accountDao.getAccountByAccNo(accNo)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun createAccount(account: Account): Long {
        return accountDao.createAccount(account)
    }

    override fun getAccountFlowByAccountNumber(accNo: Long): Flow<Account?> {
        return accountDao.getAccountFlowByAccNo(accNo)
    }
}