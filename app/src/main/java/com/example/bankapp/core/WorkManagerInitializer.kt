package com.example.bankapp.core

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.bankapp.core.datecompatability.BankDateFactory
import com.example.bankapp.core.workers.CurrencyExchangeWorker
import com.example.bankapp.core.workers.InterestCalculationWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit

object WorkManagerInitializer {

    val interval: Long
        get() {
            val expiryDate = Calendar.getInstance().apply { set(2026, Calendar.APRIL, 16) }.timeInMillis
            return if (BankDateFactory.now().epochMillis < expiryDate) 62L else 1440L
        }

    fun scheduleCurrencyExchangeSync(context: Context) {

        val syncRequest = PeriodicWorkRequestBuilder<CurrencyExchangeWorker>(interval, TimeUnit.MINUTES)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "CurrencySync",
            ExistingPeriodicWorkPolicy.REPLACE,
            syncRequest
        )
    }

    fun scheduleDailyInterest(context: Context) {
        val interestRequest = PeriodicWorkRequestBuilder<InterestCalculationWorker>(24, TimeUnit.HOURS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "DailyInterestAccrual",
            ExistingPeriodicWorkPolicy.KEEP,
            interestRequest
        )
    }
}