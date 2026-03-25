package com.example.bankapp.core.datemanager

import java.text.SimpleDateFormat
import java.util.*

class LegacyDateManagerImpl(
    private val timezoneProvider: TimezoneProvider
) : DateTimeManager {

    private fun getUserTimeZone(): TimeZone {
        val zoneIdString = timezoneProvider.getTimezone()
        return TimeZone.getTimeZone(zoneIdString)
    }

    override fun nowUTC(): Date = Date()

    override fun toMillis(dateTime: Any): Long = (dateTime as Date).time

    override fun fromMillis(millis: Long): Date = Date(millis)

    override fun format(dateTime: Any, pattern: String): String {
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        formatter.timeZone = getUserTimeZone()
        return formatter.format(dateTime as Date)
    }

    override fun minusDays(dateTime: Any, days: Long): Date {
        val cal = Calendar.getInstance()
        cal.time = dateTime as Date
        cal.add(Calendar.DAY_OF_MONTH, -days.toInt())
        return cal.time
    }

    override fun plusDays(dateTime: Any, days: Long): Date {
        val cal = Calendar.getInstance()
        cal.time = dateTime as Date
        cal.add(Calendar.DAY_OF_MONTH, days.toInt())
        return cal.time
    }

    override fun isBefore(dateTime1: Any, dateTime2: Any): Boolean =
        (dateTime1 as Date).before(dateTime2 as Date)

    override fun isAfter(dateTime1: Any, dateTime2: Any): Boolean =
        (dateTime1 as Date).after(dateTime2 as Date)
}