package com.devstudio.sharedmodule.domain.useCase.csvToTransaction

import android.content.Context
import com.devstudio.data.datastore.DataSourceModule
import com.devstudio.data.repository.BooksRepositoryImpl
import com.devstudio.data.repository.CategoryRepositoryImpl
import com.devstudio.data.repository.UserDataRepositoryImpl
import com.devstudio.database.models.Category
import com.devstudio.database.models.Transaction
import com.devstudio.sharedmodule.domain.useCase.util.contains
import com.devstudio.sharedmodule.domain.useCase.util.getCategoryMapping
import com.devstudio.sharedmodule.domain.useCase.util.parseDateToTimestamp
import com.devstudio.sharedmodule.importData.model.CSVRow
import com.devstudio.sharedmodule.importData.model.TransactionMapResult
import com.devstudio.sharedmodule.importData.model.TransactionWithIndex
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last

abstract class TransactionMapper(open val transactions: List<CSVRow>) {
    open fun getNote(row: CSVRow): String {
        return row.values[noteIndex]
    }

    open fun getAmount(row: CSVRow): Double {
        return row.values[amountIndex].toDoubleOrNull() ?: 0.0
    }

    open fun categoryId(context: Context, row: CSVRow): String {
        if (categoryIdIndex == -1) {
            return ""
        }
        val matchingCategoryId = getMatchingCategoryId(context, row.values[categoryIdIndex], getNote(row))
            ?: return insertOrGetDefaultCategory(row, context)
        return matchingCategoryId
    }

    private fun insertOrGetDefaultCategory(
        row: CSVRow, context: Context
    ): String {
        if (categoryIdIndex != -1) {
            val category =
                Category(name = row.values[categoryIdIndex], categoryType = transactionMode(row))
            CategoryRepositoryImpl(context).insertCategory(category)
            return category.id
        } else {
            return CategoryRepositoryImpl(context).getCategoryByName("Others")?.id.orEmpty()
        }
    }

    abstract fun transactionMode(row: CSVRow): String
    open fun transactionDate(row: CSVRow): String {
        return parseDateToTimestamp(row.values[transactionDateIndex]).toString()
    }

    suspend fun invoke(context: Context): TransactionMapResult {
        val result = mutableListOf<TransactionWithIndex>()
        val conflictResult = mutableListOf<TransactionWithIndex>()
        if (transactions.isEmpty()) {
            return TransactionMapResult(emptyList(), emptyList())
        }
        transactions.subList(1, transactions.size).forEachIndexed { index, row ->
            var hasConflict = false
            val transaction = Transaction()
            transaction.apply {
                this.bookId = safeGet<Long> { getBookId(context, row) } ?: 0
                this.amount = safeGet<Double> { getAmount(row) } ?: run {
                    hasConflict = true
                    0.0
                }
                this.transactionMode = safeGet<String> { transactionMode(row) } ?: run {
                    hasConflict = true
                    ""
                }
                this.categoryId = safeGet<String> { categoryId(context, row) } ?: ""
                this.note = safeGet<String> { getNote(row) } ?: ""
                this.transactionDate = safeGet<String> { transactionDate(row) } ?: run {
                    hasConflict = true
                    ""
                }
            }.also {
                if (hasConflict) {
                    conflictResult.add(TransactionWithIndex(index = index, transaction = it))
                } else {
                    result.add(TransactionWithIndex(index = index, transaction = it))
                }
            }
        }
        return TransactionMapResult(result, conflictResult)
    }

    inline fun <reified T> safeGet(getData: () -> T): T? {
        return try {
            getData.invoke()
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun getBookId(context: Context, row: CSVRow): Long {
        val userDataRepository = UserDataRepositoryImpl(DataSourceModule(context))
        val booksRepositoryImpl = BooksRepositoryImpl(context, userDataRepository)
        if (bookNameIndex == -1) {
            return userDataRepository.getSelectedBookId().last()
        }
        return (booksRepositoryImpl.findBookByName(row.values[bookNameIndex])?.id)
            ?: userDataRepository.getSelectedBookId().first()
    }

    open var noteIndex: Int = -1
    open var amountIndex: Int = -1
    open var categoryIdIndex: Int = -1
    open var transactionDateIndex: Int = -1
    open var withdrawalAmountIndex = -1
    open var depositAmountIndex = -1
    open var transactionModeIndex: Int = -1
    open var timeStampIndex = -1
    open var bookNameIndex: Int = -1

    private fun getMatchingCategoryId(context: Context, category: String, note: String): String? {
        val categoryRepository = CategoryRepositoryImpl(context)
        return if (category.isNotEmpty()) {
            categoryRepository.getCategoryByName(category)?.id
        } else {
            val categoryName = findCategoryByValue(note)?.key.orEmpty()
            val categoryId = categoryRepository.getCategoryByName(categoryName)?.id
            return categoryId
        }
    }

    private fun findCategoryByValue(category: String) =
        getCategoryMapping().asIterable().firstOrNull {
            category.contains(*(it.value.toTypedArray()))
        }

    fun initialiseIndex(strings: CSVRow) {
        strings.values.forEachIndexed { index, item ->
            when {
                item.contains("Note", "Narration") -> noteIndex = index
                item.contains("amount", "amt") -> amountIndex = index
                item.contains("Date", "time") -> transactionDateIndex = index
                item.contains("category", "group") -> categoryIdIndex = index
                item.contains("Withdrawal") -> withdrawalAmountIndex = index
                item.contains("Deposit") -> depositAmountIndex = index
                item.contains("mode", "type") -> transactionModeIndex = index
                item.contains("timestamp") -> timeStampIndex = index
                item.contains("book", "diary", "ledger", "journal", "register") -> bookNameIndex =
                    index
            }
        }
    }

}