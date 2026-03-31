package com.example.bankapp.core.datecompatability

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class LegacyDateTime(override val epochMillis: Long, override val activeTimeZone: String) : BankDateTime {

    private val userTimeZone = TimeZone.getTimeZone(activeTimeZone)

    private val calendar = Calendar.getInstance(userTimeZone).apply {
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

    private fun getFormatter(pattern: String): SimpleDateFormat {
        return SimpleDateFormat(pattern, Locale.getDefault()).apply {
            timeZone = userTimeZone
        }
    }

    override fun toFullDisplay(): String {
        val format = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault())
        format.timeZone = userTimeZone
        return format.format(calendar.time)
    }

    override fun toIsoString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(calendar.time)
    }

    override fun toString(): String = toIsoString()

    override fun toMonthDayDisplay(): String {
        return getFormatter("dd MMMM").format(calendar.time)
    }

    override fun toFullDateTimeDisplay(): String {
        return getFormatter("dd MMMM yyyy, hh:mm a").format(calendar.time)
    }

    override fun getDateAndTime(millis: Long): String {
        val date = java.util.Date(millis)
        val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", java.util.Locale.getDefault())
        return formatter.format(date)
    }
}