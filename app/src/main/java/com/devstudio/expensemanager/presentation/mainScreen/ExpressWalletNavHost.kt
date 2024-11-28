package com.devstudio.expensemanager.presentation.mainScreen

import HomeActions
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentManager
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.devstudio.expensemanager.presentation.home.composables.HomeScreenBottomBar
import com.devstudio.expensemanager.presentation.home.composables.HomeScreenNavHost
import com.devstudio.expensemanager.presentation.transactionMainScreen.model.BookEvent
import com.devstudio.expensemanager.presentation.transactionMainScreen.model.HomeScreenState
import com.devstudio.feature.books.BooksViewModel
import com.devstudio.model.models.ExpressWalletAppState
import com.devstudio.model.models.ExpressWalletAppState.HomeScreen
import com.devstudio.model.models.ExpressWalletAppState.ImportCsv
import com.devstudio.model.models.OnEvent
import com.devstudio.profile.BudgetScreen
import com.devstudio.profile.ThemeSelectionScreen
import com.devstudio.profile.composables.RemainderScreen
import com.devstudio.sharedmodule.importData.presentation.CsvImportScreen
import com.devstudio.transactions.composables.transactionList.applySelectedFilter
import com.devstudio.transactions.models.BottomSheetEvent
import com.devstudio.transactions.models.TransactionOptionsEvent
import com.devstudio.transactions.viewmodel.TransactionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ExpressWalletNavHost(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = HomeScreen.route,
    ) {
        composable(route = HomeScreen.route) {
            HomeScreen(navController)
        }
        composable(route = ExpressWalletAppState.BudgetScreen.route) {
            BudgetScreen()
        }
        composable(route = ImportCsv.route) {
            CsvImportScreen()
        }
        composable(route = ExpressWalletAppState.ThemeScreen.route) {
            ThemeSelectionScreen(navController)
        }
        composable(route = ExpressWalletAppState.RemainderScreen.route) {
            RemainderScreen(navController)
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun HomeScreen(navController: NavHostController) {
    val transactionViewModel = hiltViewModel<TransactionViewModel>()
    val transactionUiState by transactionViewModel.uiState.collectAsStateWithLifecycle()
    val booksBottomSheet = rememberModalBottomSheetState()
    val transactionFilterBottomSheet = rememberModalBottomSheetState()
    val moreOptionBottomSheet = rememberModalBottomSheetState()
    val homeNavController: NavHostController = rememberNavController()
    Scaffold(bottomBar = {
        HomeScreenBottomBar(homeNavController)
    }) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            HomeScreenNavHost(
                homeNavController,
                transactionUiState,
                HomeScreenState(
                    booksBottomSheet, transactionFilterBottomSheet, moreOptionBottomSheet
                ),
                onEvent(
                    booksBottomSheet,
                    transactionFilterBottomSheet,
                    transactionViewModel,
                    moreOptionBottomSheet,
                    navController
                ),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun onEvent(
    booksBottomSheet: SheetState,
    transactionFilterBottomSheet: SheetState,
    transactionViewModel: TransactionViewModel,
    moreOptionBottomSheet: SheetState,
    navController: NavHostController,
    homeNavController: NavHostController = rememberNavController(),
    booksViewModel: BooksViewModel = hiltViewModel<BooksViewModel>(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    fragmentManager: FragmentManager = (LocalContext.current as AppCompatActivity).supportFragmentManager,
) = remember {
    OnEvent { event ->
        when (event) {
            is HomeScreen -> {
                homeNavController.navigate(event.route)
            }

            is BookEvent -> {
                onBookEvent(event, coroutineScope, booksBottomSheet, booksViewModel)
            }

            is TransactionOptionsEvent -> {
                onTransactionEvent(
                    event,
                    coroutineScope,
                    transactionFilterBottomSheet,
                    transactionViewModel,
                    fragmentManager
                )
            }

            is BottomSheetEvent<*> -> {
                moreOptionBottomSheet.onBottomSheetEvent(coroutineScope, event, navController)
            }

            is ExpressWalletAppState -> {
                navController.navigate(event.route)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private fun SheetState.onBottomSheetEvent(
    coroutineScope: CoroutineScope,
    event: BottomSheetEvent<*>,
    navController: NavHostController,
) {
    coroutineScope.launch {
        if (event.showBottomSheet) {
            show()
        } else {
            hide()
        }
        if (event.selectedItem == HomeActions.IMPORT_CSV) {
            navController.navigate(ImportCsv.route)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private fun onTransactionEvent(
    event: TransactionOptionsEvent,
    coroutineScope: CoroutineScope,
    transactionFilterBottomSheet: SheetState,
    transactionViewModel: TransactionViewModel,
    fragmentManager: FragmentManager,
) {
    if (event.showBottomSheet) {
        coroutineScope.launch {
            transactionFilterBottomSheet.show()
        }
    } else {
        coroutineScope.launch {
            transactionFilterBottomSheet.hide()
        }
        applySelectedFilter(
            event.selectedItem, transactionViewModel, fragmentManager
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private fun onBookEvent(
    event: BookEvent,
    coroutineScope: CoroutineScope,
    booksBottomSheet: SheetState,
    booksViewModel: BooksViewModel,
) {
    if (event.showBottomSheet) {
        coroutineScope.launch {
            booksBottomSheet.show()
        }
    } else {
        coroutineScope.launch {
            booksBottomSheet.hide()
        }
    }
    event.selectedItem?.let {
        booksViewModel.saveSelectedBook(it)
    }
}