package com.devstudio.expensemanager.presentation.home.composables

import HomeActions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.devstudio.designSystem.components.BottomSheet
import com.devstudio.expensemanager.presentation.transactionMainScreen.TransactionMainScreen
import com.devstudio.expensemanager.presentation.transactionMainScreen.model.BookEvent
import com.devstudio.expensemanager.presentation.transactionMainScreen.model.HomeScreenState
import com.devstudio.expensemanager.presentation.transactionMainScreen.model.TransactionEvents
import com.devstudio.feature.books.BooksMainScreen
import com.devstudio.sharedmodule.TransactionImportSuccessScreen
import com.devstudio.transactions.composables.transactionFilter.TransactionFilterBottomSheet
import com.devstudio.transactions.models.BottomSheetEvent
import com.devstudio.transactions.models.TransactionOptionsEvent
import com.devstudio.transactions.models.TransactionUiState

@ExperimentalMaterial3Api
@Composable
fun HomeScreen(
    navController: NavHostController,
    uiState: TransactionUiState,
    homeScreenState: HomeScreenState,
    transactionEvents: TransactionEvents,
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val topAppBarState = rememberTopAppBarState()
    val booksBottomSheet = homeScreenState.booksBottomSheet
    val transactionFilterBottomSheet = homeScreenState.transactionFilterBottomSheet
    val importCSV = homeScreenState.fileImport
    val moreOptionsBottomSheet = homeScreenState.moreOptionsBottomSheet
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
    if (moreOptionsBottomSheet.currentValue.equals(SheetValue.Expanded)) {
        HomeActions(navController, snackBarHostState) {
            transactionEvents.moreOptionsEvent.invoke(BottomSheetEvent(false, it))
        }
    }
    val context = LocalContext.current
    if (importCSV.value) {
        val launcher =
            rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
        ExcelFileUpload(onClose = {
            importCSV.value = false
        }, modifyTransaction = {
            val intent = Intent(context, TransactionActivity::class.java)
            intent.putExtra(MappedTransaction, TransactionUiModel(it.transaction))
            launcher.launch(intent)
            Result.success(Unit)
        })
    }
    TransactionMainScreen(
        snackBarHostState,
        navController,
        scrollBehavior,
        transactionEvents,
        uiState,
    )
}
