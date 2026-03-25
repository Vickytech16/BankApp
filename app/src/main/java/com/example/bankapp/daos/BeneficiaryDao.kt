package com.example.bankapp.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.bankapp.entities.dbtables.Beneficiary
import com.example.bankapp.entities.dtos.BeneficiaryDto
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

    @Query("""
    SELECT 
        b.beneficiaryId,
        b.userId,
        CASE 
            WHEN b.nickname != '' THEN b.nickname
            ELSE u.userName
        END as friendName,
        b.beneficiaryUserId as friendUserId,
        u.pfpURL as friendPfp,
        a.accNo as friendPrimaryAccNo
    FROM beneficiaries b
    INNER JOIN users u ON b.beneficiaryUserId = u.userId
    INNER JOIN accounts a ON u.userId = a.userId
    WHERE b.userId = :userId
    ORDER BY b.addedDate DESC
""")
    suspend fun getAllFriends(userId: Long): List<BeneficiaryDto>

    @Query("""
    SELECT 
        b.beneficiaryId,
        b.userId,
        CASE 
            WHEN b.nickname != '' THEN b.nickname
            ELSE u.userName
        END as friendName,
        b.beneficiaryUserId as friendUserId,
        u.pfpURL as friendPfp,
        a.accNo as friendPrimaryAccNo
    FROM beneficiaries b
    INNER JOIN users u ON b.beneficiaryUserId = u.userId
    INNER JOIN accounts a ON u.userId = a.userId
    WHERE b.beneficiaryId = :beneficiaryId
    LIMIT 1
""")
    suspend fun getFriendById(beneficiaryId: Long): BeneficiaryDto?

}