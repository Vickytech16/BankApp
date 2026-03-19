package com.example.bankapp.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.bankapp.entities.dbtables.Beneficiary
import kotlinx.coroutines.flow.Flow

@Dao
interface BeneficiaryDao {

    @Insert
    suspend fun addBeneficiary(beneficiary: Beneficiary): Long

    @Query("SELECT * FROM beneficiaries WHERE userId = :userId ORDER BY isFavorite DESC, addedDate DESC")
    fun getAllBeneficiaries(userId: Long): Flow<List<Beneficiary>>

    @Query("SELECT accounts.accNo FROM accounts WHERE accounts.userId = :beneficiaryUserId LIMIT 1")
    suspend fun getBeneficiaryAccountNo(beneficiaryUserId: Long): Long?

    @Query("SELECT * FROM beneficiaries WHERE userId = :userId AND beneficiaryUserId = :beneficiaryUserId LIMIT 1")
    suspend fun getBeneficiary(userId: Long, beneficiaryUserId: Long): Beneficiary?

    @Update
    suspend fun updateBeneficiary(beneficiary: Beneficiary)

    @Query("DELETE FROM beneficiaries WHERE userId = :userId AND beneficiaryUserId = :beneficiaryUserId")
    suspend fun removeBeneficiary(userId: Long, beneficiaryUserId: Long)
}