package com.example.bankapp.repositories

import com.example.bankapp.daos.AccountDao
import com.example.bankapp.entities.uimodels.AccountUiModel
import com.example.bankapp.utilities.mappers.toDbModel
import com.example.bankapp.utilities.mappers.toUiModel
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

interface AccountRepository {
    suspend fun getAccountByUserId(userId: Long):List<AccountUiModel>

    fun getAccountAsFlowByAccountNumber(accNo: Long): Flow<AccountUiModel?>

    suspend fun createAccount(account: AccountUiModel) : Long

    suspend fun getUserIdByAccNo(accNo: Long) : Long
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

    override fun getAccountAsFlowByAccountNumber(accNo: Long): Flow<AccountUiModel?> {
        return accountDao.getAccountAsFlowByAccNo(accNo).map { it?.toUiModel() }
    }

    override suspend fun getUserIdByAccNo(accNo: Long): Long {
        return withContext(Dispatchers.IO){
            accountDao.getUserIdByAccNo(accNo)
        }
    }

}