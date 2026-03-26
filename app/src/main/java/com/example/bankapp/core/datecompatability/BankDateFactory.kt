package com.example.bankapp.core.datecompatability

object BankDateFactory {
    fun now(): BankDateTime {
        return fromMillis(System.currentTimeMillis())
    }

    fun fromMillis(millis: Long): BankDateTime {
        return if (android.os.Build.VERSION.SDK_INT >= 26) {
            RegularDateTime(millis)
        } else {
            LegacyDateTime(millis)
        }
    }
}