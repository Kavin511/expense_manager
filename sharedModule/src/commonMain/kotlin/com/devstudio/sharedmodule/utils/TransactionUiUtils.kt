package com.devstudio.sharedmodule.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.devstudio.database.models.Transaction
import com.devstudio.utils.utils.TransactionMode
import com.devstudio.designSystem.appColors

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