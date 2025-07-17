package com.devstudio.transactions.composables.transactionFilter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.devstudio.designSystem.components.BottomSheet
import com.devstudio.transactions.models.FilterItem
import com.devstudio.transactions.viewmodel.TransactionViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(ExperimentalMaterial3Api::class, KoinExperimentalAPI::class)
@Composable
fun TransactionFilterBottomSheet(
    filterBottomSheetState: SheetState,
    event: (FilterItem?) -> Unit,
) {
    val transactionViewModel = koinViewModel<TransactionViewModel>()
    BottomSheet(onDismissRequest = {
        event.invoke(null)
    }, sheetState = filterBottomSheetState) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        ) {
            Text(
                text = "Filter Range",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
            )
            LazyColumn {
                items(transactionViewModel.filterItemOptions) { transactionFilter ->
                    TransactionFilterItem(transactionFilter, event)
                }
            }
        }
    }
}

@Composable
fun TransactionFilterItem(
    filterItem: FilterItem,
    event: (FilterItem) -> Unit,
) {
    return Text(
        text = filterItem.name,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier
            .padding(vertical = 9.dp, horizontal = 4.dp)
            .fillMaxWidth()
            .clickable {
                event.invoke(filterItem)
            },
    )
}