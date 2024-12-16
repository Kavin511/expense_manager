package com.devstudio.sharedmodule.importData.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devstudio.database.ApplicationModule
import com.devstudio.database.models.Books
import com.devstudio.database.models.Category
import com.devstudio.database.models.Transaction
import com.devstudio.designSystem.getPlatform
import com.devstudio.sharedmodule.importData.model.CSVRow
import com.devstudio.sharedmodule.importData.model.TransactionField
import com.devstudio.sharedmodule.importData.model.TransactionFieldType.*
import com.devstudio.sharedmodule.saveTransactions
import kotlinx.coroutines.launch

/**
 * @Author: Kavin
 * @Date: 15/11/24
 */
class CsvImportViewModel : ViewModel() {
    private var columns by mutableStateOf(CSVRow(mutableListOf()))
    private var csv by mutableStateOf<List<CSVRow>>(emptyList())
    private var shouldImportFile by mutableStateOf(false)
    private var csvUIData by mutableStateOf<CsvUIState>(CsvUIState.Idle)

    fun uiState(): CsvImportState {
        return CsvImportState(
            csvData = csvUIData,
            columns = columns,
            csv = csv,
            shouldImportFile = shouldImportFile
        )
    }

    fun onEvent(event: CsvImportEvent) {
        when (event) {
            is CsvImportEvent.SelectFile -> {
                shouldImportFile = true
            }

            is CsvImportEvent.Import -> {
                shouldImportFile = false
                csv = event.csv ?: emptyList()
                columns = event.csv?.firstOrNull() ?: CSVRow(mutableListOf())
                csvUIData = CsvUIState.Result
            }

            is CsvImportEvent.saveTransactions -> {
                viewModelScope.launch {
                    val header = event.csv[0]
                    val transactionFieldIndex = TransactionFieldIndex()
                    event.transactionField.forEach {
                        when (it.type) {
                            NOTE -> transactionFieldIndex.noteIndex = it.selectedFieldIndex.value
                            AMOUNT -> transactionFieldIndex.amountIndex =
                                it.selectedFieldIndex.value

                            TRANSACTION_MODE -> transactionFieldIndex.transactionModeIndex =
                                it.selectedFieldIndex.value

                            DATE -> transactionFieldIndex.transactionDateIndex =
                                it.selectedFieldIndex.value

                            BOOK_NAME -> transactionFieldIndex.bookIdIndex =
                                it.selectedFieldIndex.value

                            CATEGORY -> transactionFieldIndex.categoryIdIndex =
                                it.selectedFieldIndex.value
                        }
                    }

                    val transactionList = event.csv.map {
                        val transactionMode = it.values[transactionFieldIndex.transactionModeIndex]
                        val bookId = findBookByNameOrInsert(it.values[transactionFieldIndex.bookIdIndex])
                        Transaction(
                            id = getPlatform().getCurrentTimeMillis(),
                            bookId = bookId,
                            note = it.values[transactionFieldIndex.noteIndex],
                            amount = it.values[transactionFieldIndex.amountIndex].toDoubleOrNull() ?: 0.0,
                            categoryId = getCategoryOrInsert(it, transactionFieldIndex, transactionMode, bookId),
                            transactionMode = transactionMode,//convert this as any of avaible three
                            transactionDate = it.values[transactionFieldIndex.transactionDateIndex],
                        )
                    }
                    csvUIData = CsvUIState.TransactionSaveProcessed(saveTransactions(transactionList))
                }
            }
        }
    }

    private fun getCategoryOrInsert(
        it: CSVRow,
        transactionFieldIndex: TransactionFieldIndex,
        transactionMode: String,
        bookId: Long
    ): String {
        val categoryName = it.values[transactionFieldIndex.categoryIdIndex]
        val categoryDao = ApplicationModule.config.factory.getRoomInstance().categoryDao()
        val category = categoryDao.getCategoryByName(categoryName)
        val constructedCategory = Category(
            name = categoryName,
            categoryType = transactionMode,
            bookId = bookId
        )
        return if (category == null) {
            categoryDao.insertCategory(constructedCategory)
            constructedCategory.id
        } else {
            category.id
        }
    }

    private fun findBookByNameOrInsert(bookName: String): Long {
        val booksDao = ApplicationModule.config.factory.getRoomInstance().booksDao()
        val book = booksDao.findBookByName(bookName)
            ?: return booksDao.insertBook(Books(name = bookName, timeStamp = getPlatform().getCurrentTimeMillis()))
        return book.id
    }

}

data class TransactionFieldIndex(
    var bookIdIndex: Int = -1,
    var noteIndex: Int = -1,
    var amountIndex: Int = -1,
    var categoryIdIndex: Int = -1,
    var transactionModeIndex: Int = -1,
    var transactionDateIndex: Int = -1,
    var paymentStatusIndex: Int = -1,
)

interface CsvImportEvent {
    data object SelectFile : CsvImportEvent
    data class Import(val csv: List<CSVRow>?) : CsvImportEvent
    data class saveTransactions(val csv: List<CSVRow>, val transactionField: List<TransactionField>) : CsvImportEvent
}

data class CsvImportState(
    val csvData: CsvUIState,
    val columns: CSVRow?,
    val csv: List<CSVRow>?,
    val shouldImportFile: Boolean,
)

sealed interface CsvUIState {
    data object Idle : CsvUIState
    data object Loading : CsvUIState
    data object Result : CsvUIState
    data class TransactionSaveProcessed(val transactionImportResult: TransactionImportResult) : CsvUIState
}

sealed class TransactionImportResult {
    data object ImportedSuccessfully : TransactionImportResult()
    data object PartiallyImported : TransactionImportResult()
    data object ImportFailed : TransactionImportResult()
}