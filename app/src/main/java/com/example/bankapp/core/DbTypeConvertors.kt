package com.example.bankapp.core

import androidx.room.TypeConverter
import com.example.bankapp.core.datecompatability.BankDateFactory
import com.example.bankapp.core.datecompatability.BankDateTime
import com.example.bankapp.entities.types.account.AccountType
import com.example.bankapp.entities.types.transaction.LedgerDirection
import com.example.bankapp.entities.types.transaction.TransactionFailureType
import com.example.bankapp.entities.types.transaction.TransactionStatus
import com.example.bankapp.entities.types.transaction.TransactionType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.math.BigDecimal

class DbTypeConvertors {
    @TypeConverter
    fun fromBigDecimal(value: BigDecimal): String = value.toPlainString()

    @TypeConverter
    fun toBigDecimal(value: String): BigDecimal = BigDecimal(value)

    @TypeConverter
    fun fromAccountType(type: AccountType): String {
        return type.code
    }

    @TypeConverter
    fun toAccountType(value: String): AccountType {
        return when (value.uppercase()) {
            "SAVINGS" -> AccountType.Savings
            "CURRENT" -> AccountType.Current
            else -> AccountType.Savings
        }
    }
    @TypeConverter
    fun fromLedgerDirection(direction: LedgerDirection): String = direction.name

    @TypeConverter
    fun toLedgerDirection(value: String): LedgerDirection =  LedgerDirection.valueOf(value)

    @TypeConverter
    fun fromTransactionType(type: TransactionType): String = type.name

    @TypeConverter
    fun toTransactionType(value: String): TransactionType = TransactionType.valueOf(value)

    @TypeConverter
    fun fromTransactionStatus(status: TransactionStatus): String = status.name

    @TypeConverter
    fun toTransactionStatus(value: String): TransactionStatus = TransactionStatus.valueOf(value)

    @TypeConverter
    fun fromTransactionFailureType(value: TransactionFailureType?): String? = value?.name

    @TypeConverter
    fun toTransactionFailureType(value: String?): TransactionFailureType? = value?.let { TransactionFailureType.valueOf(it) }

    @TypeConverter
    fun fromBankDate(date: BankDateTime?): Long? { return date?.epochMillis }

    @TypeConverter
    fun toBankDate(millis: Long?): BankDateTime? { return millis?.let { BankDateFactory.fromMillis(it) } }

    private val gson = Gson()

    @TypeConverter
    fun fromMap(map: Map<String, Double>?): String {
        return gson.toJson(map)
    }

    @TypeConverter
    fun toMap(value: String): Map<String, Double> {
        val mapType = object : TypeToken<Map<String, Double>>() {}.type
        return gson.fromJson(value, mapType) ?: emptyMap()
    }
}