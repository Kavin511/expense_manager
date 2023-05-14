package com.devstudio.transactions.composables.transacionDashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.core.util.Pair
import androidx.hilt.navigation.compose.hiltViewModel
import com.devstudio.transactions.viewmodel.TransactionViewModel
import com.devstudio.transactions.viewmodel.TransactionViewModel.Factory.DATE_RANGE_ID
import com.devstudio.transactions.viewmodel.TransactionViewModel.Factory.SHOW_ALL_ID
import com.devstudio.utils.formatters.DateFormatter
import com.devstudio.utils.formatters.StringFormatter.roundOffDecimal
import com.devstudioworks.core.ui.R
import com.devstudioworks.ui.theme.appColors
import java.util.Calendar

@Composable
fun TransactionSummary() {
    val transactionViewModel = hiltViewModel<TransactionViewModel>()
    val expense = transactionViewModel.sumOfExpense.collectAsState()
    val income = transactionViewModel.sumOfIncome.collectAsState()
    val selectedTransactionFilter = transactionViewModel.selectedTransactionFilter.collectAsState()
    val textColor = appColors.material.onTertiaryContainer
    return Card {
        Column(
            Modifier
                .background(color = appColors.material.tertiaryContainer)
                .fillMaxWidth()
                .padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val transactionFilter = selectedTransactionFilter.value
            val transactionSummaryText =
                when (transactionFilter?.id) {
                    DATE_RANGE_ID -> {
                        val selectedDate = transactionFilter.additionalData as Pair<Long, Long>
                        "Summary from ${
                            DateFormatter.convertLongToDate(selectedDate.first) + " to " + DateFormatter.convertLongToDate(
                                selectedDate.second
                            )
                        }"
                    }

                    SHOW_ALL_ID -> {
                        "All transactions summary"
                    }

                    else -> {
                        "${
                            DateFormatter.monthNames[Calendar.getInstance().get(Calendar.MONTH)]
                        } month summary"
                    }
                }
            Text(
                text = transactionSummaryText, color = textColor, style = Typography().bodyMedium
            )
            Row(
                Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = (Alignment.CenterHorizontally)) {
                    Icon(
                        imageVector = Icons.Rounded.ImportExport,
                        contentDescription = "Expenses",
                        tint = appColors.transactionExpenseColor,
                        modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding))
                    )
                    Text(
                        text = "Total expense ${roundOffDecimal(expense.value)}", color = textColor
                    )
                }
                Column(horizontalAlignment = (Alignment.CenterHorizontally)) {
                    Icon(
                        imageVector = Icons.Rounded.ImportExport,
                        contentDescription = "Income",
                        tint = appColors.transactionIncomeColor,
                        modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding))
                    )
                    Text(
                        text = "Total income : ${roundOffDecimal(income.value)}",
                        style = Typography().bodyMedium,
                        color = textColor
                    )
                }
            }
        }
    }
}
