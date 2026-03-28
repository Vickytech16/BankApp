package com.example.bankapp.repositories

import android.content.Context
import com.example.bankapp.R
import com.example.bankapp.entities.dtos.Country
import com.example.bankapp.entities.dtos.CountryJsonModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.json.Json

class CountryRepository(
    private val context: Context, ) {
    private val json: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }
    fun getCountries(): Flow<List<Country>> = flow {
        val jsonString = context.resources.openRawResource(R.raw.country)
            .bufferedReader()
            .use { it.readText() }

        val countryMap = json.decodeFromString<Map<String, CountryJsonModel>>(jsonString)

        val countryList = countryMap.map { (code, dto) ->
            dto.toUiModel(code)
        }.sortedBy { it.name }

        emit(countryList)
    }.flowOn(Dispatchers.IO)
}


private fun CountryJsonModel.toUiModel(code: String): Country {
    return Country(
        countryCode = code,
        name = this.countryName,
        emoji = this.countryFlagEmoji,
        phonePrefix = this.countryPhoneCodes.firstOrNull() ?: "",
        timezones = this.timezones.keys.toList(),
        displayTimezone = this.timezones.values.firstOrNull() ?: "UTC"
    )
}

