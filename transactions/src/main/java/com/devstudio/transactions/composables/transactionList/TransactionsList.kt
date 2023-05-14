package com.devstudio.transactions.composables.transactionList

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.devstudio.expensemanager.db.models.Transaction
import com.devstudio.transactions.acivity.TransactionActivity
import com.devstudio.transactions.composables.transacionDashboard.TransactionOptions
import com.devstudio.transactions.viewmodel.TransactionViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsList(filterBottomSheetScaffoldState: BottomSheetScaffoldState) {
    val transactionViewModel = hiltViewModel<TransactionViewModel>()
    val transactions = transactionViewModel.transactions.collectAsState()
    when {
        transactionViewModel.isHavingTransactions() && transactions.value.isEmpty() -> {
            TransactionOptions(filterBottomSheetScaffoldState)
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
                    TransactionOptions(filterBottomSheetScaffoldState)
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
