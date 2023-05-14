package com.devstudio.transactions.composables.transactionList

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.hilt.navigation.compose.hiltViewModel
import com.devstudio.expensemanager.db.models.Transaction
import com.devstudio.transactions.acivity.TransactionActivity
import com.devstudio.transactions.viewmodel.TransactionViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

@Composable
fun TransactionsList() {
    val transactionViewModel = hiltViewModel<TransactionViewModel>()
    val transactions = transactionViewModel.transactions.collectAsState()
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(transactions.value) {
            TransactionItem(transaction = it)
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
