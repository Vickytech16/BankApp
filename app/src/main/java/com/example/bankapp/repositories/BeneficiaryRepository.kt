package com.example.bankapp.repositories


import com.example.bankapp.core.datecompatability.BankDateFactory
import com.example.bankapp.entities.dbtables.Beneficiary
import com.example.bankapp.daos.BeneficiaryDao
import com.example.bankapp.entities.dtos.BeneficiaryDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class BeneficiaryRepository(private val beneficiaryDao: BeneficiaryDao) {

    suspend fun addBeneficiary(userId: Long, beneficiaryUserId: Long, nickname: String? = null): Long {
       return withContext(Dispatchers.IO) {

           val currentDate = BankDateFactory.now().epochMillis

           val beneficiary = Beneficiary(
                userId = userId,
                beneficiaryUserId = beneficiaryUserId,
                nickname = if(nickname.isNullOrEmpty()) null else nickname,
                isFavorite = false,
                addedDate = currentDate
            )
            beneficiaryDao.addBeneficiary(beneficiary)
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