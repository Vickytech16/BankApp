package com.example.bankapp.core

import androidx.room.TypeConverter
import com.example.bankapp.entities.types.account.AccountType
import com.example.bankapp.entities.types.transaction.LedgerDirection
import com.example.bankapp.entities.types.transaction.TransactionFailureType
import com.example.bankapp.entities.types.transaction.TransactionStatus
import com.example.bankapp.entities.types.transaction.TransactionType
import java.math.BigDecimal
import java.time.LocalDateTime

class DbTypeConvertors {
        @TypeConverter
        fun fromBigDecimal(value: BigDecimal): String = value.toPlainString()

        @TypeConverter
        fun toBigDecimal(value: String): BigDecimal = BigDecimal(value)

        @TypeConverter
        fun fromAccountType(type: AccountType): String = type.name

        @TypeConverter
        fun toAccountType(value: String): AccountType = AccountType.valueOf(value)

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

}