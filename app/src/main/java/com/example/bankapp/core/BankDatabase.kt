package com.example.bankapp.core

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.bankapp.daos.AccountDao
import com.example.bankapp.daos.BeneficiaryDao
import com.example.bankapp.daos.CurrencyRatesDao
import com.example.bankapp.daos.LedgerDao
import com.example.bankapp.daos.TransactionDao
import com.example.bankapp.daos.UserDao
import com.example.bankapp.entities.dbtables.Account
import com.example.bankapp.entities.dbtables.Beneficiary
import com.example.bankapp.entities.dbtables.CurrencyRates
import com.example.bankapp.entities.dbtables.Ledger
import com.example.bankapp.entities.dbtables.User
import com.example.bankapp.entities.dbtables.Transaction

@Database(
    entities = [User::class, Account::class, Ledger::class, Transaction::class, Beneficiary::class, CurrencyRates::class],
    version = 9,
    exportSchema = false
)
@TypeConverters(DbTypeConvertors::class)
abstract class BankDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun accountDao(): AccountDao
    abstract fun ledgerDao(): LedgerDao
    abstract fun transactionDao(): TransactionDao
    abstract fun beneficiaryDao(): BeneficiaryDao
    abstract fun currencyRatesDao(): CurrencyRatesDao
}