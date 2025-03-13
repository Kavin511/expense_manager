package com.devstudio.utils.formatters

import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object DateFormatter {
    fun convertLongToDate(time: String, format: String = DATE_MONTH_YEAR): String {
        return try {
            val instant = Instant.fromEpochMilliseconds(time.toLong())
            val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
            format.format(
                monthNames[dateTime.monthNumber - 1],
                dateTime.dayOfMonth,
                dateTime.year,
            )
        } catch (e: Exception) {
            time
        }
    }

    fun getCurrentDay(time: Long): Int {
        val instant = Instant.fromEpochMilliseconds(time)
        val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        return dateTime.dayOfMonth
    }

    fun getMonthAndYearFromLong(time: Long): String {
        val instant = Instant.fromEpochMilliseconds(time)
        val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        return MM_YY.format(
            monthNames[dateTime.monthNumber - 1],
            dateTime.year,
        )
    }

    fun getCurrentYear(time: Long): Int {
        val instant = Instant.fromEpochMilliseconds(time)
        val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        return dateTime.year
    }

    fun getCurrentMonth(time: Long): Int {
        val instant = Instant.fromEpochMilliseconds(time)
        val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        return dateTime.monthNumber - 1
    }

    const val MM_DD_YYYY = "MM/dd/yyyy"
    const val DATE_MONTH_YEAR = "%s %s, %s"
    const val MM_YY = "%s, %s"
    val monthNames = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )
}