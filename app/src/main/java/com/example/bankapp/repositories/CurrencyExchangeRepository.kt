package com.example.bankapp.repositories

import com.example.bankapp.core.WorkManagerInitializer
import com.example.bankapp.core.datecompatability.BankDateFactory
import com.example.bankapp.core.datecompatability.BankDateTime
import com.example.bankapp.daos.CurrencyRatesDao
import com.example.bankapp.entities.dbtables.CurrencyRates
import com.example.bankapp.services.CurrencyExchangeApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class CurrencyExchangeRepository(
    private val currencyExchangeApi: CurrencyExchangeApiService,
    private val currencyRatesDao: CurrencyRatesDao
) {
    companion object {
        private const val CURRENCY_API_KEY = "f2c925d0f5b4fe18a44cfdd6"
    }

    private var isFetching = false
    suspend fun getLatestRates(): CurrencyRates? {
        val cache = currencyRatesDao.getCachedRates()
        val expiryBuffer = WorkManagerInitializer.interval * 60 * 1000L

        val threeDaysInMillis = 3 * BankDateTime.DAY_IN_MINUTES
        if (cache != null && (BankDateFactory.now().epochMillis - cache.lastUpdatedMillis > threeDaysInMillis)) {
            currencyRatesDao.clearRates()
            return null
        }

        val isExpired = cache == null || (BankDateFactory.now().epochMillis - cache.lastUpdatedMillis > expiryBuffer)

        if (isExpired) {
            if (cache == null) {
                return fetchAndSave()
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    fetchAndSave()
                }
            }
        }
        return cache
    }

    suspend fun getLastUpdated(): String {
        val lastUpdatedMillis = currencyRatesDao.getLastUpdatedTime()
        return BankDateFactory.fromMillis(lastUpdatedMillis).toFullDateTimeDisplay()
    }

    private suspend fun fetchAndSave(): CurrencyRates? {

        if (isFetching) return null

        isFetching = true

        return try {
            withTimeoutOrNull(20_000L) {
                val response = currencyExchangeApi.getLatestRates(CURRENCY_API_KEY)

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    val newCache = CurrencyRates(
                        baseCode = body.baseCode,
                        rates = body.conversionRates,
                        lastUpdatedMillis = BankDateFactory.now().epochMillis
                    )
                    currencyRatesDao.insertRates(newCache)
                    newCache
                } else {
                    null
                }
            }
        } catch (_: Exception) {
            null
        } finally {
            isFetching = false
        }
    }
}