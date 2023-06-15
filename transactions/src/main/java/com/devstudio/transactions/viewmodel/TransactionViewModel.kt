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
import com.devstudio.transactions.models.TransactionFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(private val repository: TransactionsRepository,private val categoryRepository: CategoryRepository) :
    ViewModel() {
    var transactionFilterOptions: List<TransactionFilter>
    val selectedTransactionFilter = MutableStateFlow<TransactionFilter?>(null)
    val datePickerState = mutableStateOf(false)
    var transactions = MutableStateFlow<List<Transaction>>(listOf())
    val transaction = MutableStateFlow<Transaction?>(null)
    val sumOfExpense =
        MutableStateFlow<Double>(0.0)
    val sumOfIncome =
        MutableStateFlow<Double>(0.0)

    init {
        transactionFilterOptions =
            listOf(TransactionFilter(id = SHOW_ALL_ID, SHOW_ALL, additionalData = null) {
                updateSelectedTransactionFilter(this@TransactionFilter)
            }, TransactionFilter(id = DATE_RANGE_ID, DATE_RANGE, additionalData = null) {
                datePickerState.value = true
            })
        updateSelectedTransactionFilter(null)
    }

    var transactionType = MutableStateFlow(TransactionMode.EXPENSE)
    var isEditingOldTransaction = MutableLiveData<Boolean>()

    suspend fun insertTransaction(transaction: Transaction) {
        repository.insert(transaction)
    }

    suspend fun getAndUpdateTransactionById(id: Long) {
        transaction.value =
            repository.findTransactionById(id).also { isEditingOldTransaction.value = it != null }
    }

    private fun validateAndFilterTransactions(transactionFilter: TransactionFilter?): Flow<List<Transaction>?> {
        return when {
            transactionFilter?.id == SHOW_ALL_ID -> {
                repository.allTransactionsStream()
            }

            transactionFilter?.id == DATE_RANGE_ID && (transactionFilter.additionalData is Pair<*, *>) -> {
                repository.filterTransactionFromDateRange(transactionFilter.additionalData as Pair<Long, Long>)
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

    fun updateSelectedTransactionFilter(transactionFilter: TransactionFilter?) {
        selectedTransactionFilter.value = transactionFilter
        viewModelScope.launch {
            validateAndFilterTransactions(transactionFilter).collect { transactionList ->
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