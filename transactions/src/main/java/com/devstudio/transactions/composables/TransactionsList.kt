package com.devstudio.transactions.composables

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.devstudio.expensemanager.db.models.Transactions
import com.devstudio.transactions.acivity.TransactionActivity
import com.devstudio.transactions.viewmodel.TransactionViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

@Composable
fun TransactionsList(transactionViewModel: TransactionViewModel = viewModel()) {
    val transactions: List<Transactions> =
        transactionViewModel.getTransactions().collectAsState(initial = emptyList()).value
    LazyColumn(
        modifier = Modifier.padding(4.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(items = transactions) {
            Row {
                TransactionItem(transaction = it)
            }
        }
    }

}

fun showTransactionLongPressOptions(
    context: Context, it: Transactions, homeViewModel: TransactionViewModel
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

fun editTransaction(context: Context, transaction: Transactions) {
    val intent = Intent(context, TransactionActivity::class.java).apply {
        putExtra("id", transaction.id)
    }
    context.startActivity(intent)
}
