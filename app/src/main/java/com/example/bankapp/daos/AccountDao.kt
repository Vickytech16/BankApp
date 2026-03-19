package com.example.bankapp.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.bankapp.entities.dbtables.Account
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.time.LocalDateTime

@Dao
interface AccountDao {

    @Query("select * from accounts where userId=:userId")
    suspend fun getAccountByUserId(userId: Long):List<Account>

    @Insert
    suspend fun createAccount(account: Account) : Long

    @Query("update accounts set balance = balance - :amount, updatedAt = :updateAt where accNo =:accNo")
    suspend fun withdraw(amount: BigDecimal, updateAt: LocalDateTime, accNo: Long) : Int

    @Query("update accounts set balance = balance + :amount, updatedAt = :updateAt where accNo =:accNo")
    suspend fun deposit(amount: BigDecimal, updateAt: LocalDateTime, accNo: Long) : Int

    @Query("select * from accounts where accNo = :accNo")
    fun getAccountAsFlowByAccNo(accNo: Long): Flow<Account?>

    @Query("select userId from accounts where accNo = :accNo")
    fun getUserIdByAccNo(accNo: Long): Long
}