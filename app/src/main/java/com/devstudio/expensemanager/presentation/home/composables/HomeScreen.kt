package com.devstudio.expensemanager.presentation.home.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.devstudio.core_model.models.ExpressWalletAppState
import com.devstudio.expensemanager.presentation.transactionMainScreen.model.BookEvent
import com.devstudio.expensemanager.presentation.transactionMainScreen.model.TransactionEvents
import com.devstudio.expensemanager.presentation.transactionMainScreen.TransactionMainScreen
import com.devstudio.feature.books.BooksMainScreen
import com.devstudio.transactions.models.TransactionOptionsEvent
import com.devstudio.transactions.composables.transactionFilter.TransactionFilterBottomSheet
import com.devstudio.transactions.models.TransactionUiState
import com.devstudioworks.ui.theme.appColors

@ExperimentalMaterial3Api
@Composable
fun HomeScreen(
    navController: NavHostController,
    uiState: TransactionUiState,
    booksBottomSheet: SheetState = rememberModalBottomSheetState(),
    transactionFilterBottomSheet: SheetState = rememberModalBottomSheetState(),
    transactionEvents: TransactionEvents
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
        snackBarHostState, navController, scrollBehavior, transactionEvents, uiState
    )
}
