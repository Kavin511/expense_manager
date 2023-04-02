package com.devstudio.transactions.composables.transactionList

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.devstudio.expensemanager.db.models.Transactions
import com.devstudio.transactions.viewmodel.TransactionViewModel
import com.devstudio.utils.formatters.DateFormatter
import com.devstudioworks.ui.theme.appColors


@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun TransactionItem(transaction: Transactions = Transactions(transactionDate = "1000")) {
    val blockColor = if (transaction.transactionMode != "EXPENSE") {
        appColors.transactionIncomeColor
    } else {
        appColors.transactionExpenseColor
    }
    val context = LocalContext.current
    val transactionViewModel: TransactionViewModel = viewModel()
    ElevatedCard(
        modifier = Modifier
            .padding(2.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.elevatedCardElevation(2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(1F)
                .combinedClickable(onClick = {
                    editTransaction(context, transaction)
                }, onLongClick = {
                    showTransactionLongPressOptions(context, transaction, transactionViewModel)
                })
                .background(color = Color.White)
                .padding(horizontal = 4.dp)
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(color = blockColor)
                )
            }
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = transaction.category,
                    color = appColors.material.onPrimaryContainer,
                    fontSize = 16.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = DateFormatter().convertLongToDate(transaction.transactionDate.toLong()),
                        color = appColors.material.onPrimaryContainer,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(end = 5.dp)
                    )
                    if (transaction.note.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .size(3.dp)
                                .clip(CircleShape)
                                .background(color = appColors.material.onSurfaceVariant)
                        )
                        Text(
                            text = transaction.note,
                            color = appColors.material.onSecondaryContainer,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .padding(horizontal = 5.dp)
                                .align(alignment = Alignment.CenterVertically)
                        )
                    }
                }

            }
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = transaction.amount.toString(),
                    color = appColors.material.onPrimaryContainer,
                )
            }
        }
    }

}