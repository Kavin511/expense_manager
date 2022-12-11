package com.devstudio.expensemanager.ui.transaction.uicomponents

import android.content.Context
import android.text.Editable
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import com.devstudio.expensemanager.databinding.KeyboardBinding
import com.devstudio.utils.formulas.TransactionInputFormula

class TransactionKeyboard(
    val context: Context,
    private val editable: Editable,
    private val keyboardBinding: KeyboardBinding
) {
    var selectionPosition = 0

    private fun operatorClickListeners() {
        keyboardBinding.additionOperator.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                validateAndAppendOperator(KeyEvent.KEYCODE_NUMPAD_ADD)
            }
        })
        keyboardBinding.multiplicationOperator.setOnClickListener {
            validateAndAppendOperator(KeyEvent.KEYCODE_X)
        }
        keyboardBinding.divisionOperator.setOnClickListener {
            validateAndAppendOperator(KeyEvent.KEYCODE_NUMPAD_DIVIDE)
        }
        keyboardBinding.subtractionOperator.setOnClickListener {
            validateAndAppendOperator(KeyEvent.KEYCODE_NUMPAD_SUBTRACT)
        }
        keyboardBinding.equalToOperator.setOnClickListener {
            val value = editable.toString()
            editable.clear()
            editable.insert(0, TransactionInputFormula().calculate(value).toString())
        }
    }

    private fun validateAndAppendOperator(event: Int) {
        keyboardBinding.amountText.requestFocus()
        if (keyboardBinding.amountText.editableText.isNotEmpty()) {
            if (keyboardBinding.amountText.editableText.toString().last().isOperator()) {
                keyboardBinding.amountText.dispatchKeyEvent(
                    KeyEvent(
                        KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_DEL
                    )
                )
            }
            keyboardBinding.amountText.dispatchKeyEvent(
                KeyEvent(
                    KeyEvent.ACTION_DOWN,
                    event
                )
            )
        }
    }

    private fun numbersClickListeners() {
        keyboardBinding.oneValue.setOnClickListener {
            appendNumber(KeyEvent.KEYCODE_1)
        }
        keyboardBinding.twoValue.setOnClickListener {
            appendNumber(KeyEvent.KEYCODE_2)
        }
        keyboardBinding.threeValue.setOnClickListener {
            appendNumber(KeyEvent.KEYCODE_3)
        }
        keyboardBinding.fourValue.setOnClickListener {
            appendNumber(KeyEvent.KEYCODE_4)
        }
        keyboardBinding.fiveValue.setOnClickListener {
            appendNumber(KeyEvent.KEYCODE_5)
        }
        keyboardBinding.sevenValue.setOnClickListener {
            appendNumber(KeyEvent.KEYCODE_7)
        }
        keyboardBinding.sixValue.setOnClickListener {
            appendNumber(KeyEvent.KEYCODE_6)
        }
        keyboardBinding.eightValue.setOnClickListener {
            appendNumber(KeyEvent.KEYCODE_8)
        }
        keyboardBinding.nineValue.setOnClickListener {
            appendNumber(KeyEvent.KEYCODE_9)
        }
        keyboardBinding.zeroValue.setOnClickListener {
            appendNumber(KeyEvent.KEYCODE_0)
        }
        keyboardBinding.decimalValue.setOnClickListener {
            if (!editable.toString().split(Regex("[^0-9\\\\.]+")).last().contains(".")) {
                keyboardBinding.amountText.dispatchKeyEvent(
                    KeyEvent(
                        KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_PERIOD
                    )
                )
            } else {
                Toast.makeText(
                    context,
                    "Last entered amount already has a decimal!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun appendNumber(input: Int) {
        if (editable.toString() == "0") {
            editable.clear()
        }
        keyboardBinding.amountText.requestFocus()
        keyboardBinding.amountText.dispatchKeyEvent(
            KeyEvent(
                KeyEvent.ACTION_DOWN,
                input
            )
        )
    }

    fun initialiseListeners() {
        numbersClickListeners()
        operatorClickListeners()
        initialiseAmountTextBackSpaceEvent()
    }

    private fun initialiseAmountTextBackSpaceEvent() {
        keyboardBinding.amountTextWrapper.setEndIconOnClickListener {
            keyboardBinding.amountText.dispatchKeyEvent(
                KeyEvent(
                    KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_DEL
                )
            )
        }
        keyboardBinding.amountTextWrapper.setEndIconOnLongClickListener {
            editable.clear()
            true
        }
    }
}

private fun Char.isOperator(): Boolean {
    return this == '+' || this == '-' || this == 'x' || this == '/'
}
