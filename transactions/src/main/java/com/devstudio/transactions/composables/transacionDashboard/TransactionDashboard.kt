package com.devstudio.transactions.composables.transacionDashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.util.Pair
import androidx.fragment.app.FragmentManager
import com.devstudio.data.model.TransactionFilterType
import com.devstudio.transactions.composables.transactionList.TransactionsList
import com.devstudio.transactions.models.DateSelectionStatus
import com.devstudio.transactions.models.TransactionOptionsEvent
import com.devstudio.transactions.models.TransactionUiState
import com.devstudio.transactions.viewmodel.TransactionViewModel
import com.devstudio.designSystem.appColors
import com.devstudio.data.model.OnEvent
import com.google.android.material.datepicker.MaterialDatePicker
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TransactionDashBoard(uiState: TransactionUiState.Success, onEvent: OnEvent) {
    Surface(
        modifier = Modifier
            .widthIn(max = 640.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TransactionSummary(uiState.data)
            TransactionOptions(uiState.data.filterType, onEvent)
            TransactionsList(uiState.data)
        }
    }
}

fun showDateRangePicker(
    fragmentManager: FragmentManager,
    dateSelectionRange: Pair<Long, Long>?,
    dateSelectionListener: (DateSelectionStatus) -> Unit,
) {
    val dateRangePicker =
        MaterialDatePicker.Builder.dateRangePicker().setSelection(dateSelectionRange).build()
    dateRangePicker.addOnPositiveButtonClickListener {
        dateSelectionListener(DateSelectionStatus.SELECTED(it))
    }
    dateRangePicker.addOnNegativeButtonClickListener {
        dateSelectionListener(DateSelectionStatus.CANCELED)
    }
    dateRangePicker.showNow(fragmentManager, "")
}

@Composable
fun TransactionOptions(filterType: TransactionFilterType, onEvent: OnEvent) {
    val transactionViewModel = koinViewModel<TransactionViewModel>()
    TransactionOptionsRow(filterType, transactionViewModel, onEvent)
}

@Composable
private fun TransactionOptionsRow(
    filterType: TransactionFilterType,
    transactionViewModel: TransactionViewModel,
    onEvent: OnEvent,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AnimatedVisibility(
            visible = filterType.isNotDefault(),
            enter = fadeIn(initialAlpha = .5f),
            exit = fadeOut(animationSpec = tween(durationMillis = 200)),
        ) {
            TextButton(onClick = {
                if (filterType.isNotDefault()) {
                    transactionViewModel.updateSelectedTransactionFilter(TransactionFilterType.CurrentMonth)
                }
            }) {
                Text(
                    text = if (filterType.isNotDefault()) {
                        "Reset"
                    } else {
                        ""
                    },
                    color = if (filterType.isNotDefault()) {
                        appColors.material.tertiary
                    } else {
                        Color.Unspecified
                    },
                    style = MaterialTheme.typography.titleSmall,
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            shape = if (filterType.isNotDefault()) {
                ButtonDefaults.filledTonalShape
            } else {
                ButtonDefaults.outlinedShape
            },
            colors = if (filterType.isNotDefault()) {
                ButtonDefaults.filledTonalButtonColors()
            } else {
                ButtonDefaults.outlinedButtonColors()
            },
            onClick = {
                onEvent.invoke(TransactionOptionsEvent(true))
            },
            border = BorderStroke(width = 1.dp, color = appColors.material.onPrimaryContainer),
            modifier = Modifier.align(alignment = Alignment.CenterVertically),
        ) {
            Icon(imageVector = Icons.Filled.FilterList, contentDescription = "Filter")
        }
    }
}

private fun TransactionFilterType.isNotDefault(): Boolean {
    return this != TransactionFilterType.CurrentMonth
}
