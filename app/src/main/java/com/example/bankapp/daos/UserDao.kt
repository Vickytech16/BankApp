package com.example.bankapp.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.bankapp.entities.dbtables.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun createUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    @Query("select * from users where email = :email limit 1")
    suspend fun getUserByEmail(email: String) : User?

    @Query("select * from users where phoneNumber = :phoneNumber limit 1")
    suspend fun getUserByPhoneNumber(phoneNumber: String) : User?

    @Query("select * from users")
    suspend fun getAllUsers(): List<User>

    @Query("select 1")
    suspend fun ping(): Int

    @Query("select * from users where email = :email and phoneNumber = :phoneNumber limit 1")
    suspend fun getUserByEmailAndPhoneNumber(email: String, phoneNumber: String): User?

    @Query("select * from users where userId = :userId limit 1")
    suspend fun getUserById(userId: Long) : User?
}