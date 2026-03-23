package com.example.bankapp.repositories


import com.example.bankapp.entities.dbtables.Beneficiary
import com.example.bankapp.daos.BeneficiaryDao
import com.example.bankapp.entities.dtos.FriendDto
import com.example.bankapp.utilities.uiUserId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BeneficiaryRepository(private val beneficiaryDao: BeneficiaryDao, private val userRepository: UserRepository) {

    fun getAllBeneficiaries(userId: Long): Flow<List<Beneficiary>> =
        beneficiaryDao.getAllBeneficiaries(userId)

    suspend fun addBeneficiary(userId: Long, beneficiaryUserId: Long, nickname: String? = null): Long = withContext(Dispatchers.IO) {
        val currentDate = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))

        val currentNickName =
            nickname ?: (userRepository.getUserByUserId(beneficiaryUserId.uiUserId)?.userName ?: "")


        val beneficiary = Beneficiary(
            userId = userId,
            beneficiaryUserId = beneficiaryUserId,
            nickname = currentNickName,
            isFavorite = false,
            addedDate = currentDate
        )
        beneficiaryDao.addBeneficiary(beneficiary)
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

    suspend fun getAllBeneficiariesForUser(userId: Long): List<FriendDto> {
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