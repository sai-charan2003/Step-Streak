package com.charan.stepstreak.utils

import android.util.Log
import com.charan.stepstreak.data.model.StartOfWeekEnums
import java.time.DayOfWeek
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

object DateUtils {

    val ISO_LOCAL_DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val SHORT_WEEK_MONTH_DAY_FORMATTER =
        DateTimeFormatter.ofPattern("E, MMM d", Locale.ENGLISH)

    val SHORT_DAY_NAMES_MON_TO_SUN = listOf(
        "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"
    )
    val SHORT_DAY_NAMES_SUN_TO_SAT = listOf(
        "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"
    )

    fun getWeekDayList(userSetWeekStart: StartOfWeekEnums) : List<String>{
        return when(userSetWeekStart){
            StartOfWeekEnums.SUNDAY -> SHORT_DAY_NAMES_SUN_TO_SAT
            StartOfWeekEnums.MONDAY -> SHORT_DAY_NAMES_MON_TO_SUN
        }
    }

    fun formatInstantAsIsoLocalDateString(
        utcTimeString: Instant,
        zoneOffsetString: ZoneOffset?
    ): String {
        val localDateTime =
            ZonedDateTime.ofInstant(utcTimeString, zoneOffsetString ?: ZoneOffset.systemDefault())
        return localDateTime.format(ISO_LOCAL_DATE_FORMATTER)
    }

    fun getWeekdayName(date: String): String {
        val localDateTime = LocalDate.parse(date)
        return localDateTime.dayOfWeek.getDisplayName(
            TextStyle.SHORT,
            Locale.getDefault()
        )
    }

    fun getCurrentDate(): LocalDate {
        return LocalDate.now()
    }

    fun getCurrentWeekStartDate(userSetWeekStart: StartOfWeekEnums): LocalDate {
        val today = LocalDate.now()
        val currentDayOfWeek = today.dayOfWeek

        val startOfWeekDay = when (userSetWeekStart) {
            StartOfWeekEnums.SUNDAY -> DayOfWeek.SUNDAY
            StartOfWeekEnums.MONDAY -> DayOfWeek.MONDAY
        }

        val daysToSubtract = (7 + currentDayOfWeek.value - startOfWeekDay.value) % 7
        return today.minusDays(daysToSubtract.toLong())
    }

    fun getCurrentWeekEndDate(userSetWeekStart: StartOfWeekEnums): LocalDate {
        val startOfWeek = getCurrentWeekStartDate(userSetWeekStart)
        return startOfWeek.plusDays(6)
    }


    fun getGreetings(): String {
        val hour = LocalDateTime.now().hour
        return when (hour) {
            in 5..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            in 17..20 -> "Good evening"
            else -> "Good night"
        }
    }

    fun formatDateForDisplay(date: String): String {
        return try {
            val inputDate = LocalDate.parse(date)
            val today = LocalDate.now()

            when {
                inputDate.isEqual(today) -> "Today"
                inputDate.isEqual(today.minusDays(1)) -> "Yesterday"
                else -> inputDate.format(SHORT_WEEK_MONTH_DAY_FORMATTER)
            }
        } catch (e: Exception) {
            date
        }
    }

    fun getMonthStartDate(month: String, year: Int = LocalDate.now().year): String {
        val monthEnum = Month.valueOf(month.trim().uppercase(Locale.getDefault()))
        val startDate = LocalDate.of(year, monthEnum, 1)
        return startDate.format(ISO_LOCAL_DATE_FORMATTER)
    }

    fun getMonthEndDate(month: String, year: Int = LocalDate.now().year): String {
        val monthEnum = Month.valueOf(month.trim().uppercase(Locale.getDefault()))
        val endDate = LocalDate.of(year, monthEnum, 1).withDayOfMonth(monthEnum.length(LocalDate.of(year, monthEnum, 1).isLeapYear))
        return endDate.format(ISO_LOCAL_DATE_FORMATTER)
    }
    fun getMonthName(date : String) : String{
        return LocalDate.parse(date).month.getDisplayName(
            TextStyle.FULL,
            Locale.getDefault()
        )
    }

    fun getCurrentMonthName() : String{
        return LocalDate.now().month.getDisplayName(
            TextStyle.FULL,
            Locale.getDefault()
        )
    }


    fun convertInstantToEpochMillis(date: Instant, zoneOffset: ZoneOffset?): Long {
        val zonedDateTime = ZonedDateTime.ofInstant(date, zoneOffset ?: ZoneOffset.systemDefault())
        return zonedDateTime.toInstant().toEpochMilli()
    }

    val startOfCurrentDayMillis = ZonedDateTime.now()
        .toLocalDate()
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()

}