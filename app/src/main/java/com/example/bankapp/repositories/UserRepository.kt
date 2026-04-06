package com.example.bankapp.repositories

import com.example.bankapp.daos.UserDao
import com.example.bankapp.entities.dbtables.User
import com.example.bankapp.utilities.toDbUserId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

interface UserRepository{

    suspend fun getUserByEmail(email: String): User?

    suspend fun getUserByPhoneNumber(phoneNumber: String): User?

    suspend fun createNewUser(user: User): Long


    suspend fun ping()

    suspend fun updateUser(user: User)

    suspend fun getUserByEmailAndPhoneNumber(email: String, phoneNumber: String): User?

    suspend fun getUserByUserId(userId: String): User?

    fun getUserAsFlowByUserId(userId: Long): Flow<User?>

}

class UserRepositoryImpl(
    private val userDao: UserDao
): UserRepository{

    override suspend fun getUserByEmail(email: String): User? {
      return withContext(Dispatchers.IO) {
          userDao.getUserByEmail(email)
      }
    }

    override suspend fun getUserByPhoneNumber(phoneNumber: String): User? {
      return withContext(Dispatchers.IO){
          userDao.getUserByPhoneNumber(phoneNumber)
          }
    }

    override suspend fun createNewUser(user: User): Long {
      return withContext(Dispatchers.IO){
          userDao.createUser(user)
      }
    }

    override suspend fun ping() {
        userDao.ping()
    }

    override suspend fun getUserByEmailAndPhoneNumber(email: String, phoneNumber: String): User? {
       return withContext(Dispatchers.IO) {
           userDao.getUserByEmailAndPhoneNumber(email, phoneNumber)
       }
    }

    override suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    override suspend fun getUserByUserId(userId: String): User? {
        val formattedUserId = userId.toDbUserId()
        return withContext(Dispatchers.IO) {
            userDao.getUserById(formattedUserId)
        }
    }

    override fun getUserAsFlowByUserId(userId: Long): Flow<User?> {
        return userDao.getUserAsFlowByUserId(userId)
    }
}



