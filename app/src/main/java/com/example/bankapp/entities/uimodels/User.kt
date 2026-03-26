package com.example.bankapp.entities.uimodels

data class User(
    val userName: String,
    val email: String,
    val phoneNumber: String,
    val passwordHashed: String,
    val pfpURL: String? = null,
    val countryCode: String,
    val timeZone: String
)