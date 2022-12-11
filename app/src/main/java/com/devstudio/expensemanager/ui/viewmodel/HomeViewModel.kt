package com.devstudio.expensemanager.ui.viewmodel

import android.app.Application
import android.content.Context
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.devstudio.expensemanager.db.models.Transactions
import com.devstudio.expensemanager.db.repository.TransactionsRepository
import com.devstudio.expensemanager.model.BackupStatus
import com.devstudio.utils.DateFormatter
import com.devstudio.utils.utils.CSVWriter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.util.*

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    var repository: TransactionsRepository
    var totalExpenseAmount = MutableStateFlow(0.0)
    var totalIncomeAmount = MutableStateFlow(0.0)

    internal var context:Context
    init {
        repository =
            (application as com.devstudio.expensemanager.ExpenseManagerApplication).repository
        context=application.applicationContext
        getExpenseTransaction()
        getIncomeTransaction()
    }

    suspend fun transactions(): LiveData<List<Transactions>> {
            return repository.allTransactionsStream()
    }

    fun deleteTransaction(transaction: Transactions){
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
       return try{
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
            arrayOf(
                "Amount",
                "Category",
                "Transaction Date",
                "Note",
                "Transaction Mode"
            )
        )
        for (i in repository.transactions) {
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

    private fun createFileDirectoryToStoreTransaction(): CSVWriter {
        val folder =
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath + "/expressWallet/")
        if (!folder.exists()) {
            folder.mkdirs()
        }
        val file = File(folder.absolutePath, "transactions.csv")
        file.createNewFile()
        val csvWriter = CSVWriter(FileWriter(file,false))
        return csvWriter
    }


    private val _text = MutableStateFlow("Click + to add transactions")

    val text: StateFlow<String> = _text

}
