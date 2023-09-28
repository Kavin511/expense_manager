package com.devstudio.transactions.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.core.util.Pair
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devstudio.core_data.repository.CategoryRepository
import com.devstudio.core_data.repository.TransactionsRepository
import com.devstudio.expensemanager.db.models.Category
import com.devstudio.expensemanager.db.models.Transaction
import com.devstudio.expensemanager.db.models.TransactionMode
import com.devstudio.transactions.models.FuturePaymentStatus
import com.devstudio.transactions.models.ListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val repository: TransactionsRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    val futurePaymentModeStatus = FuturePaymentStatus(isDebit = false, isCredit = false)
    var listItemOptions: List<ListItem>
    val selectedListItem = MutableStateFlow<ListItem?>(null)
    var transactions = MutableStateFlow<List<Transaction>>(listOf())
    val transaction = MutableStateFlow<Transaction?>(null)
    val sumOfExpense = MutableStateFlow<Double>(0.0)
    val sumOfIncome = MutableStateFlow<Double>(0.0)

    init {
        listItemOptions = listOf(
            ListItem(
                id = SHOW_ALL_ID,
                SHOW_ALL,
                additionalData = null,
                filterType = TransactionFilterType.ALL
            ), ListItem(
                id = DATE_RANGE_ID,
                DATE_RANGE,
                additionalData = null,
                filterType = TransactionFilterType.DATE_RANGE
            )
        )
        updateSelectedTransactionFilter(null)
    }

    var transactionType = MutableStateFlow(TransactionMode.EXPENSE)
    var isEditingOldTransaction = MutableLiveData<Boolean>()

    suspend fun upsertTransaction(transaction: Transaction) {
        repository.upsertTransaction(transaction)
    }

    suspend fun getAndUpdateTransactionById(id: Long) {
        transaction.value =
            repository.findTransactionById(id).also { isEditingOldTransaction.value = it != null }
    }

    private fun validateAndFilterTransactions(listItem: ListItem?): Flow<List<Transaction>?> {
        return when {
            listItem?.id == SHOW_ALL_ID -> {
                repository.allTransactionsStream()
            }

            listItem?.id == DATE_RANGE_ID && (listItem.additionalData is Pair<*, *>) -> {
                repository.filterTransactionFromDateRange(listItem.additionalData as Pair<Long, Long>)
            }

            else -> {
                getTransactionsForCurrentMonth()
            }
        }
    }

    private fun getTransactionsForCurrentMonth(): Flow<List<Transaction>> {
        return repository.getTransactionsForCurrentMonth()
    }

    suspend fun updateTransaction(oldTransactionObject: Transaction) {
        repository.updateTransaction(oldTransactionObject)
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransactions(transaction)
        }
    }

    fun isHavingTransactions(): Boolean {
        return repository.getTotalTransactionCount() > 0
    }

    fun isCurrentMonthHavingTransactions(): Boolean {
        return repository.getCurrentMonthTransactionCount() > 0
    }

    fun updateSelectedTransactionFilter(listItem: ListItem?) {
        selectedListItem.value = listItem
        viewModelScope.launch {
            validateAndFilterTransactions(listItem).collect { transactionList ->
                transactions.value = transactionList ?: emptyList()
                var totalExpense = 0.0
                var totalIncome = 0.0
                transactionList?.forEach {
                    if (it.transactionMode != INCOME) {
                        totalExpense += it.amount
                    } else {
                        totalIncome += it.amount
                    }
                }
                sumOfExpense.value = totalExpense
                sumOfIncome.value = totalIncome
            }
        }
    }

    fun getTransactionCategoryName(categoryId: String): String {
        return repository.getTransactionCategoryName(categoryId) ?: "Category Deleted"
    }

    fun getCategories(type: String): Flow<List<Category>> {
        return categoryRepository.getCategoriesStream(type)
    }

    companion object Factory {
        const val SHOW_ALL_ID = 1000L
        const val DATE_RANGE_ID = 1001L
        const val DATE_RANGE = "Date Range"
        const val SHOW_ALL = "Show All"
        const val INCOME = "INCOME"
    }
}

enum class DateSelectionStatus {
    SELECTED, CANCELED
}

open class Type
sealed class TransactionFilterType : Type() {
    object DATE_RANGE : TransactionFilterType()
    object ALL : TransactionFilterType()
}