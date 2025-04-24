package com.charan.stepstreak.utils

import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.Instant
import java.time.LocalTime

object DateUtils {
    fun convertUtcToLocalTime(utcTimeString: Instant, zoneOffsetString: ZoneOffset?): String {
        val localDateTime = ZonedDateTime.ofInstant(utcTimeString, zoneOffsetString)
        return localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }
}