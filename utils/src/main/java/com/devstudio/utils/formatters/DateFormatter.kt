package com.devstudio.utils.formatters

import java.util.*

class DateFormatter {
    val monthNames =
        listOf("January", "February", "March", "April","May" ,"June", "July", "August", "September", "October", "November", "December")

    fun convertLongToDate(time: Long,format:String = DATE_MONTH_YEAR): String {
        val cal = Calendar.getInstance()
        cal.timeInMillis = time
        return String.format(
            format,
            monthNames[cal[Calendar.MONTH]],
            cal[Calendar.DAY_OF_MONTH],
            cal[Calendar.YEAR],
        )
    }

    fun getMonthAndYearFromLong(time: Long): String {
        val cal = Calendar.getInstance()
        cal.timeInMillis = time
        return String.format(
            MM_YY,
            monthNames[cal[Calendar.MONTH]],
            cal[Calendar.YEAR],
        )
    }
    companion object {
        const val MM_DD_YYYY = "MM/dd/yyyy"
        const val DATE_MONTH_YEAR = "%s %s, %s"
        const val MM_YY = "%s, %s"
    }
}