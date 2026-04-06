package com.example.bankapp.utilities

import com.example.bankapp.core.datecompatability.BankDateTime
import com.example.bankapp.entities.dtos.Country
import java.util.Currency
import java.util.Locale

val Long.uiUserId: String get() = "USER" + this.toString().padStart(8, '0')
val Long.uiAccNo: String get() = (this + 100_000_000_000L).toString()

fun String.toDbUserId(): Long = this.removePrefix("USER").toLong()
fun String.toDbAccNo(): Long = this.toLong() - 100_000_000_000

fun BankDateTime.isSameDay(other: BankDateTime): Boolean {
    val thisDisplay = this.toIsoString().substringBefore("T")
    val otherDisplay = other.toIsoString().substringBefore("T")
    return thisDisplay == otherDisplay
}

