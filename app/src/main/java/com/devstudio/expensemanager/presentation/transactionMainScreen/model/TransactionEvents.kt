package com.devstudio.expensemanager.presentation.transactionMainScreen.model

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import com.devstudio.model.models.OnEvent

data class TransactionEvents(
    val onEvent : OnEvent
)

data class HomeScreenState
@OptIn(ExperimentalMaterial3Api::class)
constructor(
    val booksBottomSheet: SheetState,
    val transactionFilterBottomSheet: SheetState,
    val moreOptionsBottomSheet: SheetState,
)
