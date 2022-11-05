package com.devstudio.expensemanager.utils

class TransactionInputFormula {
    fun calculate(value: String): String {
        val numbersList = value.split(Regex("[^0-9\\\\.]+")).filter { it.isNotEmpty() }
        val operationsLst = value.filter { !it.isDigit() && it != '.' }
        var res = 0.0
        var operatorPosition = 0
        try {
            for (i in numbersList.indices step 2) {
                val value1 = numbersList[i]
                res += if (i + 1 < numbersList.size) {
                    val value2 = numbersList[i + 1]
                    applyOperator(
                        value1.toDouble(),
                        operationsLst[operatorPosition++].toString(),
                        value2.toDouble()
                    ).toDouble()
                } else {
                    value1.toDouble()
                }
            }
            return if (res == 0.0) "0" else CurrencyFormatter().format(res)
        } catch (e: Exception) {
            return "0"
        }
    }

    private fun applyOperator(value1: Double, operator: String?, value2: Double): String {
        var result = 0.0
        when (operator) {
            "+"  -> {
                result = value1 + value2
            }
            "-"  -> {
                result = value1 - value2
            }
            "/"  -> {
                if (value2 == 0.0) {
                    throw UnsupportedOperationException(
                        "Cannot divide by zero"
                    )
                }
                result = value1 / value2
            }
            else -> {
                result = value1 * value2
            }
        }
        return CurrencyFormatter().format(result)
    }
}