package com.devstudio.transactions.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devstudio.core_data.repository.CategoryRepository
import com.devstudio.core_data.repository.TransactionsRepository
import com.devstudio.core_data.repository.UserDataRepository
import com.devstudio.data.model.TransactionFilterType
import com.devstudio.expensemanager.db.models.Category
import com.devstudio.expensemanager.db.models.Transaction
import com.devstudio.expensemanager.db.models.TransactionMode
import com.devstudio.transactions.models.FilterItem
import com.devstudio.transactions.models.FuturePaymentStatus
import com.devstudio.transactions.models.TransactionUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionsRepository: TransactionsRepository,
    private val categoryRepository: CategoryRepository,
    getTransactionBook: GetTransactionBook,
    private val userDataRepository: UserDataRepository
) : ViewModel() {
    val futurePaymentModeStatus = FuturePaymentStatus(isDebit = false, isCredit = false)
    var filterItemOptions: List<FilterItem>
    val transaction = MutableStateFlow<Transaction?>(null)
    var uiState: StateFlow<TransactionUiState> =
        getTransactionBook().map {
            TransactionUiState.Success(it)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = TransactionUiState.Loading,
        )

    init {
        filterItemOptions = listOf(
            FilterItem(
                id = SHOW_ALL_ID,
                SHOW_ALL,
                additionalData = null,
                filterType = TransactionFilterType.ALL
            ), FilterItem(
                id = DATE_RANGE_ID,
                DATE_RANGE,
                additionalData = null,
                filterType = TransactionFilterType.DATE_RANGE(Pair(0L, 0L))
            )
        )
    }

    var transactionType = MutableStateFlow(TransactionMode.EXPENSE)
    var isEditingOldTransaction = MutableLiveData<Boolean>()

    suspend fun upsertTransaction(transaction: Transaction) {
        transactionsRepository.upsertTransaction(transaction)
    }

    suspend fun getAndUpdateTransactionById(id: Long) {
        transaction.value =
            transactionsRepository.findTransactionById(id)
                .also { isEditingOldTransaction.value = it != null }
    }

    fun getTransactionSummaryDetails(transactions: List<Transaction>): Pair<Double, Double> {
        var totalExpense = 0.0
        var totalIncome = 0.0
        viewModelScope.launch {
            transactions.forEach {
                if (it.transactionMode != INCOME) {
                    totalExpense += it.amount
                } else {
                    totalIncome += it.amount
                }
            }
        }
        return Pair(totalIncome, totalExpense)
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionsRepository.deleteTransactions(transaction)
        }
    }

    fun isHavingTransactions(): Boolean {
        return transactionsRepository.getTotalTransactionCount() > 0
    }

    fun isCurrentMonthHavingTransactions(): Boolean {
        return transactionsRepository.getCurrentMonthTransactionCount() > 0
    }

    fun updateSelectedTransactionFilter(filterItem: TransactionFilterType) {
        viewModelScope.launch {
            userDataRepository.updateTransactionFilter(filterItem)
        }
    }

    fun getTransactionCategoryName(categoryId: String, result: (String) -> Unit) {
        viewModelScope.launch {
            result.invoke(
                transactionsRepository.getTransactionCategoryName(categoryId) ?: "Category Deleted"
            )
        }
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