package com.devstudio.expensemanager.viewmodel

import android.os.Environment
import androidx.lifecycle.ViewModel
import com.devstudio.core_data.repository.TransactionsRepository
import com.devstudio.expensemanager.models.BackupStatus
import com.devstudio.utils.formatters.DateFormatter
import com.devstudio.utils.utils.CSVWriter
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import java.io.FileWriter
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: TransactionsRepository) :
    ViewModel() {
    fun exportTransactions(): BackupStatus {
        return try {
            val csvWriter = createFileDirectoryToStoreTransaction()
            writeTransactionsAsCSV(csvWriter)
            BackupStatus.success("Transactions backed up successfully")
        } catch (e: Exception) {
            BackupStatus.failure(e.message.toString())
        }
    }

    private fun writeTransactionsAsCSV(csvWriter: CSVWriter) {
        csvWriter.writeNext(
            "ID", columnNames()
        )
        for (i in repository.transactions()) {
            csvWriter.writeNext(
                i.id.toString(), arrayOf(
                    i.amount.toString(),
                    i.category,
                    DateFormatter.convertLongToDate(i.transactionDate.toLong()),
                    i.note,
                    i.transactionMode
                )
            )
        }
        csvWriter.close()
    }

    private fun columnNames(): Array<String?> = arrayOf(
        "Amount", "Category", "Transaction Date", "Note", "Transaction Mode"
    )

    private fun createFileDirectoryToStoreTransaction(): CSVWriter {
        val folder =
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath + "/expressWallet/")
        if (!folder.exists()) {
            folder.mkdirs()
        }
        val file = File(folder.absolutePath, "transactions.csv")
        file.createNewFile()
        return CSVWriter(FileWriter(file, false))
    }
}
