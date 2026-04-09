package com.example.bankapp.di

import android.content.Context
import androidx.room.Room
import com.example.bankapp.core.BankDatabase
import com.example.bankapp.core.RetrofitClient
import com.example.bankapp.repositories.AccountRepository
import com.example.bankapp.repositories.AccountRepositoryImpl
import com.example.bankapp.repositories.BeneficiaryRepository
import com.example.bankapp.repositories.CountryRepository
import com.example.bankapp.repositories.CurrencyExchangeRepository
import com.example.bankapp.repositories.LedgerRepository
import com.example.bankapp.repositories.TransactionRepository
import com.example.bankapp.repositories.UserRepository
import com.example.bankapp.repositories.UserRepositoryImpl
import com.example.bankapp.services.SharedPreferenceService
import com.example.bankapp.entities.ChangePasswordState
import com.example.bankapp.utilities.SharedPreferenceHelper


class AppContainer(applicationContext: Context) {
    val database: BankDatabase =
        Room.databaseBuilder(
                applicationContext,
                BankDatabase::class.java,
                "bank_database"
            ).fallbackToDestructiveMigration(true).build()

    private val userDao = database.userDao()
    private val accountDao = database.accountDao()
    private val transactionDao = database.transactionDao()
    private val ledgerDao = database.ledgerDao()
    private val beneficiaryDao = database.beneficiaryDao()
    private val currencyRatesDao = database.currencyRatesDao()
    val userRepository: UserRepository = UserRepositoryImpl(userDao)
    val accountRepository: AccountRepository = AccountRepositoryImpl(accountDao)
    private val ledgerRepository: LedgerRepository = LedgerRepository(ledgerDao)
    val beneficiaryRepository: BeneficiaryRepository = BeneficiaryRepository(beneficiaryDao)
    val countryRepository: CountryRepository = CountryRepository(applicationContext)
    val currencyExchangeRepository: CurrencyExchangeRepository = CurrencyExchangeRepository(RetrofitClient.api, currencyRatesDao)
    val transactionRepository: TransactionRepository = TransactionRepository(transactionDao, ledgerRepository, accountRepository, currencyExchangeRepository, userRepository, database)
    private val sharedPreferenceService: SharedPreferenceService = SharedPreferenceService(applicationContext)
    val sharedPreferenceHelper: SharedPreferenceHelper = SharedPreferenceHelper(sharedPreferenceService, userRepository)
    val changePasswordState: ChangePasswordState = ChangePasswordState()
    val viewModelContainer: ViewModelContainer = ViewModelContainer(userRepository, changePasswordState,sharedPreferenceHelper, accountRepository,
        countryRepository)

}