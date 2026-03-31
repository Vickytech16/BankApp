package com.example.bankapp.services

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.bankapp.BankApp
import dagger.hilt.android.qualifiers.ApplicationContext

class CurrencyExchangeWorkerService(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val repository = (applicationContext as BankApp).appContainer.currencyExchangeRepository
            repository.getLatestRates()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}