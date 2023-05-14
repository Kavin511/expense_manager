package com.devstudio.utils.formulas

import com.devstudio.utils.utils.CurrencyFormatter

class TransactionInputFormula {
    fun calculate(value: String): Double {
        val numbersList = value.split(Regex("[^0-9\\\\.]+")).filter { it.isNotEmpty() }
        val operationsLst = value.filter { !it.isDigit() && it != '.' }
        return try {
            var res = numbersList[0]
            var operatorPosition = 0
            if(value[0]=='-'){
                res=value[0]+res
                operatorPosition++
            }
            for (i in 1 until numbersList.size) {
                res = applyOperator(res.toDouble(), operationsLst[operatorPosition++].toString(), numbersList[i].toDouble())
            }
            if (res == "0.0") 0.0 else CurrencyFormatter().format(res.toDouble()).toDouble()
        } catch (e: Exception) {
            0.0
        }
    }

    private fun applyOperator(value1: Double, operator: String?, value2: Double): String {
        val result: Double
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
        return result.toString()
    }
}