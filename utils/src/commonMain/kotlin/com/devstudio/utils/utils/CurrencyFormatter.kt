package com.devstudio.utils.utils

import kotlin.math.round

class CurrencyFormatter {
    fun format(value: Double): Double {
        return round(value * 100) / 100.0
    }
}