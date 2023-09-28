package com.devstudio.transactions.composables.transactionList

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.core.util.Pair
import androidx.fragment.app.FragmentManager
import androidx.hilt.navigation.compose.hiltViewModel
import com.devstudio.expensemanager.db.models.Transaction
import com.devstudio.transactions.acivity.TransactionActivity
import com.devstudio.transactions.composables.transacionDashboard.TransactionOptions
import com.devstudio.transactions.composables.transacionDashboard.showDateRangePicker
import com.devstudio.transactions.composables.transactionFilter.TransactionFilterBottomSheet
import com.devstudio.transactions.models.DateSelectionStatus
import com.devstudio.transactions.viewmodel.TransactionFilterType.ALL
import com.devstudio.transactions.viewmodel.TransactionFilterType.DATE_RANGE
import com.devstudio.transactions.viewmodel.TransactionViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsList() {
    val coroutineScope = rememberCoroutineScope()
    val transactionViewModel = hiltViewModel<TransactionViewModel>()
    val transactions = transactionViewModel.transactions.collectAsState()
    val filterBottomSheetState: SheetState = rememberModalBottomSheetState()
    val fragmentManager = (LocalContext.current as AppCompatActivity).supportFragmentManager
    if (filterBottomSheetState.currentValue.equals(SheetValue.Expanded)) {
        TransactionFilterBottomSheet(filterBottomSheetState) {
            coroutineScope.launch {
                filterBottomSheetState.hide()
                when (it?.filterType) {
                    ALL -> {
                        transactionViewModel.updateSelectedTransactionFilter(it)
                    }

                    DATE_RANGE -> {
                        selectDateRangeAndApplyFilter(transactionViewModel, fragmentManager)
                    }

                    else -> {}
                }
            }
        }
    }
    when {
        transactionViewModel.isCurrentMonthHavingTransactions()
            .not() && transactions.value.isEmpty() && transactionViewModel.isHavingTransactions() -> {
            TransactionOptions {
                coroutineScope.launch {
                    filterBottomSheetState.show()
                }
            }
            Box(modifier = Modifier.fillMaxSize(1f), contentAlignment = Alignment.Center) {
                Text(
                    text = "No transactions for current month, old transactions can be viewed through applying filter",
                    modifier = Modifier.align(alignment = Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }
        }

        transactionViewModel.isHavingTransactions() && transactions.value.isEmpty() -> {
            TransactionOptions {
                coroutineScope.launch {
                    filterBottomSheetState.show()
                }
            }
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

        transactionViewModel.isHavingTransactions().not() && transactions.value.isEmpty() -> {
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
                item {
                    TransactionOptions {
                        coroutineScope.launch {
                            filterBottomSheetState.show()
                        }
                    }
                }
                transactions.value.also { transactionList ->
                    items(transactionList) {
                        TransactionItem(transaction = it)
                    }
                }
            }
        }
    }
}

private fun selectDateRangeAndApplyFilter(
    transactionViewModel: TransactionViewModel,
    fragmentManager: FragmentManager
) {
    val transactionFilterState = transactionViewModel.selectedListItem
    var dateSelectionRange: Pair<Long, Long>? = null
    if (transactionFilterState.value?.id?.equals(TransactionViewModel.DATE_RANGE_ID) == true && transactionFilterState.value?.additionalData != null) {
        dateSelectionRange =
            (transactionFilterState.value!!.additionalData as Pair<Long, Long>)
    }
    showDateRangePicker(
        fragmentManager = fragmentManager,
        dateSelectionRange = dateSelectionRange
    ) { dateSelectionStatus ->
        when (dateSelectionStatus) {
            is DateSelectionStatus.SELECTED -> {
                val transactionFilter = transactionViewModel.listItemOptions[1]
                transactionFilter.additionalData = dateSelectionStatus.selectedRange
                transactionViewModel.updateSelectedTransactionFilter(transactionFilter)
            }

            else -> {}
        }
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
