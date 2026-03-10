package com.example.bankapp.entities

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.bankapp.entities.types.AccountType
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity(
    tableName = "accounts",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = arrayOf("userId"),
            childColumns = arrayOf("userId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"])])

data class Account @RequiresApi(Build.VERSION_CODES.O) constructor(
    @PrimaryKey(autoGenerate = true) val accNo: Long = 0,
    @ColumnInfo(name = "userId")
    val userId: Long,
    val ifscCode: String="VANGI00001",
    val accountType: AccountType,
    val balance: BigDecimal = BigDecimal.ZERO,
    val createdAt: LocalDateTime,
    var updatedAt: LocalDateTime
    )
