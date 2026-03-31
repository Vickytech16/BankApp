package com.example.bankapp.services

import com.example.bankapp.entities.dtos.CurrencyExchangeApiResult
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CurrencyExchangeApiService {
    @GET("v6/{apiKey}/latest/{base}")
    suspend fun getLatestRates(
        @Path("apiKey") apiKey: String,
        @Path("base") base: String = "USD"
    ): Response<CurrencyExchangeApiResult>
}