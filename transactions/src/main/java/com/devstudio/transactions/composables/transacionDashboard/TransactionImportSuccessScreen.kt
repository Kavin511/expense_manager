package com.devstudio.transactions.composables.transacionDashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devstudio.database.models.Transaction
import com.devstudio.sharedmodule.model.TransactionMapResult
import com.devstudio.sharedmodule.model.TransactionWithIndex
import com.devstudio.designSystem.appColors

/**
 * @Author: Kavin
 * @Date: 17/07/24
 */
fun getSampleMap(): TransactionMapResult {
    return TransactionMapResult(
        listOf(
            TransactionWithIndex(
                0, Transaction()
            ),
            TransactionWithIndex(
                0, Transaction()
            ),
            TransactionWithIndex(
                0, Transaction()
            ),
            TransactionWithIndex(
                0, Transaction()
            ),
            TransactionWithIndex(
                0, Transaction()
            ),
            TransactionWithIndex(
                0, Transaction()
            ),
        ), listOf(
            TransactionWithIndex(
                0, Transaction()
            ),
            TransactionWithIndex(
                0, Transaction()
            ),
            TransactionWithIndex(
                0, Transaction()
            ),
            TransactionWithIndex(
                0, Transaction()
            ),
            TransactionWithIndex(
                0, Transaction()
            ),
            TransactionWithIndex(
                0, Transaction()
            ),
            TransactionWithIndex(
                0, Transaction()
            ),
            TransactionWithIndex(
                0, Transaction()
            ),
            TransactionWithIndex(
                0, Transaction()
            ),
            TransactionWithIndex(
                0, Transaction()
            ),
        )
    )
}

@Preview(device = "id:resizable")
@Composable
fun TransactionImportSuccessScreen(
    transactionMapResult: TransactionMapResult = getSampleMap(),
    onDone: () -> Unit = {},
    onModifyTransaction: (Transaction) -> Unit = {}
) {

    Column(
        modifier = Modifier
            .background(appColors.material.onPrimary)
            .padding(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SuccessHeader()
        Content(
            transactionMapResult, modifier = Modifier.wrapContentHeight(), onModifyTransaction
        )
    }

}

@Composable
private fun Content(
    transactionMapResult: TransactionMapResult,
    modifier: Modifier = Modifier,
    onModifyTransaction: (Transaction) -> Unit
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        val failedCount = transactionMapResult.conflictTransactions.size
        ImportSummary(transactionMapResult, failedCount)
        if (failedCount > 0) {
            DefaultDivider()
            UnImportedTransactionList(transactionMapResult, onModifyTransaction)
        }
    }
}

@Composable
private fun DefaultDivider() {
    HorizontalDivider(
        thickness = 5.dp
    )
}

@Composable
private fun ImportSummary(
    transactionMapResult: TransactionMapResult, failedCount: Int
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SubHeading("Import Summary")
        LabelAndValueRow("Transactions imported", transactionMapResult.transactions.size.toString())
        LabelAndValueRow(
            "Failed transactions",
            "$failedCount",
            color = appColors.material.error,
        )
    }

}

@Composable
private fun UnImportedTransactionList(
    transactionMapResult: TransactionMapResult, onModifyTransaction: (Transaction) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        var showUnimportedList by remember { mutableStateOf(true) }
        val failedCount = transactionMapResult.conflictTransactions.size
        if (failedCount > 0) {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showUnimportedList = !showUnimportedList
                    }) {
                SubHeading("Conflict Transactions")
                Icon(Icons.Outlined.KeyboardArrowDown, "", modifier = Modifier.padding(0.dp))
            }
        }

        if (showUnimportedList) {
            LazyColumn {
                items(transactionMapResult.conflictTransactions) { transaction ->
                    TransactionItem(transaction = transaction, onModifyTransaction)
                }
            }
        }
    }
}

@Composable
private fun SubHeading(text: String) {
    Text(
        text, fontSize = 16.sp, style = MaterialTheme.typography.labelMedium
    )
}

@Composable
private fun LabelAndValueRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    color: Color = appColors.material.onPrimaryContainer,
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        RegularText(label)
        RegularText(value, color = color)
    }
}

@Composable
private fun ColumnScope.SuccessHeader() {
    Column(
        modifier = Modifier
            .background(appColors.material.surfaceBright)
            .fillMaxWidth()
            .padding(16.dp)
            .align(Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Image(
            Icons.Filled.Check,
            contentDescription = "",
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        RegularText(
            text = "Import Completed!",
            style = MaterialTheme.typography.labelLarge,
            color = appColors.material.onSurface,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun TransactionItem(
    transaction: TransactionWithIndex, onModifyTransaction: (Transaction) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier
            .padding(top = 8.dp)
            .clickable {
                onModifyTransaction.invoke(transaction.transaction)
            }
            .background(appColors.material.errorContainer)
            .padding(8.dp)) {
            LabelAndValueRow("Sheet Row No: " + transaction.index,
                "Date: " + transaction.transaction.transactionDate.ifBlank { "-" })
            LabelAndValueRow("Mode: " + transaction.transaction.transactionMode.ifBlank { "-" },
                "Amount: " + transaction.transaction.amount.toString().ifBlank { "-" })
        }
    }
}

@Composable
private fun RegularText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = appColors.material.onPrimaryContainer,
    style: TextStyle = MaterialTheme.typography.bodyMedium
) {
    Text(text = text, fontSize = 16.sp, modifier = modifier, color = color, style = style)
}
