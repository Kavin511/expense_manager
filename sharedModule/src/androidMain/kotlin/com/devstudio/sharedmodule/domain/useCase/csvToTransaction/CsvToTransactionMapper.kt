package com.devstudio.sharedmodule.domain.useCase.csvToTransaction

import com.devstudio.database.models.TransactionMode
import com.devstudio.sharedmodule.domain.useCase.util.parseDateToTimestamp
import com.devstudio.sharedmodule.importData.model.CSVRow

class CsvToTransactionMapper(override val transactions: List<CSVRow>) :
    TransactionMapper(transactions) {
    init {
        transactions.firstOrNull()?.let { initialiseIndex(it) }
    }

    private fun withdrawalAmount(row: CSVRow) =
        row.values[withdrawalAmountIndex].toDoubleOrNull() ?: 0.0

    private fun depositAmount(row: CSVRow) = row.values[depositAmountIndex].toDoubleOrNull() ?: 0.0

    override fun transactionMode(row: CSVRow): String {
        return when {
            transactionModeIndex != -1 -> {
                row.values[transactionModeIndex]
            }
            withdrawalAmount(row) > 0.0 -> {
                TransactionMode.EXPENSE.name
            }
            depositAmount(row) > 0.0 -> {
                TransactionMode.INCOME.name
            }
            else -> {
                TransactionMode.EXPENSE.name
            }
        }
    }

    override fun transactionDate(row: CSVRow): String {
        if (timeStampIndex != -1) {
            return row.values[timeStampIndex]
        }
        return parseDateToTimestamp(row.values[transactionDateIndex]).toString()
    }

}