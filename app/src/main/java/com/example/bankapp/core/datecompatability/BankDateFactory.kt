package com.example.bankapp.core.datecompatability

object BankDateFactory {
    private var activeTimeZone: String = "UTC"

    fun initialize(timeZoneId: String) {
        activeTimeZone = timeZoneId
    }

    fun now(): BankDateTime {
        return fromMillis(System.currentTimeMillis())
    }

    fun fromMillis(millis: Long, tempTimeZone: String? = null): BankDateTime {
        return if (android.os.Build.VERSION.SDK_INT >= 26) {
            RegularDateTime(millis, tempTimeZone ?: activeTimeZone)
        } else {
            LegacyDateTime(millis, tempTimeZone ?: activeTimeZone)
        }
    }
}