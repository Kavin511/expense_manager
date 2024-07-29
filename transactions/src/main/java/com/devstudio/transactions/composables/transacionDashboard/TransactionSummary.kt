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
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devstudio.data.model.TransactionFilterType.ALL
import com.devstudio.data.model.TransactionFilterType.DateRange
import com.devstudio.designSystem.DEFAULT_CARD_CORNER_RADIUS
import com.devstudio.transactions.viewmodel.TransactionBook
import com.devstudio.transactions.viewmodel.TransactionViewModel
import com.devstudio.utils.formatters.DateFormatter
import com.devstudio.utils.formatters.StringFormatter.roundOffDecimal
import com.devstudio.designSystem.appColors
import java.util.Calendar

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TransactionSummary(transactionBook: TransactionBook) {
    val transactionViewModel = hiltViewModel<TransactionViewModel>()
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
                style = Typography().bodyMedium,
            )
            FlowRow(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Column(horizontalAlignment = (Alignment.CenterHorizontally)) {
                    Icon(
                        imageVector = Icons.Rounded.ImportExport,
                        contentDescription = "Expenses",
                        tint = appColors.transactionExpenseColor,
                        modifier = Modifier.padding(DEFAULT_CARD_CORNER_RADIUS),
                    )
                    Text(
                        text = "Total expense ${roundOffDecimal(expense)}",
                        color = textColor,
                    )
                }
                Column(horizontalAlignment = (Alignment.CenterHorizontally)) {
                    Icon(
                        imageVector = Icons.Rounded.ImportExport,
                        contentDescription = "Income",
                        tint = appColors.transactionIncomeColor,
                        modifier = Modifier.padding(DEFAULT_CARD_CORNER_RADIUS),
                    )
                    Text(
                        text = "Total income : ${roundOffDecimal(income)}",
                        style = Typography().bodyMedium,
                        color = textColor,
                    )
                }
                Column(horizontalAlignment = (Alignment.CenterHorizontally)) {
                    Icon(
                        imageVector = Icons.Rounded.ImportExport,
                        contentDescription = "Investment",
                        tint = appColors.transactionInvestmentColor,
                        modifier = Modifier.padding(DEFAULT_CARD_CORNER_RADIUS),
                    )
                    Text(
                        text = "Total investment ${roundOffDecimal(investment)}",
                        color = textColor,
                    )
                }
            }
        }
    }
}
