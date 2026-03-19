package com.example.bankapp.di

import android.content.Context
import androidx.room.Room
import com.example.bankapp.core.BankDatabase
import com.example.bankapp.repositories.AccountRepository
import com.example.bankapp.repositories.AccountRepositoryImpl
import com.example.bankapp.repositories.BeneficiaryRepository
import com.example.bankapp.repositories.TransactionRepository
import com.example.bankapp.repositories.UserRepository
import com.example.bankapp.repositories.UserRepositoryImpl
import com.example.bankapp.services.SessionManagementService
import com.example.bankapp.usecases.HomeSessionHandler
import com.example.bankapp.usecases.TransactionSessionHolder


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

    val transactionHolder = TransactionSessionHolder()

    val userRepository: UserRepository = UserRepositoryImpl(userDao)

    val accountRepository: AccountRepository = AccountRepositoryImpl(accountDao)

    val transactionRepository: TransactionRepository = TransactionRepository(transactionDao, ledgerDao, accountDao, database)

    val beneficiaryRepository: BeneficiaryRepository = BeneficiaryRepository(beneficiaryDao)
    private val sessionManagementService: SessionManagementService = SessionManagementService(applicationContext)
    private val useCaseContainer: UseCaseContainer = UseCaseContainer(
        sessionManagementService = sessionManagementService,
        userRepository = userRepository
    )
    val viewModelContainer: ViewModelContainer = ViewModelContainer(userRepository, useCaseContainer, accountRepository, transactionRepository)
}