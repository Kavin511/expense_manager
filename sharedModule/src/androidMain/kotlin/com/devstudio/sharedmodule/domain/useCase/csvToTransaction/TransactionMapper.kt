package com.devstudio.sharedmodule.domain.useCase.csvToTransaction

import android.content.Context
import com.devstudio.data.datastore.DataSourceModule
import com.devstudio.data.repository.BooksRepositoryImpl
import com.devstudio.data.repository.CategoryRepositoryImpl
import com.devstudio.data.repository.UserDataRepositoryImpl
import com.devstudio.expensemanager.db.models.Category
import com.devstudio.expensemanager.db.models.Transaction
import com.devstudio.sharedmodule.domain.useCase.util.contains
import com.devstudio.sharedmodule.domain.useCase.util.getCategoryMapping
import com.devstudio.sharedmodule.domain.useCase.util.parseDateToTimestamp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last

abstract class TransactionMapper(open val transactions: List<List<String>>) {
    open fun getNote(row: List<String>): String {
        return row[noteIndex]
    }


    open fun getAmount(row: List<String>): Double {
        return row[amountIndex].toDoubleOrNull() ?: 0.0
    }

    open fun categoryId(context: Context, row: List<String>): String {
        if (categoryIdIndex == -1) {
            return ""
        }
        val matchingCategoryId = getMatchingCategoryId(context, row[categoryIdIndex], getNote(row))
            ?: return insertOrGetDefaultCategory(row, context)
        return matchingCategoryId
    }

    private fun insertOrGetDefaultCategory(
        row: List<String>, context: Context
    ): String {
        if (categoryIdIndex != -1) {
            val category =
                Category(name = row[categoryIdIndex], categoryType = transactionMode(row))
            CategoryRepositoryImpl(context).insertCategory(category)
            return category.id
        } else {
            return CategoryRepositoryImpl(context).getCategoryByName("Others")?.id.orEmpty()
        }
    }

    abstract fun transactionMode(row: List<String>): String
    open fun transactionDate(row: List<String>): String {
        return parseDateToTimestamp(row[transactionDateIndex]).toString()
    }

    suspend fun invoke(context: Context): List<Transaction> {
        val result = mutableListOf<Transaction>()
        if (transactions.isEmpty()) {
            return result
        }
        transactions.subList(1, transactions.size).forEach { row ->
            try {
                Transaction().apply {
                    this.bookId = getBookId(context, row)
                    this.note = getNote(row)
                    this.amount = getAmount(row)
                    this.categoryId = categoryId(context, row)
                    this.transactionMode = transactionMode(row)
                    this.transactionDate = transactionDate(row)
                }.also { result.add(it) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return result
    }

    private suspend fun getBookId(context: Context, row: List<String>): Long {
        val userDataRepository = UserDataRepositoryImpl(DataSourceModule(context))
        val booksRepositoryImpl = BooksRepositoryImpl(context, userDataRepository)
        if (bookNameIndex == -1) {
            return userDataRepository.getSelectedBookId().last()
        }
        return (booksRepositoryImpl.findBookByName(row[bookNameIndex])?.id)
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

    fun initialiseIndex(strings: List<String>) {
        strings.forEachIndexed { index, item ->
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