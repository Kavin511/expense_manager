package com.devstudio.utils.formatters

import java.util.Calendar

object DateFormatter {
    fun convertLongToDate(time: String, format: String = DATE_MONTH_YEAR): String {
        try {
            val cal = Calendar.getInstance()
            cal.timeInMillis = time.toLong()
            return String.format(
                format,
                monthNames[getCurrentMonth(cal)],
                getCurrentDay(cal),
                getCurrentYear(cal),
            )
        } catch (e: Exception) {
            return time
        }
    }

    fun getCurrentDay(cal: Calendar) = cal[Calendar.DAY_OF_MONTH]

    fun getMonthAndYearFromLong(time: Long): String {
        val cal = Calendar.getInstance()
        cal.timeInMillis = time
        return String.format(
            MM_YY,
            monthNames[getCurrentMonth(cal)],
            getCurrentYear(cal),
        )
    }

    fun getCurrentYear(cal: Calendar) = cal[Calendar.YEAR]

    fun getCurrentMonth(cal: Calendar) = cal[Calendar.MONTH]
    const val MM_DD_YYYY = "MM/dd/yyyy"
    private const val DATE_MONTH_YEAR = "%s %s, %s"
    private const val MM_YY = "%s, %s"
    private const val JANUARY = "January"
    private const val FEBRUARY = "February"
    private const val MARCH = "March"
    private const val APRIL = "April"
    private const val MAY = "May"
    private const val JUNE = "June"
    private const val JULY = "July"
    private const val AUGUST = "August"
    private const val SEPTEMBER = "September"
    private const val OCTOBER = "October"
    private const val NOVEMBER = "November"
    private const val DECEMBER = "December"
    val monthNames = listOf(
        JANUARY,
        FEBRUARY,
        MARCH,
        APRIL,
        MAY,
        JUNE,
        JULY,
        AUGUST,
        SEPTEMBER,
        OCTOBER,
        NOVEMBER,
        DECEMBER,
    )
}
