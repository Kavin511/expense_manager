package com.devstudio.data.repository

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.devstudio.data.util.FileUtils.getBackupFolder
import com.devstudio.database.ExpenseManagerDataBase
import com.devstudio.data.model.BackupStatus
import com.devstudio.data.model.Status
import com.devstudio.utils.formatters.DateFormatter
import com.devstudio.utils.utils.AppConstants.StringConstants.BACK_UP_STATUS_KEY
import com.devstudio.utils.utils.AppConstants.StringConstants.BACK_UP_STATUS_MESSAGE
import com.devstudio.utils.utils.AppConstants.StringConstants.WORK_TRIGGERING_MODE_KEY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import java.io.File
import java.io.FileWriter

class TransactionDataBackupWorker(
    val context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    private val db = Room.databaseBuilder(
        context,
        ExpenseManagerDataBase::class.java,
        "expense_manager_database",
    ).allowMainThreadQueries().build()

    override suspend fun doWork(): Result {
        Log.d("doingWork", "doWork:${inputData.getBoolean(WORK_TRIGGERING_MODE_KEY, false)} ")
        if (inputData.getBoolean(WORK_TRIGGERING_MODE_KEY, false).not()) {
            return Result.failure()
        }
        val outputData = CoroutineScope(Dispatchers.IO).async {
            val backupStatus = exportTransactions()
            return@async workDataOf(
                BACK_UP_STATUS_KEY to (backupStatus.status == Status.SUCCESS),
                BACK_UP_STATUS_MESSAGE to backupStatus.message,
            )
        }.await()
        return Result.success(outputData)
    }

    private suspend fun exportTransactions(): BackupStatus {
        return try {
            val csvWriter = createFileDirectoryToStoreTransaction(context)
            writeTransactionsAsCSV(csvWriter)
            BackupStatus.success("Transactions backed up to documents folder successfully")
        } catch (e: Exception) {
            BackupStatus.failure(e.message.toString())
        }
    }

    private suspend fun writeTransactionsAsCSV(csvWriter: CSVWriter) {
        csvWriter.writeNext(
            "ID",
            columnNames(),
        )
        val transactions = db.transactionsDao().getAllTransactionsStream().first().groupBy {
            it.bookId
        }
        transactions.forEach {
            it.value.forEach { transaction ->
                csvWriter.writeNext(
                    transaction.id.toString(),
                    arrayOf(
                        it.key.toString(),
                        transaction.amount.toString(),
                        db.categoryDao().findCategoryById(transaction.categoryId)?.name
                            ?: transaction.categoryId,
                        DateFormatter.convertLongToDate(transaction.transactionDate),
                        transaction.note,
                        transaction.transactionMode,
                    ),
                )
            }
        }
        csvWriter.close()
    }

    private fun columnNames(): Array<String?> = arrayOf(
        "Book Name",
        "Amount",
        "Category",
        "Transaction Date",
        "Note",
        "Transaction Mode",
    )

    private fun createFileDirectoryToStoreTransaction(context: Context): CSVWriter {
        val file = getFileFolderToStoreTransactions(context)
        file.createNewFile()
        return CSVWriter(FileWriter(File(file, "transaction.csv"), false))
    }

    companion object {
        fun getFileFolderToStoreTransactions(context: Context): File {
            val backupPath = getBackupFolder(context)
            val backupFile = File(backupPath)
            if (!backupFile.exists()) {
                backupFile.mkdirs()
            }
            return File(backupFile.absolutePath)
        }
    }
}
