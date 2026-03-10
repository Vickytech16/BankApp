package com.example.bankapp.utilities


val Long.uiUserId: String get() = "USER" + this.toString().padStart(8, '0')
val Long.uiAccNo: String get() = (this + 100_000_000_000L).toString()


fun String.toDbUserId(): Long = this.removePrefix("USER").toLong()
fun String.toDbAccNo(): Long = this.toLong() - 100_000_000_000L