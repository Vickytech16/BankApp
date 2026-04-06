package com.example.bankapp.core.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.bankapp.BankApp
import com.example.bankapp.core.datecompatability.BankDateFactory
import com.example.bankapp.core.datecompatability.BankDateTime
import com.example.bankapp.entities.errors.TransactionResult
import java.math.BigDecimal

class InterestCalculationWorker(
    context: Context,
    params: WorkerParameters,
) :
    CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val dayInMs = BankDateTime.DAY_IN_MINUTES
        val interestRate = BigDecimal("0.001")

        val appContainer = (applicationContext as BankApp).appContainer
        val accountRepository = appContainer.accountRepository
        val transactionRepository = appContainer.transactionRepository

        return try {
            val savingsAccounts = accountRepository.getAllSavingsAccounts()

            for (account in savingsAccounts) {
                val currentTime = BankDateFactory.now().epochMillis
                var lastApplied = account.lastInterestDate
                var currentBalance = account.balance

                while (currentTime - lastApplied >= dayInMs) {
                    val interestAmount = currentBalance.multiply(interestRate)
                    val targetInterestDate = lastApplied + dayInMs

                    val result = transactionRepository.addInterest(
                        accNo = account.accNo,
                        interestAmount = interestAmount,
                        interestDate = targetInterestDate
                    )

                    when (result) {
                        is TransactionResult.Success -> {
                            currentBalance = currentBalance.add(interestAmount)
                            lastApplied = targetInterestDate
                        }
                        is TransactionResult.Error.RepeatedTransaction -> {
                            lastApplied = targetInterestDate
                        }
                        else -> {
                            return Result.retry()
                        }
                    }
                }
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}