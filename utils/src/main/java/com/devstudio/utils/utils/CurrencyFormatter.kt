package com.devstudio.utils.utils

import java.math.RoundingMode
import java.text.DecimalFormat

class CurrencyFormatter {
    fun format(value: Double): String {
        return DecimalFormat("0.00").format(value)
    }
}