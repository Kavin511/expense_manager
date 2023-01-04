package com.devstudio.expensemanager.ui.viewmodel

import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devstudio.expensemanager.db.models.Transactions
import com.devstudio.expensemanager.db.repository.TransactionsRepository
import com.devstudio.expensemanager.model.BackupStatus
import com.devstudio.utils.formatters.DateFormatter
import com.devstudio.utils.utils.CSVWriter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: TransactionsRepository) : ViewModel() {
    var totalExpenseAmount = MutableStateFlow(0.0)
    var totalIncomeAmount = MutableStateFlow(0.0)

    init {
        getExpenseTransaction()
        getIncomeTransaction()
    }

    suspend fun transactions(): LiveData<List<Transactions>> {
        return repository.allTransactionsStream()
    }

    fun deleteTransaction(transaction: Transactions) {
        viewModelScope.launch {
            repository.deleteTransactions(transaction)
        }
    }

    fun getExpenseTransaction() {
        viewModelScope.launch {
            repository.getExpenseTransaction().collectLatest {
                totalExpenseAmount.value = it.filter {
                    DateFormatter().getMonthAndYearFromLong(it.transactionDate.toLong()) == DateFormatter().getMonthAndYearFromLong(
                        Calendar.getInstance().timeInMillis
                    )
                }.sumOf { transaction -> transaction.amount }
            }
        }
    }

    fun getIncomeTransaction() {
        viewModelScope.launch {
            repository.getIncomeTransaction().collectLatest {
                totalIncomeAmount.value = it.filter {
                    DateFormatter().getMonthAndYearFromLong(it.transactionDate.toLong()) == DateFormatter().getMonthAndYearFromLong(
                        Calendar.getInstance().timeInMillis
                    )
                }.sumOf { transaction -> transaction.amount }
            }
        }
    }

    fun getTransactions(): LiveData<List<Transactions>> {
        return repository.allTransactionsStream()
    }

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
            "ID",
            columnNames()
        )
        for (i in repository.transactions()) {
            csvWriter.writeNext(
                i.id.toString(),
                arrayOf(
                    i.amount.toString(),
                    i.category,
                    DateFormatter().convertLongToDate(i.transactionDate.toLong()),
                    i.note,
                    i.transactionMode
                )
            )
        }
        csvWriter.close()
    }

    private fun columnNames(): Array<String?> = arrayOf(
        "Amount",
        "Category",
        "Transaction Date",
        "Note", "Transaction Mode"
    )

    private fun createFileDirectoryToStoreTransaction(): CSVWriter {
        val folder =
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath + "/expressWallet/")
        if (!folder.exists()) {
            folder.mkdirs()
        }
        val file = File(folder.absolutePath, "transactions.csv")
        file.createNewFile()
        val csvWriter = CSVWriter(FileWriter(file, false))
        return csvWriter
    }


    private val _text = MutableStateFlow("Click + to add transactions")

    val text: StateFlow<String> = _text

}
