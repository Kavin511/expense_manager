package com.devstudio.expensemanager.ui.home

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.devstudio.expensemanager.R
import com.devstudio.expensemanager.db.models.Transactions
import com.devstudio.expensemanager.ui.transaction.TransactionActivity
import com.devstudio.utils.DateFormatter
import com.google.android.material.dialog.MaterialAlertDialogBuilder

@Composable
fun TransactionsList(
    paddingValues: PaddingValues,
    homeViewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val transactions: List<Transactions> =
        homeViewModel.getTransactions().observeAsState().value ?: emptyList()
    LazyColumn(
        modifier = Modifier.padding(top = paddingValues.calculateTopPadding()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(items = transactions) {
            Row {
                TransactionItem(transaction = it)
            }
        }
    }

}

@Composable
fun onClickAddTransaction(context: Context): () -> Unit = {
    val intent = Intent(context, TransactionActivity::class.java)
    context.startActivity(intent)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionItem(transaction: Transactions) {
    val blockColor = if (transaction.transactionMode != "EXPENSE") {
        Color(0XFFE7FBE8)
    } else {
        Color(0XFFFCEEED)
    }
    Card(
        modifier = Modifier
            .padding(4.dp)
            .background(blockColor),
        shape = AbsoluteCutCornerShape(4.dp),
    ) {
        val context = LocalContext.current
        val homeViewModel: HomeViewModel = viewModel()
        Row(
            modifier = Modifier
                .fillMaxWidth(1F)
                .padding(horizontal = dimensionResource(id = R.dimen.default_padding))
                .combinedClickable(onClick = {
                    editTransaction(context, transaction)
                }, onLongClick = {
                    showTransactionLongPressOptions(context, transaction, homeViewModel)
                })
                .background(blockColor)
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column() {
                Text(text = transaction.amount.toString())
                Text(text = transaction.category)
            }
            Column {
                Text(text = DateFormatter().convertLongToDate(transaction.transactionDate.toLong()))
                Text(text = transaction.note)
            }
        }
    }

}

private fun showTransactionLongPressOptions(
    context: Context,
    it: Transactions,
    homeViewModel: HomeViewModel
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

private fun editTransaction(context: Context, transaction: Transactions) {
    val intent = Intent(context, TransactionActivity::class.java).apply {
        putExtra("id", transaction.id)
    }
    context.startActivity(intent)
}
