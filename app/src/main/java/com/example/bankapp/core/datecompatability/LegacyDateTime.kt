package com.example.bankapp.core.datecompatability

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class LegacyDateTime(override val epochMillis: Long) : BankDateTime {
    private val calendar = Calendar.getInstance().apply {
        timeInMillis = epochMillis
    }

    override fun plusDays(days: Int): BankDateTime {
        val newCal = calendar.clone() as Calendar
        newCal.add(Calendar.DAY_OF_YEAR, days)
        return BankDateFactory.fromMillis(newCal.timeInMillis)
    }

    override fun minusDays(days: Int): BankDateTime {
        val newCal = calendar.clone() as Calendar
        newCal.add(Calendar.DAY_OF_YEAR, -days)
        return BankDateFactory.fromMillis(newCal.timeInMillis)
    }

    override fun toFullDisplay(): String {
        val format = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault())
        return format.format(calendar.time)
    }

    override fun toIsoString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(calendar.time)
    }

    override fun toString(): String = toIsoString()

    override fun toMonthDayDisplay(): String {
        val sdf = SimpleDateFormat("dd MMMM", Locale.getDefault())
        return sdf.format(calendar.time)
    }

    override fun toFullDateTimeDisplay(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy, hh:mm a", Locale.getDefault())
        return sdf.format(java.util.Date(epochMillis))
    }
}