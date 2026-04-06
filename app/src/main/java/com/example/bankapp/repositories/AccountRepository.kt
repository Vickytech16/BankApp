package com.example.bankapp.repositories

import com.example.bankapp.daos.AccountDao
import com.example.bankapp.entities.dbtables.Account
import com.example.bankapp.entities.uientities.uimodels.AccountUiModel
import com.example.bankapp.utilities.mappers.toDbModel
import com.example.bankapp.utilities.mappers.toUiModel
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.math.BigDecimal

interface AccountRepository {
    suspend fun getAccountByUserId(userId: Long) : List<AccountUiModel>

    fun getAccountAsFlowByAccNo(accNo: Long) : Flow<Account?>

    suspend fun createAccount(account: AccountUiModel) : Long

    suspend fun getUserIdByAccNo(accNo: Long) : Long

    fun getAccountAsFlowByUserId(userId: Long) : Flow<AccountUiModel?>

    suspend fun getAllSavingsAccounts(): List<Account>

    suspend fun updateBalanceAndInterestDate(accNo: Long, newBalance: BigDecimal, newInterestDate: Long, updateAt: Long) : Int

    suspend fun withdraw(amount: BigDecimal, updateAt: Long, accNo: Long) : Int

    suspend fun deposit(amount: BigDecimal, updateAt: Long, accNo: Long) : Int
}

class AccountRepositoryImpl(
    private val accountDao: AccountDao
): AccountRepository {

    override suspend fun getAccountByUserId(userId:Long): List<AccountUiModel> {
        return withContext(Dispatchers.IO) {
            accountDao.getAccountByUserId(userId).map { it.toUiModel() }
        }
    }

    override suspend fun createAccount(account: AccountUiModel): Long {
        return withContext(Dispatchers.IO){
        accountDao.createAccount(account.toDbModel())
            }
    }

    override fun getAccountAsFlowByAccNo(accNo: Long): Flow<Account?> {
        return accountDao.getAccountAsFlowByAccNo(accNo)
    }

    override suspend fun getUserIdByAccNo(accNo: Long): Long {
        return withContext(Dispatchers.IO){
            accountDao.getUserIdByAccNo(accNo)
        }
    }

    override fun getAccountAsFlowByUserId(userId: Long): Flow<AccountUiModel?> {
          return  accountDao.getAccountAsFlowByUserId(userId).map { it?.toUiModel() }
    }

    override suspend fun getAllSavingsAccounts(): List<Account> {
        return accountDao.getAllSavingsAccounts()
    }

    override suspend fun updateBalanceAndInterestDate(accNo: Long, newBalance: BigDecimal, newInterestDate: Long, updateAt: Long): Int {
       return accountDao.updateBalanceAndInterestDate(accNo, newBalance, newInterestDate, updateAt)
    }

    override suspend fun deposit(amount: BigDecimal, updateAt: Long, accNo: Long): Int {
        return accountDao.deposit(amount, updateAt, accNo)
    }

    override suspend fun withdraw(amount: BigDecimal, updateAt: Long, accNo: Long): Int {
        return accountDao.withdraw(amount, updateAt, accNo)
    }
}