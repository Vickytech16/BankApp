package com.example.bankapp.repositories

import android.util.Log
import com.example.bankapp.core.WorkManagerInitializer
import com.example.bankapp.core.datecompatability.BankDateFactory
import com.example.bankapp.daos.CurrencyRatesDao
import com.example.bankapp.entities.dbtables.CurrencyRates
import com.example.bankapp.services.CurrencyExchangeApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CurrencyExchangeRepository(
    private val currencyExchangeApi: CurrencyExchangeApiService,
    private val currencyRatesDao: CurrencyRatesDao
) {
    private val currencyExchangeApiKey = "f2c925d0f5b4fe18a44cfdd6"
    private var isFetching = false
    suspend fun getLatestRates(): CurrencyRates? {
        val cache = currencyRatesDao.getCachedRates()
        val expiryBuffer = WorkManagerInitializer.interval * 60 * 1000L

        val isExpired = cache == null || (System.currentTimeMillis() - cache.lastUpdatedMillis > expiryBuffer)

        if(cache!=null){
            Log.d("BANK_DEBUG", "Database has ${cache.rates.size} currencies stored in one row.")
            cache.rates.forEach { (code, value) ->
                Log.d("BANK_DEBUG", "Retrieved: $code -> $value")
            }
        }

        if (isExpired) {
            CoroutineScope(Dispatchers.IO).launch {
                fetchAndSave()
            }
        }
        return cache
    }

    suspend fun getLastUpdated(): String {
        val lastUpdatedMillis = currencyRatesDao.getLastUpdatedTime()
        return BankDateFactory.fromMillis(lastUpdatedMillis).toFullDateTimeDisplay()
    }

    private suspend fun fetchAndSave(): CurrencyRates? {

        if (isFetching)
            return null

        isFetching = true

        return try {
            val response = currencyExchangeApi.getLatestRates(currencyExchangeApiKey)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!

                val newCache = CurrencyRates(
                    baseCode = body.baseCode,
                    rates = body.conversionRates,
                    lastUpdatedMillis = System.currentTimeMillis()
                )
                currencyRatesDao.insertRates(newCache)
                newCache
            } else null
        } catch (_: Exception) {
            null
        } finally {
            isFetching = false
        }
    }
}