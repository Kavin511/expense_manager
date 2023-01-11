package com.devstudio.transactions.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.devstudio.expensemanager.db.models.Transactions
import com.devstudio.transactions.viewmodel.TransactionViewModel
import com.devstudio.utils.formatters.DateFormatter
import com.devstudioworks.ui.theme.appColors


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionItem(transaction: Transactions) {
    val blockColor = if (transaction.transactionMode != "EXPENSE") {
        appColors.transactionIncomeColor
    } else {
        appColors.transactionExpenseColor
    }
    Card(modifier = Modifier.padding(4.dp)) {
        val context = LocalContext.current
        val transactionViewModel: TransactionViewModel = viewModel()
        Row(
            modifier = Modifier
                .fillMaxWidth(1F)
                .padding(horizontal = 4.dp)
                .combinedClickable(onClick = {
                    editTransaction(context, transaction)
                }, onLongClick = {
                    showTransactionLongPressOptions(context, transaction, transactionViewModel)
                })
                .background(blockColor)
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.fillMaxWidth(.5f), horizontalAlignment = Alignment.Start) {
                Text(text = transaction.amount.toString(), color = appColors.material.onPrimaryContainer)
                Text(text = transaction.category, color = appColors.material.onPrimaryContainer)
            }
            Column(modifier = Modifier.fillMaxWidth(1f), horizontalAlignment = Alignment.End) {
                Text(
                    text = DateFormatter().convertLongToDate(transaction.transactionDate.toLong()),
                    color = appColors.material.onPrimaryContainer
                )
                Text(
                    text = transaction.note,
                    color = appColors.material.onPrimaryContainer,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
            }
        }
    }

}
