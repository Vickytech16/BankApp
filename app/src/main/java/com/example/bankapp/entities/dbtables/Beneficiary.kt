package com.example.bankapp.entities.dbtables

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "beneficiaries",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = arrayOf("userId"),
            childColumns = arrayOf("userId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = arrayOf("userId"),
            childColumns = arrayOf("beneficiaryUserId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["beneficiaryUserId"]),
        Index(value = ["userId", "beneficiaryUserId"], unique = true)
    ]
)
data class Beneficiary(
    @PrimaryKey(autoGenerate = true)
    val beneficiaryId: Long = 0,
    val userId: Long,
    val beneficiaryUserId: Long,
    val nickname: String = "",
    val isFavorite: Boolean = false,
    val addedDate: String
)