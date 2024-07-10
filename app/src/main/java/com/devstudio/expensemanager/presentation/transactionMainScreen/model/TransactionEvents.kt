package com.devstudio.expensemanager.presentation.transactionMainScreen.model

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import com.devstudio.model.models.ExpressWalletAppState
import com.devstudio.transactions.models.BottomSheetEvent
import com.devstudio.transactions.models.TransactionOptionsEvent

data class TransactionEvents(
    val booksEventCallback: (BookEvent) -> Unit,
    val filterEvent: (TransactionOptionsEvent) -> Unit,
    val moreOptionsEvent: (BottomSheetEvent<ExpressWalletAppState>) -> Unit,
)

data class HomeScreenState @OptIn(ExperimentalMaterial3Api::class) constructor(
    val booksBottomSheet: SheetState,
    val transactionFilterBottomSheet: SheetState,
    val moreOptionsBottomSheet: SheetState
)