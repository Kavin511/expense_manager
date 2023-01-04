package com.devstudio.utils.formatters

import java.math.RoundingMode
import java.text.DecimalFormat

object StringFormatter {
    fun roundOffDecimal(double: Double, decimalPlaces: Int = 2): String {
        val decimalFormat = DecimalFormat("#.${"#".repeat(decimalPlaces)}")
        decimalFormat.roundingMode = RoundingMode.CEILING
        return decimalFormat.format(double)
    }
}