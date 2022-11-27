package com.devstudio.expensemanager.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.devstudio.expensemanager.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home() {
    Scaffold(
        floatingActionButton = {
            AddTransactions()
        },
        topBar = { TopAppBar(title = { Text(stringResource(id = R.string.app_name)) }) },
        modifier = Modifier.padding()
    ) {
        TransactionsList(it)
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