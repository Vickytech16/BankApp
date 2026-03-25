package com.example.bankapp.repositories


import com.example.bankapp.entities.dbtables.Beneficiary
import com.example.bankapp.daos.BeneficiaryDao
import com.example.bankapp.entities.dtos.BeneficiaryDto
import com.example.bankapp.utilities.toDbFormat
import com.example.bankapp.utilities.uiUserId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BeneficiaryRepository(private val beneficiaryDao: BeneficiaryDao, private val userRepository: UserRepository) {

    fun getAllBeneficiaries(userId: Long): Flow<List<Beneficiary>> =
        beneficiaryDao.getAllBeneficiaries(userId)

    suspend fun addBeneficiary(userId: Long, beneficiaryUserId: Long, nickname: String? = null): Long {
       return withContext(Dispatchers.IO) {

            val currentDate = LocalDateTime.now().toDbFormat()

            val currentNickName =
                nickname  ?: ""

            val beneficiary = Beneficiary(
                userId = userId,
                beneficiaryUserId = beneficiaryUserId,
                nickname = currentNickName,
                isFavorite = false,
                addedDate = currentDate
            )
            beneficiaryDao.addBeneficiary(beneficiary)
        }
    }

    suspend fun getBeneficiaryAccountNo(beneficiaryUserId: Long): Long? {
       return withContext(Dispatchers.IO) {
            beneficiaryDao.getBeneficiaryAccountNo(beneficiaryUserId)
        }
    }

    suspend fun getBeneficiary(userId: Long, beneficiaryUserId: Long): Beneficiary? {
        return withContext(Dispatchers.IO) {
            beneficiaryDao.getBeneficiary(userId, beneficiaryUserId)
        }
    }

    suspend fun getAllBeneficiariesForUser(userId: Long): List<BeneficiaryDto> {
        return  withContext(Dispatchers.IO) {
            beneficiaryDao.getAllFriends(userId)
        }
    }

    suspend fun updateBeneficiary(beneficiary: Beneficiary) {
        withContext(Dispatchers.IO) {
            beneficiaryDao.updateBeneficiary(beneficiary)
        }
    }

    suspend fun removeBeneficiary(userId: Long, beneficiaryUserId: Long) {
        withContext(Dispatchers.IO) {
            beneficiaryDao.removeBeneficiary(userId, beneficiaryUserId)
        }
    }

}