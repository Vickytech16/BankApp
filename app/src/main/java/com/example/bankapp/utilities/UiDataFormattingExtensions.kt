package com.example.bankapp.utilities

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


val Long.uiUserId: String get() = "USER" + this.toString().padStart(8, '0')
val Long.uiAccNo: String get() = (this + 100_000_000_000L).toString()


fun String.toDbUserId(): Long = this.removePrefix("USER").toLong()
fun String.toDbAccNo(): Long = this.toLong() - 100_000_000_000

fun formatTransactionDateTime(dateTimeString: String): String {
    return try {
        val localDateTime = LocalDateTime.parse(dateTimeString)
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy, hh:mm a")
        localDateTime.format(formatter)
    } catch (_: Exception) {
        dateTimeString
    }
}

fun LocalDateTime.toDbFormat(): String {
    return this.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
}