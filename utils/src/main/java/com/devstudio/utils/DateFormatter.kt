package com.devstudio.utils

import java.util.*

class DateFormatter {
    fun convertLongToDate(time: Long): String {
        val cal = Calendar.getInstance()
        val monthNames =
            listOf("Jan", "Feb", "Mar", "Apr", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        cal.timeInMillis = time
        return String.format(
            "%s %s, %s",
            monthNames[cal[Calendar.MONTH] - 1],
            cal[Calendar.DAY_OF_MONTH],
            cal[Calendar.YEAR],
        )
    }
}