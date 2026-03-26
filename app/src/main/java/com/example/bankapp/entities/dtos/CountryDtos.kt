package com.example.bankapp.entities.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Country(
    val countryCode: String,
    val countryName: String,
    val countryFlagEmoji: String,
    val countryPhoneCode: List<String>,
    val timezones: Map<String, String>,
    val iso: IsoDetails
)

data class CountryDto(
    val code: String,
    val name: String,
    val emoji: String,
    val phonePrefix: String,
    val timezones: List<String>,
    val displayTimezone: String
)

@Serializable
data class IsoDetails(
    @SerialName("alpha-2") val alpha2: String,
    @SerialName("alpha-3") val alpha3: String
)