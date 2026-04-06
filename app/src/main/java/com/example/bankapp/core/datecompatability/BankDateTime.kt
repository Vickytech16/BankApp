package com.example.bankapp.core.datecompatability

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

interface BankDateTime {
    val epochMillis: Long

    val activeTimeZone: String

    val startOfDayMillis: Long
    val endOfDayMillis: Long

    fun minusDays(days: Int): BankDateTime

    fun toIsoString(): String

    fun toMonthDayDisplay(): String

    fun toFullDateTimeDisplay(): String

    fun fileNameDate(): String

    companion object {
        const val DAY_IN_MINUTES = 24 * 60 * 60 * 1000L
    }



}
