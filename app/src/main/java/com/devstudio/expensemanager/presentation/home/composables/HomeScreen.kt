package com.devstudio.expensemanager.presentation.home.composables

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import com.devstudio.expensemanager.presentation.transactionMainScreen.TransactionMainScreen
import com.devstudio.expensemanager.presentation.transactionMainScreen.model.BookEvent
import com.devstudio.expensemanager.presentation.transactionMainScreen.model.TransactionEvents
import com.devstudio.feature.books.BooksMainScreen
import com.devstudio.transactions.composables.transactionFilter.TransactionFilterBottomSheet
import com.devstudio.transactions.models.TransactionOptionsEvent
import com.devstudio.transactions.models.TransactionUiState

@ExperimentalMaterial3Api
@Composable
fun HomeScreen(
    navController: NavHostController,
    uiState: TransactionUiState,
    booksBottomSheet: SheetState = rememberModalBottomSheetState(),
    transactionFilterBottomSheet: SheetState = rememberModalBottomSheetState(),
    transactionEvents: TransactionEvents,
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
    if (booksBottomSheet.currentValue.equals(SheetValue.Expanded)) {
        BooksMainScreen(booksBottomSheet) {
            transactionEvents.booksEventCallback.invoke(BookEvent(false, it))
        }
    }
    if (transactionFilterBottomSheet.currentValue.equals(SheetValue.Expanded)) {
        TransactionFilterBottomSheet(transactionFilterBottomSheet) {
            transactionEvents.filterEvent.invoke(TransactionOptionsEvent(false, it))
        }
    }
    TransactionMainScreen(
        snackBarHostState,
        navController,
        scrollBehavior,
        transactionEvents,
        uiState,
    )
}
