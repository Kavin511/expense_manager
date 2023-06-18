package com.devstudio.utils.utils

class AppConstants {
    companion object {
        val ADDITION = "+"
        val DIVISION = "/"
        val MULTIPLICATION = "x"
        val SUBTRACTION = "-"
        const val EXPENSE = "EXPENSE"
        const val INCOME = "INCOME"
        const val ALL = "All"
        val supportedTransactionTypes = listOf(EXPENSE, INCOME)
    }

    object StringConstants {
        const val TAG = "TAG_OUTPUT"
        const val BACK_UP_WORK_NAME = "backUp"
        const val BACK_UP_RESPONSE_KEY = "is_success"
        const val WORK_TRIGGERING_MODE_KEY = "isManuallyTriggered"
    }
}
