package com.devstudio.sharedmodule.importData.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devstudio.database.AppContext
import com.devstudio.database.ApplicationModule
import com.devstudio.database.models.Books
import com.devstudio.database.models.Category
import com.devstudio.database.models.DataSource
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
import com.devstudio.sharedmodule.importData.model.MappingStatus.MappingError.DateMappingFailed
import com.devstudio.sharedmodule.importData.model.MappingStatus.MappingError.TransactionModeMappingFailed
import com.devstudio.sharedmodule.importData.model.MetaInformation
import com.devstudio.sharedmodule.importData.model.TransactionField
import com.devstudio.sharedmodule.importData.model.TransactionFieldType.*
import com.devstudio.sharedmodule.importData.presentation.CsvImportIntent.MapTransactionField
import com.devstudio.sharedmodule.importData.presentation.CsvUIState.CloseImportScreen
import com.devstudio.sharedmodule.importData.presentation.CsvUIState.TransactionSaveResult
import com.devstudio.sharedmodule.saveTransactions
import com.devstudio.utils.utils.AppConstants.StringConstants.DEFAULT_BOOK_NAME
import com.devstudio.utils.utils.TransactionMode.EXPENSE
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import kotlin.math.abs

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
            csvUIState = csvUIData, columns = columns, csv = csv, shouldImportFile = shouldImportFile
        )
    }

    fun onEvent(event: CsvImportIntent) {
        when (event) {
            is CsvImportIntent.SelectFile -> {
                shouldImportFile = true
            }

            is CsvImportIntent.Import -> {
                shouldImportFile = false
                if (event.csv == null) {
                    csvUIData = CloseImportScreen
                    return
                } else {
                    csv = event.csv
                }
                columns = event.csv.firstOrNull() ?: CSVRow(mutableListOf())
                csvUIData = CsvUIState.MappingSelectedFile
            }

            is CsvImportIntent.SaveTransactions -> {
                viewModelScope.launch {
                    val transactionFieldIndex = TransactionFieldIndex()
                    var hasError = false
                    event.transactionField.forEach {
                        if (it.selectedFieldIndex.value == -1 && it.mappingStatus.value !is Mapped) {
                            it.mappingStatus.value = MappingError.FieldNotSelected(-1)
                            hasError = true
                        } else {
                            when (it.type) {
                                Note -> transactionFieldIndex.noteIndex =
                                    it.selectedFieldIndex.value

                                Amount -> transactionFieldIndex.amountIndex =
                                    it.selectedFieldIndex.value

                                TransactionModeField -> {
                                    transactionFieldIndex.transactionModeIndex =
                                        it.selectedFieldIndex.value
                                    transactionFieldIndex.transactionModeMetaList =
                                        it.additionalInfo ?: listOf()
                                }

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
                        val transactionList = event.csv.subList(1, csv.size).map {
                            val transactionMode = parseTransactionType(
                                it.values[transactionFieldIndex.transactionModeIndex],
                                transactionFieldIndex.transactionModeMetaList.toMutableList()
                            ) ?: EXPENSE
                            val bookId =
                                findBookByNameOrInsert(it.values.getOrNull(transactionFieldIndex.bookIdIndex))
                            val transactionDate =
                                parseDate(it.values[transactionFieldIndex.transactionDateIndex])
                                    ?: getPlatform().getCurrentTimeMillis()
                            Transaction(
                                id = getPlatform().getCurrentTimeMillis(),
                                bookId = bookId,
                                note = it.values[transactionFieldIndex.noteIndex],
                                amount = abs(it.values[transactionFieldIndex.amountIndex].toDoubleOrNull()?:0.0),
                                categoryId = getCategoryOrInsert(
                                    it, transactionFieldIndex, transactionMode.name, bookId
                                ),
                                transactionMode = transactionMode.name,
                                transactionDate = transactionDate.toString(),
                                dataSource = DataSource.CSV.value
                            )
                        }
                        val saveTransactions = saveTransactions(transactionList)
                        csvUIData = TransactionSaveResult(saveTransactions)
                    }
                }

            }

            is MapTransactionField -> onFieldMappingIntent(event)
        }
    }

    private fun onFieldMappingIntent(event: MapTransactionField) {
        val valueMap = csv.subList(1, csv.size).map { it.values[event.index] }
        for (value in valueMap) {
            val index = valueMap.indexOf(value)
            val mappingStatus = when (event.transactionField.type) {
                Amount -> parseAmount(value)?.let { Mapped(event.index) }
                    ?: MappingError.AmountMappingFailed(index)

                TransactionModeField -> parseTransactionType(
                    value, event.transactionField.additionalInfo
                )?.let { Mapped(event.index) } ?: TransactionModeMappingFailed(index)

                DATE -> parseDate(value)?.let { Mapped(event.index) } ?: DateMappingFailed(index)
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
        val categoryDao = ApplicationModule.config.factory.getRoomInstance().categoryDao()
        val categoryName = it.values.getOrNull(transactionFieldIndex.categoryIdIndex) ?: "Other"
        val category = categoryDao.getCategoryByName(categoryName)
        return if (category == null) {
            val constructedCategory = Category(name = categoryName, categoryType = transactionMode, bookId = bookId)
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
    var transactionModeMetaList: List<MetaInformation> = listOf(),
    var transactionDateIndex: Int = -1,
    var paymentStatusIndex: Int = -1,
)

interface CsvImportIntent {
    data object SelectFile : CsvImportIntent
    data class Import(val csv: List<CSVRow>?) : CsvImportIntent
    data class SaveTransactions(
        val csv: List<CSVRow>, val transactionField: List<TransactionField>
    ) : CsvImportIntent

    data class MapTransactionField(val transactionField: TransactionField, val index: Int) :
        CsvImportIntent
}

data class CsvImportState(
    val csvUIState: CsvUIState,
    val columns: CSVRow?,
    val csv: List<CSVRow>?,
    val shouldImportFile: Boolean,
)

sealed interface CsvUIState {
    data object Idle : CsvUIState
    data object SelectingFile : CsvUIState
    data object CloseImportScreen : CsvUIState
    data object MappingSelectedFile : CsvUIState
    data class TransactionSaveResult(val result: Result<Int>) : CsvUIState
}
