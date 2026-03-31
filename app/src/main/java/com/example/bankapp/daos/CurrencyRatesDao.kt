package com.example.bankapp.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bankapp.entities.dbtables.CurrencyRates


@Dao
interface CurrencyRatesDao {
    @Query("SELECT * FROM exchange_rates WHERE id = 1 LIMIT 1")
    suspend fun getCachedRates(): CurrencyRates?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRates(cache: CurrencyRates)

    @Query("DELETE FROM exchange_rates")
    suspend fun clearCache()

    @Query("SELECT lastUpdatedMillis from EXCHANGE_RATES WHERE ID = 1 LIMIT 1")
    suspend fun getLastUpdatedTime(): Long
}