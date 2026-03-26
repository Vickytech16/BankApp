package com.example.bankapp.entities.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CountryDto(
    val name: String,
    val emoji: String,
    val phone: List<String>,
    val timezones: Map<String, String>,
    val iso: IsoDetails
)

@Serializable
data class IsoDetails(
    @SerialName("alpha-2") val alpha2: String,
    @SerialName("alpha-3") val alpha3: String
)