package com.devstudio.transactions.composables.transactionFilter

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentManager
import androidx.hilt.navigation.compose.hiltViewModel
import com.devstudio.transactions.composables.transacionDashboard.showDateRangePicker
import com.devstudio.transactions.models.DateSelectionStatus
import com.devstudio.transactions.models.TransactionFilter
import com.devstudio.transactions.viewmodel.TransactionViewModel
import com.devstudio.transactions.viewmodel.TransactionViewModel.Factory.DATE_RANGE_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionFilterBottomSheet(
    coroutineScope: CoroutineScope, bottomSheetState: BottomSheetScaffoldState
) {
    val transactionViewModel = hiltViewModel<TransactionViewModel>()
    val datePickerState = remember {
        transactionViewModel.datePickerState
    }
    val fragmentManager = (LocalContext.current as AppCompatActivity).supportFragmentManager
    DateRangePicker(
        datePickerState, fragmentManager
    )
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "Filter Range",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
        )
        LazyColumn {
            items(transactionViewModel.transactionFilterOptions) { transactionFilter ->
                TransactionFilterItem(transactionFilter, coroutineScope, bottomSheetState)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionFilterItem(
    transactionFilter: TransactionFilter,
    coroutineScope: CoroutineScope,
    bottomSheetState: BottomSheetScaffoldState
) {
    return Text(text = transactionFilter.name,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier
            .padding(vertical = 9.dp, horizontal = 4.dp)
            .fillMaxWidth()
            .clickable {
                coroutineScope.launch {
                    bottomSheetState.bottomSheetState.hide()
                    transactionFilter.function.invoke(transactionFilter)
                }
            })
}


@Composable
fun DateRangePicker(
    datePickerState: MutableState<Boolean>, fragmentManager: FragmentManager
) {
    val transactionViewModel = hiltViewModel<TransactionViewModel>()
    if (datePickerState.value) {
        val transactionFilterState = transactionViewModel.selectedTransactionFilter.collectAsState()
        var dateSelectionRange: androidx.core.util.Pair<Long, Long>? = null
        if (transactionFilterState.value?.id?.equals(DATE_RANGE_ID) == true && transactionFilterState.value?.additionalData != null) {
            dateSelectionRange =
                (transactionFilterState.value!!.additionalData as androidx.core.util.Pair<Long, Long>)
        }
        showDateRangePicker(fragmentManager, dateSelectionRange) { dateSelectionStatus ->
            datePickerState.value = false
            when (dateSelectionStatus) {
                is DateSelectionStatus.SELECTED -> {
                    val transactionFilter = transactionViewModel.transactionFilterOptions[1]
                    transactionFilter.additionalData = dateSelectionStatus.selectedRange
                    transactionViewModel.updateSelectedTransactionFilter(transactionFilter)
                }

                else -> {}
            }
        }
    }
}