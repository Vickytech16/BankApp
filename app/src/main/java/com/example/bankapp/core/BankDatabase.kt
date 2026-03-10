package com.example.bankapp.core

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.bankapp.daos.AccountDao
import com.example.bankapp.daos.LedgerDao
import com.example.bankapp.daos.TransactionDao
import com.example.bankapp.daos.UserDao
import com.example.bankapp.entities.Account
import com.example.bankapp.entities.Ledger
import com.example.bankapp.entities.User

@Database(
    entities = [User::class, Account::class, Ledger::class, com.example.bankapp.entities.Transaction::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(DbTypeConvertors::class)
abstract class BankDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun accountDao(): AccountDao
    abstract fun ledgerDao(): LedgerDao
    abstract fun transactionDao(): TransactionDao
}