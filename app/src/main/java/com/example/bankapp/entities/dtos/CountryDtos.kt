package com.example.bankapp.entities.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CountryJsonModel(
    @SerialName("name")
    val countryName: String,

    @SerialName("emoji")
    val countryFlagEmoji: String,

    @SerialName("phone")
    val countryPhoneCodes: List<String>,

    @SerialName("timezones")
    val timezones: Map<String, String>,

    @SerialName("iso")
    val iso: IsoDetails
)

data class Country(
    val countryCode: String,
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