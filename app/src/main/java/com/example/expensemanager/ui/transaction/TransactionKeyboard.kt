package com.example.expensemanager.ui.transaction

import android.content.Context
import android.text.Editable
import android.widget.Toast
import com.example.expensemanager.databinding.KeyboardBinding
import com.example.expensemanager.utils.AppConstants

class TransactionKeyboard(
    val context: Context,
    private val editable: Editable,
    private val keyboardBinding: KeyboardBinding
) {
    var selectionPosition = 0

    private fun operatorClickListeners() {
        keyboardBinding.additionOperator.setOnClickListener {
            calculateAndAppendOperator(AppConstants.ADDITION)
        }
        keyboardBinding.multiplicationOperator.setOnClickListener {
            calculateAndAppendOperator(AppConstants.MULTIPLICATION)
        }
        keyboardBinding.divisionOperator.setOnClickListener {
            calculateAndAppendOperator(AppConstants.DIVISION)
        }
        keyboardBinding.subtractionOperator.setOnClickListener {
            calculateAndAppendOperator(AppConstants.SUBTRACTION)
        }
        keyboardBinding.equalToOperator.setOnClickListener {
            calculateExistingValues(
                editable.toString().split(Regex("[^0-9\\\\.]+")).filter { it.isNotEmpty() }, ""
            )
        }
    }

    private fun calculateAndAppendOperator(operator: String) {
        val values = editable.toString().split(Regex("[^0-9\\\\.]+")).filter { it.isNotEmpty() }
        if (values.size > 1) {
            calculateExistingValues(values, operator)
        } else if (values.size == 1) {
            editable.clear()
            editable.insert(selectionPosition, values[0] + operator)
        }
    }

    private fun calculateExistingValues(values: List<String>, operator: String) {
        when (values.isNotEmpty()) {
            editable.toString().contains(AppConstants.ADDITION) -> {
                editable.clear()
                editable.insert(
                    selectionPosition,
                    "%.2f".format(values[0].toDouble().plus(values[1].toDouble()))
                )
                editable.insert(selectionPosition, operator)
            }
            editable.toString().contains(AppConstants.DIVISION) -> {
                if (values[1].toLong() == 0L) {
                    Toast.makeText(context, "Dividing with 0 is not allowed!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    editable.clear()
                    editable.insert(
                        selectionPosition,
                        "%.2f".format(values[0].toDouble().div(values[1].toDouble()))
                    )
                    editable.insert(selectionPosition, operator)
                }
            }
            editable.toString().contains("x") -> {
                editable.clear()
                editable.insert(
                    selectionPosition,
                    "%.2f".format(values[0].toDouble().times(values[1].toDouble()))
                )
                editable.insert(selectionPosition, operator)
            }
            editable.toString().contains("-") -> {
                editable.clear()
                editable.insert(
                    0,
                    "%.2f".format(values[0].toDouble().minus(values[1].toDouble()))
                )
                editable.insert(selectionPosition, operator)
            }
        }
    }

    private fun numbersClickListeners() {
        keyboardBinding.oneValue.setOnClickListener {
            editable.insert(selectionPosition, "1")
        }
        keyboardBinding.twoValue.setOnClickListener {
            editable.insert(selectionPosition, "2")
        }
        keyboardBinding.threeValue.setOnClickListener {
            editable.insert(selectionPosition, "3")
        }
        keyboardBinding.fourValue.setOnClickListener {
            editable.insert(selectionPosition, "4")
        }
        keyboardBinding.fiveValue.setOnClickListener {
            editable.insert(selectionPosition, "5")
        }
        keyboardBinding.sevenValue.setOnClickListener {
            editable.insert(selectionPosition, "7")
        }
        keyboardBinding.sixValue.setOnClickListener {
            editable.insert(selectionPosition, "6")
        }
        keyboardBinding.eightValue.setOnClickListener {
            editable.insert(selectionPosition, "8")
        }
        keyboardBinding.nineValue.setOnClickListener {
            editable.insert(selectionPosition, "9")
        }
        keyboardBinding.zeroValue.setOnClickListener {
            editable.insert(selectionPosition, "0")
        }
        keyboardBinding.decimalValue.setOnClickListener {
            if (!editable.toString().split(Regex("[^0-9\\\\.]+")).last().contains(".")) {
                editable.insert(selectionPosition, ".")
            } else {
                Toast.makeText(
                    context,
                    "Last entered amount already has a decimal!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun initialiseListeners() {
        numbersClickListeners()
        operatorClickListeners()
    }
}