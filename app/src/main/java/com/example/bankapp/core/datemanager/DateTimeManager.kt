package com.example.bankapp.core.datemanager

interface DateTimeManager {
    fun nowUTC(): Any
    fun toMillis(dateTime: Any): Long
    fun fromMillis(millis: Long): Any
    fun format(dateTime: Any, pattern: String): String
    fun minusDays(dateTime: Any, days: Long): Any
    fun plusDays(dateTime: Any, days: Long): Any
    fun isBefore(dateTime1: Any, dateTime2: Any): Boolean
    fun isAfter(dateTime1: Any, dateTime2: Any): Boolean
}