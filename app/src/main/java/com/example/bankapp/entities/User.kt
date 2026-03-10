package com.example.bankapp.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(tableName = "users",
    indices = [
        Index(value = ["email"], unique = true),
        Index(value = ["phoneNumber"], unique = true)
    ]
)
data class User(
    @PrimaryKey(autoGenerate = true) val userId: Long = 0,
    val userName: String,
    val email: String,
    val phoneNumber: String,
    val passwordHashed: String,
    val pfpURL: String? = null
)