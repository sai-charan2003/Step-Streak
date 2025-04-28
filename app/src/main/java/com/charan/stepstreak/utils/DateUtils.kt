package com.charan.stepstreak.utils

import androidx.glance.text.TextStyle
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Calendar
import java.util.Locale

object DateUtils {

    val weekList = listOf(
        "Mon",
        "Tue",
        "Wed",
        "Thu",
        "Fri",
        "Sat",
        "Sun"
    )
    fun convertUtcToLocalTime(utcTimeString: Instant, zoneOffsetString: ZoneOffset?): String {
        val localDateTime = ZonedDateTime.ofInstant(utcTimeString, zoneOffsetString)
        return localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    fun getCurrentWeekDates(): List<String> {
        val today = LocalDate.now()
        val startOfWeek = today.with(DayOfWeek.MONDAY)
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE

        return (0..6).map { startOfWeek.plusDays(it.toLong()).format(formatter) }
    }

    fun getWeekStartDate() : LocalDateTime {
        val today = LocalDateTime.now()
        return today.with(DayOfWeek.MONDAY)
    }

    fun getWeekEndDate() : LocalDateTime {
        val today = LocalDateTime.now()
        return today.with(DayOfWeek.SUNDAY)
    }

    fun getDateNumberFromIso(date: String): Int {
        val localDateTime = LocalDateTime.parse(date)
        return localDateTime.dayOfMonth
    }

    fun getWeekFromIso(date: String): String {
        val localDateTime = LocalDateTime.parse(date)
        return localDateTime.dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT,Locale.getDefault())
    }

    fun getCurrentDate() : LocalDate{
        return LocalDate.now()
    }

}