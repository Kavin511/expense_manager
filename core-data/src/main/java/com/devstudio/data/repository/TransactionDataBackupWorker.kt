package com.devstudio.data.repository

import android.content.Context
import android.os.Environment
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.util.Log
import androidx.core.os.BuildCompat
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.devstudio.expensemanager.db.ExpenseManagerDataBase
import com.devstudio.model.models.BackupStatus
import com.devstudio.model.models.Status
import com.devstudio.utils.formatters.DateFormatter
import com.devstudio.utils.utils.AppConstants.StringConstants.BACK_UP_STATUS_KEY
import com.devstudio.utils.utils.AppConstants.StringConstants.BACK_UP_STATUS_MESSAGE
import com.devstudio.utils.utils.AppConstants.StringConstants.WORK_TRIGGERING_MODE_KEY
import com.devstudio.utils.utils.CSVWriter
import com.google.firebase.crashlytics.FirebaseCrashlytics
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

    val db = Room.databaseBuilder(
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
            FirebaseCrashlytics.getInstance()
                .recordException(Throwable(e.message ?: "Failure in transactions backup"))
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
        val file = getFileToStoreTransactions(context)
        file.createNewFile()
        return CSVWriter(FileWriter(file, false))
    }

    companion object {
        fun getFileToStoreTransactions(context: Context): File {
            val backupPath = backupPath(context)
            val folder = File(backupPath)
            if (!folder.exists()) {
                folder.mkdirs()
            }
            return File(folder.absolutePath, BACK_UP_FILE_NAME)
        }

        fun backupPath(context: Context): String {
            val path = if (BuildCompat.isAtLeastT()) {
                context.filesDir.absolutePath
            } else {
                Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS,
                ).absolutePath
            }
            return "$path/${context.applicationInfo.packageName}"
        }

        const val BACK_UP_FILE_NAME = "transactions.csv"
    }
}
