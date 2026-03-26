//package com.example.bankapp.repositories
//
//import android.content.Context
//import com.example.bankapp.entities.dtos.CountryDto
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.flow
//import kotlinx.coroutines.flow.flowOn
//import kotlinx.serialization.json.Json
//import kotlinx.serialization.decodeFromString
//
//class CountryRepository(
//    private val context: Context,
//    private val json: Json = Json {
//        ignoreUnknownKeys = true
//        isLenient = true
//        coerceInputValues = true
//    }
//) {
//    fun getCountries(): Flow<List<CountryDto>> = flow {
//        val jsonString = context.assets.open("countries.json")
//            .bufferedReader()
//            .use { it.readText() }
//
//        val countryMap = json.decodeFromString<Map<String, CountryDto>>(jsonString)
//
//        val countryList = countryMap.map { (code, dto) ->
//            dto.toUiModel(code)
//        }.sortedBy { it.name }
//
//        emit(countryList)
//    }.flowOn(Dispatchers.IO)
//}
//
//private fun CountryDto.toUiModel(code: String): CountryDto {
//    return CountryDto(
//        code = code,
//        name = this.name,
//        emoji = this.emoji,
//        phonePrefix = this.phonePrefix.firstOrNull().toString(),
//        timezones = this.timezones.keys.toList(),
//        displayTimezone = this.timezones.values.firstOrNull() ?: "UTC"
//    )
//}