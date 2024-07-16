package com.devstudio.sharedmodule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.devstudio.database.models.Transaction
import com.devstudio.sharedmodule.model.TransactionMapResult

@Composable
fun TransactionImportSuccessScreen(
    transactionMapResult: TransactionMapResult,
    onDone: () -> Unit,
    onModifyTransaction: (Transaction) -> Unit
) {
    var showUnimportedList by remember { mutableStateOf(false) }
    val totalImported = transactionMapResult.transactions.size
    val failedCount = transactionMapResult.conflictTransactions.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Import Successful",
            style = MaterialTheme.typography.headlineMedium
        )

        Text("Total transactions imported: $totalImported")
        Text("Failed transactions: $failedCount")

        if (failedCount > 0) {
            Button(onClick = { showUnimportedList = !showUnimportedList }) {
                Text("Show Unimported Transactions")
            }
        }

        if (showUnimportedList) {
            LazyColumn {
                items(transactionMapResult.conflictTransactions) { transaction ->
                    TransactionItem(
                        transaction = transaction.transaction,
                        onModify = { onModifyTransaction(transaction.transaction) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(onClick = onDone) {
            Text("Done")
        }
    }
}

@Composable
fun TransactionItem(
    transaction: Transaction,
    onModify: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = transaction.transactionMode)
            Text(text = transaction.amount.toString(), style = MaterialTheme.typography.titleMedium)
        }
        IconButton(onClick = onModify) {
            Icon(Icons.Default.Edit, contentDescription = "Modify")
        }
    }
}

