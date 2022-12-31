package com.devstudio.expensemanager.ui.home.composables

import HomeAppBar
import TransactionSummary
import TransactionsList
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
import com.devstudio.expensemanager.R
import com.devstudio.expensemanager.ui.transaction.acivity.TransactionActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home() {
    Scaffold(
        floatingActionButton = {
            AddTransactions()
        },
        topBar = {
            HomeAppBar()
        },
        modifier = Modifier.padding()
    ) {
        Column {
            TransactionSummary(it)
            TransactionsList()
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
            contentDescription = stringResource(R.string.add_transaction)
        )
    }
}


@Composable
fun onClickAddTransaction(context: Context): () -> Unit = {
    val intent = Intent(context, TransactionActivity::class.java)
    context.startActivity(intent)
}
