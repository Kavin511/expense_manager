package com.devstudio.core_data.repository

import android.content.Context
import android.os.Environment
import androidx.room.Room
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.devstudio.core_model.models.BackupStatus
import com.devstudio.expensemanager.db.ExpenseManagerDataBase
import com.devstudio.utils.formatters.DateFormatter
import com.devstudio.utils.utils.AppConstants.StringConstants.BACK_UP_RESPONSE_KEY
import com.devstudio.utils.utils.CSVWriter
import java.io.File
import java.io.FileWriter

class TransactionDataBackupWorker(
    val context: Context,
    workerParams: WorkerParameters,
) : Worker(context, workerParams) {

    val db = Room.databaseBuilder(
        context,
        ExpenseManagerDataBase::class.java,
        "expense_manager_database"
    ).allowMainThreadQueries().build()

    override fun doWork(): Result {
        val backupStatus = exportTransactions()
        val outputData = workDataOf(BACK_UP_RESPONSE_KEY to backupStatus.message)
        return Result.success(outputData)
    }

    private fun exportTransactions(): BackupStatus {
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
        for (i in db.transactionsDao().getTransactions()) {
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