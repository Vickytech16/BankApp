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

    override val startOfDayMillis: Long
        get() {
            val utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                timeInMillis = epochMillis
            }
            return Calendar.getInstance(userTimeZone).apply {
                set(utcCal.get(Calendar.YEAR), utcCal.get(Calendar.MONTH), utcCal.get(Calendar.DAY_OF_MONTH), 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        }

    override val endOfDayMillis: Long
        get() = startOfDayMillis + (24 * 60 * 60 * 1000L) - 1

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

    override fun fileNameDate(): String {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmm", Locale.US)
        sdf.timeZone = userTimeZone
        return sdf.format(calendar.time)
    }
}