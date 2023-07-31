package com.devstudio.transactions.composables.transactionList

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devstudio.expensemanager.db.models.Transaction
import com.devstudio.transactions.acivity.PaymentStatus
import com.devstudio.transactions.viewmodel.TransactionViewModel
import com.devstudio.utils.formatters.DateFormatter
import com.devstudioworks.ui.theme.DEFAULT_CARD_CORNER_RADIUS
import com.devstudioworks.ui.theme.DEFAULT_CARD_ELEVATION
import com.devstudioworks.ui.theme.appColors
import com.devstudioworks.ui.theme.model.AppColor
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    device = "id:Nexus One", showSystemUi = true, showBackground = true
)
@Composable
fun TransactionItem(
    transaction: Transaction = Transaction(
        transactionDate = "1000",
        note = "",
        amount = 0.0
    )
) {
    val blockColor = if (transaction.transactionMode != "EXPENSE") {
        appColors.transactionIncomeColor
    } else {
        appColors.transactionExpenseColor
    }
    val context = LocalContext.current
    val transactionViewModel: TransactionViewModel = hiltViewModel()
    ElevatedCard(
        modifier = Modifier
            .padding(vertical = DEFAULT_CARD_ELEVATION),
        shape = RoundedCornerShape(DEFAULT_CARD_CORNER_RADIUS),
        elevation = CardDefaults.elevatedCardElevation(DEFAULT_CARD_ELEVATION),
    ) {
        Row(
            modifier = Modifier
                .combinedClickable(onClick = {
                    editTransaction(context, transaction)
                }, onLongClick = {
                    showTransactionLongPressOptions(context, transaction, transactionViewModel)
                })
                .background(color = appColors.material.onTertiary)
                .padding(horizontal = 4.dp)
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
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
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start,
            ) {
                CategoryName(transactionViewModel, transaction, appColors)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TransactionDate(transaction, appColors)
                    if (transaction.note.isNotEmpty()) {
                        TransactionNote(appColors, transaction, this)
                    }
                }

            }
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center,
            ) {
                TransactionAmount(transaction, appColors)
                if (transaction.paymentStatus != PaymentStatus.COMPLETED.name) {
                    PaymentStatus(transaction)
                }
            }
        }
    }

}

@Composable
private fun PaymentStatus(transaction: Transaction) {
    AssistChip(onClick = {}, label = {
        Text(text = transaction.paymentStatus.replaceFirstChar {
           it.uppercase()
        }, modifier = Modifier.padding(0.dp))
    }, enabled = false, modifier = Modifier.padding(0.dp), shape = RoundedCornerShape(50.dp))
}

@Composable
private fun TransactionAmount(
    transaction: Transaction,
    appColors: AppColor
) {
    Text(
        text = formatAndGetTransactionAmount(transaction),
        color = appColors.material.onPrimaryContainer,
    )
}

@Composable
private fun TransactionNote(
    appColors: AppColor,
    transaction: Transaction,
    rowScope: RowScope
) {
    rowScope.run {
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

@Composable
private fun TransactionDate(
    transaction: Transaction,
    appColors: AppColor
) {
    Text(
        text = DateFormatter.convertLongToDate(transaction.transactionDate.toLong()),
        color = appColors.material.onPrimaryContainer,
        fontSize = 13.sp,
        modifier = Modifier.padding(end = 5.dp)
    )
}

@Composable
private fun CategoryName(
    transactionViewModel: TransactionViewModel,
    transaction: Transaction,
    appColors: AppColor
) {
    Text(
        text = transactionViewModel.getTransactionCategoryName(transaction.categoryId),
        color = appColors.material.onPrimaryContainer,
        fontSize = 16.sp
    )
}

private fun formatAndGetTransactionAmount(transaction: Transaction): String =
    (if (transaction.transactionMode != "EXPENSE") "" else "- ") + transaction.amount.toString()