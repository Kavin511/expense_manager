package com.devstudio.expensemanager.ui.home.composables.transactionList

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.devstudio.expensemanager.R
import com.devstudio.expensemanager.db.models.Transactions
import com.devstudio.expensemanager.ui.viewmodel.HomeViewModel
import com.devstudio.utils.DateFormatter
import com.devstudioworks.uiComponents.theme.appColors
import editTransaction
import showTransactionLongPressOptions


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
