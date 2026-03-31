package com.example.bankapp.core

import com.example.bankapp.services.CurrencyExchangeApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://v6.exchangerate-api.com/"

    val api: CurrencyExchangeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CurrencyExchangeApiService::class.java)
    }
}