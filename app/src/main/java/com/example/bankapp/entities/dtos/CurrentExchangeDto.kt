package com.example.bankapp.entities.dtos

import com.google.gson.annotations.SerializedName

data class CurrencyExchangeApiResult(
    @SerializedName("result") val result: String,
    @SerializedName("base_code") val baseCode: String,
    @SerializedName("conversion_rates") val conversionRates: Map<String, Double>,
    @SerializedName("time_last_update_unix") val lastUpdateUnix: Long
)