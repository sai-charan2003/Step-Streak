package com.charan.stepstreak.utils

import android.util.Log
import java.time.DayOfWeek
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.TextStyle
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
        val localDateTime = ZonedDateTime.ofInstant(utcTimeString, zoneOffsetString ?: ZoneOffset.systemDefault())
        return localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

fun convertLocalDateTimeToLocalDate(localDateTime: String): String {
    val parsedDateTime = LocalDate.parse(localDateTime, DateTimeFormatter.ISO_DATE)
    return parsedDateTime.toString()
}

    fun getCurrentWeekDates(): List<String> {
        val today = LocalDate.now()
        val startOfWeek = today.with(DayOfWeek.MONDAY)
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE

        return (0..6).map { startOfWeek.plusDays(it.toLong()).format(formatter) }
    }

    fun getWeekStartDateInISO() : LocalDateTime {
        val today = LocalDateTime.now()
        return today.with(DayOfWeek.MONDAY)
    }

    fun getWeekEndDateInISO() : LocalDateTime {
        val today = LocalDateTime.now()
        return today.with(DayOfWeek.SUNDAY)
    }

    fun getDateNumberFromIso(date: String): Int {
        val localDateTime = LocalDate.parse(date)
        return localDateTime.dayOfMonth
    }

    fun getWeekFromIso(date: String): String {
        val localDateTime = LocalDate.parse(date)
        return localDateTime.dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT,Locale.getDefault())
    }

    fun getCurrentDate() : LocalDate{
        return LocalDate.now()
    }

    fun getWeekStartDate() : LocalDate{
        val today = LocalDate.now()
        return today.with(DayOfWeek.MONDAY)

    }

    fun getWeekEndData() : LocalDate {
        val today = LocalDate.now()
        return today.with(DayOfWeek.SUNDAY)

    }

    fun getGreetings() : String{
        val hour = LocalDateTime.now().hour
        return when (hour) {
            in 5..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            in 17..20 -> "Good evening"
            else -> "Good night"
        }
    }

    fun formatDateWithSuffix(date: String): String {
        val inputDate = LocalDate.parse(date)
        val today = LocalDate.now()
        return when {
            inputDate.isEqual(today) -> "Today"
            inputDate.isEqual(today.minusDays(1)) -> "Yesterday"
            else -> {
                val dayOfWeek = inputDate.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                val month = inputDate.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                val day = inputDate.dayOfMonth
                "$dayOfWeek, $month $day"
            }
        }
    }

    fun convertToTimeMillis(date : Instant, zoneOffset: ZoneOffset?): Long {
        val zonedDateTime = ZonedDateTime.ofInstant(date, zoneOffset ?: ZoneOffset.systemDefault())
        return zonedDateTime.toInstant().toEpochMilli()
    }

    val currentDayMillis = ZonedDateTime.now()
        .toLocalDate() // Get only the date part (year, month, day)
        .atStartOfDay(ZoneId.systemDefault()) // Set time to midnight in the system's default time zone
        .toInstant()
        .toEpochMilli()

}