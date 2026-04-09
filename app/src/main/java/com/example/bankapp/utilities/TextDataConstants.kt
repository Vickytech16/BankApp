package com.example.bankapp.utilities

const val USERNAME_MAX_SIZE = 25

const val EMAIL_MAX_SIZE = 320

const val PASSWORD_MAX_SIZE = 64

const val PHONE_NUMBER_MAX_SIZE = 15

const val ACCOUNT_NUMBER_SIZE = 12

const val COUNTRY_MAX_SIZE = 50

const val TIMEZONE_MAX_SIZE = 100

const val USERNAME_MIN_SIZE = 3

val cashTransferAmountRegex = Regex("^(0|[1-9]\\d{0,9})(\\.\\d{0,2})?$")
val depositAmountRegex = Regex("^(0|[1-9]\\d{0,12})(\\.\\d{0,2})?$")

