package com.devstudio.utils.formatters

import kotlin.math.pow
import kotlin.math.round

object StringFormatter {
    fun roundOffDecimal(double: Double, decimalPlaces: Int = 2): String {
        val factor = 10.0.pow(decimalPlaces)
        val roundedValue = round(double * factor) / factor
        return "%.${decimalPlaces}f".format(roundedValue)
    }
}

expect  fun String.format(vararg args: Any?): String