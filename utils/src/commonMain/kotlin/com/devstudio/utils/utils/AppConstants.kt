package com.devstudio.utils.utils

import com.devstudio.utils.utils.TransactionMode.EXPENSE
import com.devstudio.utils.utils.TransactionMode.INCOME
import com.devstudio.utils.utils.TransactionMode.INVESTMENT

class AppConstants {
    companion object {
        val ADDITION = "+"
        val DIVISION = "/"
        val MULTIPLICATION = "x"
        val SUBTRACTION = "-"
        const val ALL = "All"
        val supportedTransactionTypes = listOf(
            EXPENSE.name,
            INCOME.title,
            INVESTMENT.title
        )
    }

    object StringConstants {
        const val TAG = "TAG_OUTPUT"
        const val BACK_UP_WORK_NAME = "backUp"
        const val BACK_UP_STATUS_KEY = "is_success"
        const val BACK_UP_STATUS_MESSAGE = "backUpStatusMessage"
        const val WORK_TRIGGERING_MODE_KEY = "isManuallyTriggered"
        const val DEFAULT_BOOK_NAME = "Daily Book"
    }
}
