package com.example.expensemanager.utils

import java.math.RoundingMode
import java.text.DecimalFormat

class CurrencyFormatter {
    fun format(value: Double): String {
        return DecimalFormat("0.00").apply { roundingMode = RoundingMode.UP }.format(value)
    }
}