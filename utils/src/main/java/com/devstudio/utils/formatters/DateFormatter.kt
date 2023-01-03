package com.devstudio.utils.formatters

import java.util.*

class DateFormatter {
    val monthNames =
        listOf("January", "February", "March", "April","May" ,"June", "July", "August", "September", "October", "November", "December")

    fun convertLongToDate(time: Long): String {
        val cal = Calendar.getInstance()
        cal.timeInMillis = time
        return String.format(
            "%s %s, %s",
            monthNames[cal[Calendar.MONTH]],
            cal[Calendar.DAY_OF_MONTH],
            cal[Calendar.YEAR],
        )
    }

    fun getMonthAndYearFromLong(time: Long): String {
        val cal = Calendar.getInstance()
        cal.timeInMillis = time
        return String.format(
            "%s, %s",
            monthNames[cal[Calendar.MONTH]],
            cal[Calendar.YEAR],
        )
    }
}