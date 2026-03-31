package com.example.bankapp.core

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.bankapp.services.CurrencyExchangeWorkerService
import java.util.Calendar
import java.util.concurrent.TimeUnit

object WorkManagerInitializer {

    val interval: Long
        get() {
            val expiryDate = Calendar.getInstance().apply { set(2026, Calendar.APRIL, 11) }.timeInMillis
            return if (System.currentTimeMillis() < expiryDate) 62L else 1440L
        }

    fun scheduleCurrencyExchangeSync(context: Context) {

        val syncRequest = PeriodicWorkRequestBuilder<CurrencyExchangeWorkerService>(interval, TimeUnit.MINUTES)
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
}