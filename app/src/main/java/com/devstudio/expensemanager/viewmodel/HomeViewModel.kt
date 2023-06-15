package com.devstudio.expensemanager.viewmodel

import android.content.Context
import android.os.Environment
import androidx.core.os.BuildCompat
import androidx.lifecycle.ViewModel
import com.devstudio.core_data.repository.CategoryRepository
import com.devstudio.core_data.repository.TransactionsRepository
import com.devstudio.expensemanager.models.BackupStatus
import com.devstudio.utils.formatters.DateFormatter
import com.devstudio.utils.utils.CSVWriter
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import java.io.FileWriter
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TransactionsRepository,
    private val categoryRepository: CategoryRepository
) :
    ViewModel() {
    fun exportTransactions(context: Context): BackupStatus {
        return try {
            val csvWriter = createFileDirectoryToStoreTransaction(context)
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
                    categoryRepository.findCategoryById(i.categoryId).name,
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

    @androidx.annotation.OptIn(BuildCompat.PrereleaseSdkCheck::class)
    private fun createFileDirectoryToStoreTransaction(context: Context): CSVWriter {
        val path = if (BuildCompat.isAtLeastT()) {
            context.filesDir.absolutePath
        } else {
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS
            ).absolutePath
        }
        val folder =
            File("$path/expressWallet/")
        if (!folder.exists()) {
            folder.mkdirs()
        }
        val file = File(folder.absolutePath, "transactions.csv")
        file.createNewFile()
        return CSVWriter(FileWriter(file, false))
    }
}
