package com.example.bankapp.repositories

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.bankapp.daos.AccountDao
import com.example.bankapp.entities.dbtables.Account
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

interface AccountRepository {
    suspend fun getAccountByUserId(userId: Long):List<Account>


    fun getAccountAsFlowByAccountNumber(accNo: Long): Flow<Account?>

    suspend fun createAccount(account: Account) : Long

    suspend fun getUserIdByAccNo(accNo: Long) : Long
}

class AccountRepositoryImpl(
    private val accountDao: AccountDao
): AccountRepository {
    override suspend fun getAccountByUserId(userId:Long): List<Account> {
        return withContext(Dispatchers.IO) {
            accountDao.getAccountByUserId(userId)
        }
    }

    override suspend fun createAccount(account: Account): Long {
        return withContext(Dispatchers.IO){
        accountDao.createAccount(account)
            }
    }

    override fun getAccountAsFlowByAccountNumber(accNo: Long): Flow<Account?> {
        return accountDao.getAccountAsFlowByAccNo(accNo)
    }

    override suspend fun getUserIdByAccNo(accNo: Long): Long {
        return withContext(Dispatchers.IO){
            accountDao.getUserIdByAccNo(accNo)
        }
    }
}