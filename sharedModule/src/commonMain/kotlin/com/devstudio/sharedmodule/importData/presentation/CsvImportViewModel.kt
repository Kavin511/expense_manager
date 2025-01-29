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
import com.devstudio.sharedmodule.importData.domain.notBlankTrimmedString
import com.devstudio.sharedmodule.importData.domain.parseAmount
import com.devstudio.sharedmodule.importData.domain.parseDate
import com.devstudio.sharedmodule.importData.domain.parseTransactionType
import com.devstudio.sharedmodule.importData.model.CSVRow
import com.devstudio.sharedmodule.importData.model.MappingStatus.Mapped
import com.devstudio.sharedmodule.importData.model.MappingStatus.MappingError
import com.devstudio.sharedmodule.importData.model.MappingStatus.MappingError.CategoryMappingFailed
import com.devstudio.sharedmodule.importData.model.MappingStatus.MappingError.DATEMappingFailed
import com.devstudio.sharedmodule.importData.model.MappingStatus.MappingError.TransactionModeMappingFailed
import com.devstudio.sharedmodule.importData.model.TransactionField
import com.devstudio.sharedmodule.importData.model.TransactionFieldType.*
import com.devstudio.sharedmodule.importData.presentation.CsvImportEvent.FieldMappingEvent
import com.devstudio.sharedmodule.importData.presentation.CsvUIState.TransactionSaveResult
import com.devstudio.sharedmodule.saveTransactions
import com.devstudio.utils.utils.AppConstants.StringConstants.DEFAULT_BOOK_NAME
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
            csvData = csvUIData, columns = columns, csv = csv, shouldImportFile = shouldImportFile
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
                csvUIData = CsvUIState.MappingSelectedFile
            }

            is CsvImportEvent.SaveTransactions -> {
                viewModelScope.launch {
                    val transactionFieldIndex = TransactionFieldIndex()
                    var hasError = false
                    event.transactionField.forEach {
                        if (it.selectedFieldIndex.value == -1) {
                            it.mappingStatus.value = MappingError.FieldNotSelected(-1)
                            hasError = true
                        } else {
                            when (it.type) {
                                Note -> transactionFieldIndex.noteIndex =
                                    it.selectedFieldIndex.value

                                Amount -> transactionFieldIndex.amountIndex =
                                    it.selectedFieldIndex.value

                                TransactionModeField -> transactionFieldIndex.transactionModeIndex =
                                    it.selectedFieldIndex.value

                                DATE -> transactionFieldIndex.transactionDateIndex =
                                    it.selectedFieldIndex.value

                                BookName -> transactionFieldIndex.bookIdIndex =
                                    it.selectedFieldIndex.value

                                Category -> transactionFieldIndex.categoryIdIndex =
                                    it.selectedFieldIndex.value
                            }
                        }
                    }

                    if (hasError) {
                        csvUIData =
                            TransactionSaveResult(Result.failure(Throwable("Mapping failed")))
                    } else {
                        val transactionList = event.csv.map {
                            val transactionMode =
                                it.values[transactionFieldIndex.transactionModeIndex]
                            val bookId =
                                findBookByNameOrInsert(it.values.getOrNull(transactionFieldIndex.bookIdIndex))
                            Transaction(
                                id = getPlatform().getCurrentTimeMillis(),
                                bookId = bookId,
                                note = it.values[transactionFieldIndex.noteIndex],
                                amount = it.values[transactionFieldIndex.amountIndex].toDoubleOrNull()
                                    ?: 0.0,
                                categoryId = getCategoryOrInsert(
                                    it, transactionFieldIndex, transactionMode, bookId
                                ),
                                transactionMode = transactionMode,
                                transactionDate = it.values[transactionFieldIndex.transactionDateIndex],
                            )
                        }
                        val saveTransactions = saveTransactions(transactionList)
                        csvUIData = TransactionSaveResult(saveTransactions)
                    }
                }

            }

            is FieldMappingEvent -> onFieldMappingEvent(event)
        }
    }

    private fun onFieldMappingEvent(event: FieldMappingEvent) {
        val valueMap = csv.subList(1, csv.size).map { it.values[event.index] }
        for (value in valueMap) {
            val index = valueMap.indexOf(value)
            val mappingStatus = when (event.transactionField.type) {
                Amount -> parseAmount(value)?.let { Mapped(event.index) }
                    ?: MappingError.AmountMappingFailed(index)

                TransactionModeField -> parseTransactionType(
                    value, event.transactionField.additionalInfo
                )?.let { Mapped(event.index) } ?: TransactionModeMappingFailed(index)

                DATE -> parseDate(value)?.let { Mapped(event.index) } ?: DATEMappingFailed(index)
                BookName -> Mapped(event.index)

                Category -> notBlankTrimmedString(value)?.let { Mapped(event.index) }
                    ?: CategoryMappingFailed(index)

                Note -> Mapped(event.index)
            }
            if (mappingStatus is MappingError) {
                event.transactionField.mappingStatus.value = mappingStatus
                return
            }
        }
        event.transactionField.mappingStatus.value = Mapped(event.index)
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
            name = categoryName, categoryType = transactionMode, bookId = bookId
        )
        return if (category == null) {
            categoryDao.insertCategory(constructedCategory)
            constructedCategory.id
        } else {
            category.id
        }
    }

    private fun findBookByNameOrInsert(bookName: String?): Long {
        val booksDao = ApplicationModule.config.factory.getRoomInstance().booksDao()
        val book = if (bookName.isNullOrEmpty()) {
            booksDao.getBooks().firstOrNull()
        } else {
            booksDao.findBookByName(bookName)
        } ?: return booksDao.insertBook(
            Books(
                name = bookName ?: DEFAULT_BOOK_NAME, timeStamp = getPlatform().getCurrentTimeMillis()
            )
        )
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
    data class SaveTransactions(
        val csv: List<CSVRow>, val transactionField: List<TransactionField>
    ) : CsvImportEvent

    data class FieldMappingEvent(val transactionField: TransactionField, val index: Int) :
        CsvImportEvent
}

data class CsvImportState(
    val csvData: CsvUIState,
    val columns: CSVRow?,
    val csv: List<CSVRow>?,
    val shouldImportFile: Boolean,
)

sealed interface CsvUIState {
    data object Idle : CsvUIState
    data object SelectingFile : CsvUIState
    data object MappingSelectedFile : CsvUIState
    data class TransactionSaveResult(val result: Result<Boolean>) : CsvUIState
}
