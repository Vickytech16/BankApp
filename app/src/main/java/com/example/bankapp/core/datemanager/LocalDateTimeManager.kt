package com.example.bankapp.core.datemanager

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.Instant


class LocalDateTimeManagerImpl(
    private val timezoneProvider: TimezoneProvider
) : DateTimeManager {

    private fun getUserZoneId(): ZoneId {
        val zoneIdString = timezoneProvider.getTimezone()
        return try {
            ZoneId.of(zoneIdString)
        } catch (e: Exception) {
            ZoneId.of("Asia/Kolkata")
        }
    }

    override fun nowUTC(): Instant = Instant.now()

    override fun toMillis(dateTime: Any): Long = (dateTime as Instant).toEpochMilli()

    override fun fromMillis(millis: Long): Instant = Instant.ofEpochMilli(millis)

    override fun format(dateTime: Any, pattern: String): String {
        val formatter = DateTimeFormatter
            .ofPattern(pattern)
            .withZone(getUserZoneId())
        return formatter.format(dateTime as Instant)
    }

    override fun minusDays(dateTime: Any, days: Long): Instant {
        val instant = dateTime as Instant
        val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        return localDateTime.minusDays(days)
            .atZone(ZoneId.systemDefault())
            .toInstant()
    }

    override fun plusDays(dateTime: Any, days: Long): Instant {
        val instant = dateTime as Instant
        val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        return localDateTime.plusDays(days)
            .atZone(ZoneId.systemDefault())
            .toInstant()
    }

    override fun isBefore(dateTime1: Any, dateTime2: Any): Boolean =
        (dateTime1 as Instant).isBefore(dateTime2 as Instant)

    override fun isAfter(dateTime1: Any, dateTime2: Any): Boolean =
        (dateTime1 as Instant).isAfter(dateTime2 as Instant)
}