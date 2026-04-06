package com.example.bankapp.core.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.bankapp.BankApp

class CurrencyExchangeWorker(appContext: Context, workerParams: WorkerParameters) :
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