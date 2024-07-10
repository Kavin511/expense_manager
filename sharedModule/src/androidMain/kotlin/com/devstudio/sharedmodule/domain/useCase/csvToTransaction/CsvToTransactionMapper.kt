package com.devstudio.sharedmodule.domain.useCase.csvToTransaction

import android.content.Context
import com.devstudio.expensemanager.db.models.TransactionMode
import com.devstudio.sharedmodule.domain.useCase.util.parseDateToTimestamp

class CsvToTransactionMapper(override val transactions: List<List<String>>) :
    TransactionMapper(transactions) {
    init {
        transactions.firstOrNull()?.let { initialiseIndex(it) }
    }

    private fun withdrawalAmount(row: List<String>) =
        row[withdrawalAmountIndex].toDoubleOrNull() ?: 0.0

    private fun depositAmount(row: List<String>) = row[depositAmountIndex].toDoubleOrNull() ?: 0.0

    override fun transactionMode(row: List<String>): String {
        if (transactionModeIndex != -1) {
            return row[transactionModeIndex]
        } else {
            return if (withdrawalAmount(row) > 0.0) {
                return TransactionMode.EXPENSE.name
            } else if (depositAmount(row) > 0.0) {
                return TransactionMode.INCOME.name
            } else {
                TransactionMode.EXPENSE.name
            }
        }
    }

    override fun transactionDate(row: List<String>): String {
        if (timeStampIndex != -1) {
            return row[timeStampIndex]
        }
        return parseDateToTimestamp(row[transactionDateIndex]).toString()
    }

}