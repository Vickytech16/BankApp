package com.example.bankapp.core.datecompatability

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

interface BankDateTime {
    val epochMillis: Long

    val activeTimeZone: String

    fun plusDays(days: Int): BankDateTime
    fun minusDays(days: Int): BankDateTime

    fun toFullDisplay(): String
    fun toIsoString(): String

    fun toMonthDayDisplay(): String

    fun toFullDateTimeDisplay(): String
}
