package com.charan.stepstreak.utils

import java.time.DayOfWeek
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
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

}