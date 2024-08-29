package com.devstudio.expensemanager.presentation.transactionMainScreen.model

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.MutableState
import com.devstudio.transactions.models.BottomSheetEvent
import com.devstudio.transactions.models.TransactionOptionsEvent

data class TransactionEvents(
    val booksEventCallback: (BookEvent) -> Unit,
    val filterEvent: (TransactionOptionsEvent) -> Unit,
    val moreOptionsEvent: (BottomSheetEvent<String>) -> Unit,
)

data class HomeScreenState
@OptIn(ExperimentalMaterial3Api::class)
constructor(
    val booksBottomSheet: SheetState,
    val transactionFilterBottomSheet: SheetState,
    val moreOptionsBottomSheet: SheetState,
    val fileImport: MutableState<Boolean>,
)
