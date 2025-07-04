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
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeParseException
import java.time.format.TextStyle
import java.time.temporal.IsoFields
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
    private val INPUT_FORMATTER = DateTimeFormatter.ofPattern("MMM yyyy", Locale.getDefault())

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

    fun getDayFromDate(date: String): String {
        return try {
            val localDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE)
            localDate.dayOfMonth.toString()
        } catch (e: DateTimeParseException) {
            ""
        }
    }

    fun getMonthStartDate(monthYear: String): String {
        val yearMonth = YearMonth.parse(monthYear, INPUT_FORMATTER)
        val startDate = yearMonth.atDay(1)
        return startDate.format(ISO_LOCAL_DATE_FORMATTER)
    }

    fun getMonthEndDate(monthYear: String): String {
        val yearMonth = YearMonth.parse(monthYear, INPUT_FORMATTER)
        val endDate = yearMonth.atEndOfMonth()
        return endDate.format(ISO_LOCAL_DATE_FORMATTER)
    }
    fun getMonthName(date : String) : String{
        return LocalDate.parse(date).month.getDisplayName(
            TextStyle.FULL,
            Locale.getDefault()
        )
    }

    fun getCurrentMonthWithYear(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("MMM yyyy", Locale.getDefault())
        return currentDate.format(formatter)
    }


    fun convertInstantToEpochMillis(date: Instant, zoneOffset: ZoneOffset?): Long {
        val zonedDateTime = ZonedDateTime.ofInstant(date, zoneOffset ?: ZoneOffset.systemDefault())
        return zonedDateTime.toInstant().toEpochMilli()
    }

    fun getAllDatesInMonth(date: String): List<String> {
        return try {
            val localDate = LocalDate.parse(date, ISO_LOCAL_DATE_FORMATTER)
            val year = localDate.year
            val month = localDate.month
            val daysInMonth = month.length(localDate.isLeapYear)

            (1..daysInMonth).map { day ->
                LocalDate.of(year, month, day).format(ISO_LOCAL_DATE_FORMATTER)
            }
        } catch (e: DateTimeParseException) {
            emptyList()
        }
    }

    fun getPreviousMonth(currentMonthYear: String): String {
        val formatter = DateTimeFormatter.ofPattern("MMM yyyy", Locale.getDefault())
        val yearMonth = YearMonth.parse(currentMonthYear, formatter)
        val previousYearMonth = yearMonth.minusMonths(1)
        return previousYearMonth.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()) + " ${previousYearMonth.year}"
    }

    fun getNextMonth(currentMonthYear: String): String {
        val formatter = DateTimeFormatter.ofPattern("MMM yyyy", Locale.getDefault())
        val yearMonth = YearMonth.parse(currentMonthYear, formatter)
        val nextYearMonth = yearMonth.plusMonths(1)
        return nextYearMonth.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()) + " ${nextYearMonth.year}"
    }

    fun getCurrentWeekRange(userSetWeekStart: StartOfWeekEnums): Pair<String, String> {
        val startDate = getCurrentWeekStartDate(userSetWeekStart)
        val endDate = getCurrentWeekEndDate(userSetWeekStart)
        return Pair(startDate.format(ISO_LOCAL_DATE_FORMATTER), endDate.format(ISO_LOCAL_DATE_FORMATTER))
    }

    fun getNextWeekRange(userSetWeekStart: StartOfWeekEnums, weekLastDate: String): Pair<String, String> {
        val lastWeekEndDate = LocalDate.parse(weekLastDate, ISO_LOCAL_DATE_FORMATTER)
        val nextWeekStart = lastWeekEndDate.plusDays(1)
        val nextWeekEnd = nextWeekStart.plusDays(6)
        return Pair(nextWeekStart.format(ISO_LOCAL_DATE_FORMATTER), nextWeekEnd.format(ISO_LOCAL_DATE_FORMATTER))
    }

    fun getPreviousWeekRange(userSetWeekStart: StartOfWeekEnums, weekFirstDate: String): Pair<String, String> {
        val firstWeekDate = LocalDate.parse(weekFirstDate, ISO_LOCAL_DATE_FORMATTER)
        val previousWeekEnd = firstWeekDate.minusDays(1)
        val previousWeekStart = previousWeekEnd.minusDays(6)
        return Pair(previousWeekStart.format(ISO_LOCAL_DATE_FORMATTER), previousWeekEnd.format(ISO_LOCAL_DATE_FORMATTER))
    }

    fun getFormattedDate(date : String) : String {
        val date  = LocalDate.parse(date,ISO_LOCAL_DATE_FORMATTER )
        return date.format(SHORT_WEEK_MONTH_DAY_FORMATTER)
    }


    val startOfCurrentDayMillis = ZonedDateTime.now()
        .toLocalDate()
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()

}