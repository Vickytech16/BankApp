package com.example.bankapp.repositories

import com.example.bankapp.daos.UserDao
import com.example.bankapp.entities.User
import com.example.bankapp.utilities.toDbUserId

interface UserRepository{
    suspend fun getUserByEmail(email: String): User?

    suspend fun getUserByPhoneNumber(phoneNumber: String): User?

    suspend fun createNewUser(user: User): Long

    suspend fun getAllUsers(): List<User>

    suspend fun ping()

    suspend fun updateUser(user: User)

    suspend fun getUserByEmailAndPhoneNumber(email: String, phoneNumber: String): User?

    suspend fun getUserByUserId(userId: String): User?
}

class UserRepositoryImpl(
    private val userDao: UserDao
): UserRepository{
    override suspend fun getUserByEmail(email: String): User? {
      return  userDao.getUserByEmail(email)
    }

    override suspend fun getUserByPhoneNumber(phoneNumber: String): User? {
      return  userDao.getUserByPhoneNumber(phoneNumber)
    }

    override suspend fun createNewUser(user: User): Long {
      return userDao.createUser(user)
    }

    override suspend fun getAllUsers(): List<User> {
        return userDao.getAllUsers()
    }

    override suspend fun ping() {
        userDao.ping()
    }

    override suspend fun getUserByEmailAndPhoneNumber(email: String, phoneNumber: String): User? {
       return userDao.getUserByEmailAndPhoneNumber(email, phoneNumber)
    }

    override suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    override suspend fun getUserByUserId(userId: String): User? {
        val formattedUserId = userId.toDbUserId()
        return userDao.getUserById(formattedUserId)
    }


}



