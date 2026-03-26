package com.example.bankapp.core.datecompatability

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
class RegularDateTime(override val epochMillis: Long) : BankDateTime {
    private val instant = Instant.ofEpochMilli(epochMillis)
    private val zonedDateTime = instant.atZone(ZoneId.systemDefault())

    override fun plusDays(days: Int): BankDateTime =
        BankDateFactory.fromMillis(zonedDateTime.plusDays(days.toLong()).toInstant().toEpochMilli())

    override fun minusDays(days: Int): BankDateTime =
        BankDateFactory.fromMillis(zonedDateTime.minusDays(days.toLong()).toInstant().toEpochMilli())

    override fun toFullDisplay(): String =
        zonedDateTime.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))

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
}