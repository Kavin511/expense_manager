package com.devstudio.transactions.composables.transacionDashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ImportExport
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.devstudio.data.model.TransactionFilterType.ALL
import com.devstudio.data.model.TransactionFilterType.DateRange
import com.devstudio.designSystem.appColors
import com.devstudio.designSystem.smallPadding
import com.devstudio.transactions.viewmodel.TransactionBook
import com.devstudio.transactions.viewmodel.TransactionViewModel
import com.devstudio.utils.formatters.DateFormatter
import com.devstudio.utils.formatters.StringFormatter.roundOffDecimal
import org.koin.compose.viewmodel.koinViewModel
import java.util.Calendar

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TransactionSummary(transactionBook: TransactionBook) {
    val transactionViewModel = koinViewModel<TransactionViewModel>()
    val (income, expense, investment) = transactionViewModel.getTransactionSummaryDetails(
        transactionBook.transactions.collectAsState(
            initial = listOf(),
        ).value,
    )
    val textColor = appColors.material.onTertiaryContainer
    val transactionFilterType = transactionBook.filterType
    return Card {
        Column(
            Modifier
                .background(color = appColors.material.tertiaryContainer)
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val transactionSummaryText = when (transactionFilterType) {
                is DateRange -> {
                    val selectedDate = transactionFilterType.additionalData
                    "Summary from ${
                        DateFormatter.convertLongToDate(selectedDate.first.toString()) + " to " + DateFormatter.convertLongToDate(
                            selectedDate.second.toString(),
                        )
                    }"
                }

                is ALL -> {
                    "All transactions summary"
                }

                else -> {
                    "${
                        DateFormatter.monthNames[Calendar.getInstance().get(Calendar.MONTH)]
                    } month summary"
                }
            }
            Text(
                text = transactionSummaryText,
                color = textColor,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = smallPadding),
            )
            FlowRow(
                Modifier.fillMaxWidth().padding(vertical = smallPadding),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                SummaryItem(
                    value = "Total income : ${roundOffDecimal(income)}",
                    iconTint = appColors.transactionIncomeColor,
                    modifier = Modifier.padding(horizontal = smallPadding),
                )
                SummaryItem(
                    value = "Total expense ${roundOffDecimal(expense)}",
                    iconTint = appColors.transactionExpenseColor,
                    modifier = Modifier.padding(horizontal = smallPadding),
                )
                SummaryItem(
                    value = "Total investment ${roundOffDecimal(investment)}",
                    iconTint = appColors.transactionInvestmentColor,
                    modifier = Modifier.padding(horizontal = smallPadding),
                )
            }
        }
    }
}

@Composable
private fun SummaryItem(
    modifier: Modifier,
    value: String,
    iconTint: Color = LocalContentColor.current
) {
    Column(modifier = modifier, horizontalAlignment = (Alignment.CenterHorizontally)) {
        Icon(
            imageVector = Icons.Rounded.ImportExport,
            contentDescription = value,
            tint = iconTint,
            modifier = Modifier.padding(smallPadding),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = appColors.material.onTertiaryContainer,
        )
    }
}
