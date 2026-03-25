package com.example.bankapp.di

import android.content.Context
import androidx.room.Room
import com.example.bankapp.core.BankDatabase
import com.example.bankapp.di.providers.SessionStateProvider
import com.example.bankapp.repositories.AccountRepository
import com.example.bankapp.repositories.AccountRepositoryImpl
import com.example.bankapp.repositories.BeneficiaryRepository
import com.example.bankapp.repositories.TransactionRepository
import com.example.bankapp.repositories.UserRepository
import com.example.bankapp.repositories.UserRepositoryImpl
import com.example.bankapp.services.SharedPreferenceService


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


    val userRepository: UserRepository = UserRepositoryImpl(userDao)
    val accountRepository: AccountRepository = AccountRepositoryImpl(accountDao)
    val transactionRepository: TransactionRepository = TransactionRepository(transactionDao, ledgerDao, accountDao, database)
    val beneficiaryRepository: BeneficiaryRepository = BeneficiaryRepository(beneficiaryDao, userRepository)

    private val sharedPreferenceService: SharedPreferenceService = SharedPreferenceService(applicationContext)

    private val useCaseContainer: UseCaseContainer = UseCaseContainer(sharedPreferenceService = sharedPreferenceService, userRepository = userRepository)
    val sessionStateProvider: SessionStateProvider = SessionStateProvider(useCaseContainer.sharedPreferenceHelper, accountRepository)
    val viewModelContainer: ViewModelContainer = ViewModelContainer(userRepository, useCaseContainer, accountRepository, transactionRepository)
}