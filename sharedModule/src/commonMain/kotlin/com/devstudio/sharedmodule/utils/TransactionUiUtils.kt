package com.devstudio.sharedmodule.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.devstudio.database.models.Transaction
import com.devstudio.database.models.TransactionMode
import com.devstudio.theme.appColors

/**
 * @Author: Kavin
 * @Date: 16/07/24
 */

@Composable
fun getTransactionBlockColor(transaction: Transaction): Color {
    return when (transaction.transactionMode) {
        TransactionMode.EXPENSE.name -> {
            appColors.transactionExpenseColor
        }

        TransactionMode.INVESTMENT.name -> {
            appColors.transactionInvestmentColor
        }

        else -> {
            appColors.transactionIncomeColor
        }
    }
}