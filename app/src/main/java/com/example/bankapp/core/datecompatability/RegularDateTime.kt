package com.example.bankapp.core.datecompatability

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
class RegularDateTime(override val epochMillis: Long, override val activeTimeZone: String) : BankDateTime {
    private val zoneId = ZoneId.of(activeTimeZone)
    private val instant = Instant.ofEpochMilli(epochMillis)
    private val zonedDateTime = instant.atZone(zoneId)

    override val startOfDayMillis: Long
        get() = Instant.ofEpochMilli(epochMillis)
            .atZone(java.time.ZoneOffset.UTC)
            .toLocalDate()
            .atStartOfDay(zoneId)
            .toInstant()
            .toEpochMilli()

    override val endOfDayMillis: Long
        get() = startOfDayMillis + (24 * 60 * 60 * 1000L) - 1

    override fun minusDays(days: Int): BankDateTime =
        BankDateFactory.fromMillis(zonedDateTime.minusDays(days.toLong()).toInstant().toEpochMilli())

    override fun toIsoString(): String = zonedDateTime.toString()

    override fun toString(): String = toIsoString()

    override fun toMonthDayDisplay(): String {
        val formatter = DateTimeFormatter.ofPattern("dd MMMM", Locale.getDefault())
        return zonedDateTime.format(formatter)
    }

    override fun toFullDateTimeDisplay(): String {
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy, hh:mm a", Locale.getDefault())
        return zonedDateTime.format(formatter)
    }

    override fun fileNameDate(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm", Locale.US)
        return zonedDateTime.format(formatter)
    }
}