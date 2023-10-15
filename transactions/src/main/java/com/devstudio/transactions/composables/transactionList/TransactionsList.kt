package com.devstudio.transactions.composables.transactionList

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.core.util.Pair
import androidx.fragment.app.FragmentManager
import androidx.hilt.navigation.compose.hiltViewModel
import com.devstudio.expensemanager.db.models.Transaction
import com.devstudio.transactions.acivity.TransactionActivity
import com.devstudio.transactions.composables.transacionDashboard.showDateRangePicker
import com.devstudio.transactions.models.DateSelectionStatus
import com.devstudio.data.model.TransactionFilterType.ALL
import com.devstudio.data.model.TransactionFilterType.DATE_RANGE
import com.devstudio.transactions.models.FilterItem
import com.devstudio.transactions.viewmodel.TransactionBook
import com.devstudio.transactions.viewmodel.TransactionViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Calendar

@Composable
fun TransactionsList(transactionsStream: TransactionBook) {
    val transactions = transactionsStream.transactions.collectAsState(initial = listOf()).value
    val transactionViewModel = hiltViewModel<TransactionViewModel>()
    when {
        transactionViewModel.isCurrentMonthHavingTransactions()
            .not() && transactions.isEmpty() && transactionViewModel.isHavingTransactions() -> {
            Box(modifier = Modifier.fillMaxSize(1f), contentAlignment = Alignment.Center) {
                Text(
                    text = "No transactions for current month, old transactions can be viewed through applying filter",
                    modifier = Modifier.align(alignment = Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }
        }

        transactionViewModel.isHavingTransactions() && transactions.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize(1f),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "No transactions found for the applied filter",
                    modifier = Modifier.align(alignment = Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }
        }

        transactionViewModel.isHavingTransactions().not() && transactions.isEmpty() -> {
            Box(modifier = Modifier.fillMaxSize(1f), contentAlignment = Alignment.Center) {
                Text(
                    text = "No transactions found, Click + to add transactions",
                    modifier = Modifier.align(alignment = Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }
        }

        else -> {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(transactions) {
                    TransactionItem(transaction = it)
                }
            }
        }
    }
}

fun applySelectedFilter(
    it: FilterItem?,
    transactionViewModel: TransactionViewModel,
    fragmentManager: FragmentManager
) {
    when (it?.filterType) {
        ALL -> {
            transactionViewModel.updateSelectedTransactionFilter(it.filterType)
        }

        is DATE_RANGE -> {
            selectDateRangeAndApplyFilter(
                it,
                fragmentManager
            ) { dateSelectionStatus ->
                when (dateSelectionStatus) {
                    is DateSelectionStatus.SELECTED -> {
                        var dataFilter = DATE_RANGE(
                            additionalData = kotlin.Pair(
                                dateSelectionStatus.selectedRange.first,
                                dateSelectionStatus.selectedRange.second
                            )
                        )
                        transactionViewModel.updateSelectedTransactionFilter(dataFilter)
                    }

                    else -> {}
                }
            }
        }

        else -> {}
    }
}

private fun selectDateRangeAndApplyFilter(
    filterItem: FilterItem,
    fragmentManager: FragmentManager,
    function: (DateSelectionStatus) -> Unit
) {
    val previouslySelectedFilter = filterItem.filterType
    var dateSelectionRange: Pair<Long, Long>? = null
    if (previouslySelectedFilter is DATE_RANGE) {
        dateSelectionRange = Pair(
            previouslySelectedFilter.additionalData.first.isNotZeroOrDefault(),
            previouslySelectedFilter.additionalData.second.isNotZeroOrDefault()
        )
    }
    showDateRangePicker(
        fragmentManager = fragmentManager, dateSelectionRange = dateSelectionRange
    ) {
        function.invoke(it)
    }
}

inline fun Long.isNotZeroOrDefault(): Long? {
    return if (this == 0L) {
        Calendar.getInstance().timeInMillis
    } else {
        this
    }
}

fun showTransactionLongPressOptions(
    context: Context, it: Transaction, homeViewModel: TransactionViewModel
) {
    val builder = MaterialAlertDialogBuilder(context)
    val options = arrayOf("Edit Transaction", "Delete Transaction")
    builder.setItems(options) { dialog, which ->
        if (which == 0) {
            editTransaction(context, it)
        } else if (which == 1) {
            homeViewModel.deleteTransaction(it)
        }
        dialog.dismiss()
    }
    builder.show()
}

fun editTransaction(context: Context, transaction: Transaction) {
    val intent = Intent(context, TransactionActivity::class.java).apply {
        putExtra("id", transaction.id)
    }
    context.startActivity(intent)
}
