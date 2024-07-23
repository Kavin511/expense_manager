package com.devstudio.expensemanager.presentation.mainScreen

import IMPORT_CSV
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.devstudio.category.composables.CategoryMainScreen
import com.devstudio.expensemanager.presentation.home.composables.HomeScreen
import com.devstudio.expensemanager.presentation.transactionMainScreen.model.BookEvent
import com.devstudio.expensemanager.presentation.transactionMainScreen.model.HomeScreenState
import com.devstudio.expensemanager.presentation.transactionMainScreen.model.TransactionEvents
import com.devstudio.feature.books.BooksViewModel
import com.devstudio.model.models.ExpressWalletAppState
import com.devstudio.profile.BudgetScreen
import com.devstudio.profile.ThemeSelectionScreen
import com.devstudio.profile.composables.ProfileMainScreen
import com.devstudio.profile.composables.RemainderScreen
import com.devstudio.transactions.composables.transactionList.applySelectedFilter
import com.devstudio.transactions.viewmodel.TransactionViewModel
import kotlinx.coroutines.launch

@Composable
fun NavigationHost(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = ExpressWalletAppState.HomeScreen.route,
    ) {
        homeScreenGraph(navController)
        composable(route = ExpressWalletAppState.BudgetScreen.route) {
            BudgetScreen()
        }
        navigation(route = "/", startDestination = ExpressWalletAppState.HomeScreen.route) {
            composable(
                route = ExpressWalletAppState.HomeScreen.CategoryScreen.route,
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern =
                            "android:app://com.devstudio.expensemanager.ui.home.activity.HomeActivity/categoryMainScreen"
                    },
                ),
            ) {
                CategoryMainScreen()
            }
            composable(
                route = ExpressWalletAppState.HomeScreen.CategoryScreen.route,
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern =
                            "android:app://com.devstudio.expensemanager.ui.home.activity.HomeActivity/categoryMainScreen"
                    },
                ),
            ) {
                CategoryMainScreen()
            }

            composable(route = ExpressWalletAppState.HomeScreen.AccountScreen.route) {
                ProfileMainScreen(navController)
            }
        }
        composable(route = ExpressWalletAppState.ThemeScreen.route) {
            ThemeSelectionScreen(navController)
        }
        composable(route = ExpressWalletAppState.RemainderScreen.route) {
            RemainderScreen(navController)
        }
    }
}

private fun NavGraphBuilder.homeScreenGraph(navController: NavHostController) {
    composable(route = ExpressWalletAppState.HomeScreen.route) {
        HomeScreenRoute(navController)
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun HomeScreenRoute(navController: NavHostController) {
    val booksViewModel = hiltViewModel<BooksViewModel>()
    val transactionViewModel = hiltViewModel<TransactionViewModel>()
    val transactionUiState by transactionViewModel.uiState.collectAsStateWithLifecycle()
    val booksBottomSheet = rememberModalBottomSheetState()
    val transactionFilterBottomSheet = rememberModalBottomSheetState()
    val moreOptionBottomSheet = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    var invokeExcelImport = remember { mutableStateOf(false) }
    val fragmentManager = (LocalContext.current as AppCompatActivity).supportFragmentManager
    val booksEventCallback: (BookEvent) -> Unit = { bookEvent ->
        if (bookEvent.showBottomSheet) {
            coroutineScope.launch {
                booksBottomSheet.show()
            }
        } else {
            coroutineScope.launch {
                booksBottomSheet.hide()
            }
        }
        bookEvent.selectedItem?.let {
            booksViewModel.saveSelectedBook(it)
        }
    }
    HomeScreen(
        navController,
        transactionUiState,
        HomeScreenState(booksBottomSheet, transactionFilterBottomSheet, moreOptionBottomSheet, invokeExcelImport),
        TransactionEvents(booksEventCallback = booksEventCallback, filterEvent = {
            if (it.showBottomSheet) {
                coroutineScope.launch {
                    transactionFilterBottomSheet.show()
                }
            } else {
                coroutineScope.launch {
                    transactionFilterBottomSheet.hide()
                }
                applySelectedFilter(it.selectedItem, transactionViewModel, fragmentManager)
            }
        }, {
            coroutineScope.launch {
                if (it.showBottomSheet) {
                    moreOptionBottomSheet.show()
                } else {
                    if (it.selectedItem.equals(IMPORT_CSV)) {
                        invokeExcelImport.value = true
                    }
                    moreOptionBottomSheet.hide()
                }
            }
        })
    )
}
