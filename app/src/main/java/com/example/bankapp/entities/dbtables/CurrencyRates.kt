package com.example.bankapp.entities.dbtables

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exchange_rates")
data class CurrencyRates(
    @PrimaryKey val id: Int = 1,
    val baseCode: String,
    val rates: Map<String, Double>,
    val lastUpdatedMillis: Long
)