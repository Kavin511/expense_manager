package com.devstudio.expensemanager.ui.home.composables

import HomeAppBar
import TransactionSummary
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.devstudio.transactions.acivity.TransactionActivity
import com.devstudio.transactions.composables.transactionList.TransactionsList
import com.devstudio.transactions.viewmodel.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDashboard(transactionViewModel:TransactionViewModel = viewModel()) {
    Scaffold(
        floatingActionButton = {
            AddTransactions()
        },
        topBar = {
            HomeAppBar()
        },
    ) {
        Column(modifier = Modifier.padding(it)) {
            TransactionSummary()
            TransactionsList(transactionViewModel)
        }
    }
}

@Composable
private fun AddTransactions() {
    val context = LocalContext.current
    FloatingActionButton(
        onClick = onClickAddTransaction(context),
        modifier = Modifier.padding(start = 6.dp, end = 6.dp, top = 4.dp, bottom = 4.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = stringResource(com.devstudioworks.core.ui.R.string.add_transaction)
        )
    }
}


@Composable
fun onClickAddTransaction(context: Context): () -> Unit = {
    val intent = Intent(context, TransactionActivity::class.java)
    context.startActivity(intent)
}
