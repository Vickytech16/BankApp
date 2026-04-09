package com.example.bankapp.core.datecompatability

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

interface BankDateTime {

    companion object {
        const val DAY_IN_MINUTES = 24 * 60 * 60 * 1000L

        const val MONTH_DAY_PATTERN = "dd MMMM"

        const val FULL_DATE_TIME_PATTERN = "dd MMMM yyyy, hh:mm a"

        const val FILE_DATE_TIME = "yyyyMMdd_HHmm"

        const val UTC_ID = "UTC"
    }
    val epochMillis: Long

    val activeTimeZone: String

    val startOfDayMillis: Long
    val endOfDayMillis: Long

    fun minusDays(days: Int): BankDateTime

    fun toIsoString(): String

    fun toMonthDayDisplay(): String

    fun toFullDateTimeDisplay(): String

    fun fileNameDate(): String

    fun getStartOfDayUtc(): Long
}
