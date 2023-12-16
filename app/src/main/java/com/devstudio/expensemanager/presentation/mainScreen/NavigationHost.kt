package com.devstudio.expensemanager.presentation.mainScreen

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.devstudio.core_model.models.ExpressWalletAppState
import com.devstudio.expensemanager.presentation.transactionMainScreen.model.BookEvent
import com.devstudio.expensemanager.presentation.home.composables.HomeScreen
import com.devstudio.expensemanager.presentation.transactionMainScreen.model.TransactionEvents
import com.devstudio.feature.books.BooksViewModel
import com.devstudio.profile.BudgetScreen
import com.devstudio.profile.ThemeSelectionScreen
import com.devstudio.profile.composables.ProfileMainScreen
import com.devstudio.profile.composables.RemainderScreen
import com.devstudio.transactions.composables.transactionList.applySelectedFilter
import com.devstudio.transactions.models.TransactionUiState
import com.devstudio.transactions.viewmodel.TransactionViewModel
import kotlinx.coroutines.launch

@Composable
fun NavigationHost(
    navController: NavHostController
) {
    NavHost(
        navController = navController, startDestination = ExpressWalletAppState.HomeScreen.route
    ) {
        homeScreenGraph(navController)
        composable(route = ExpressWalletAppState.BudgetScreen.route) {
            BudgetScreen()
        }
        navigation(route = "/", startDestination = ExpressWalletAppState.HomeScreen.route) {
            composable(
                route = ExpressWalletAppState.HomeScreen.CategoryScreen.route,
                deepLinks = listOf(navDeepLink {
                    uriPattern =
                        "android:app://com.devstudio.expensemanager.ui.home.activity.HomeActivity/categoryMainScreen"
                })
            ) {
                CategoryMainScreen()
            }
            composable(
                route = ExpressWalletAppState.HomeScreen.CategoryScreen.route,
                deepLinks = listOf(navDeepLink {
                    uriPattern =
                        "android:app://com.devstudio.expensemanager.ui.home.activity.HomeActivity/categoryMainScreen"
                })
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
    val transactionUiState by transactionViewModel.uiState.collectAsStateWithLifecycle(
        TransactionUiState.Loading
    )
    val booksBottomSheet = rememberModalBottomSheetState()
    val transactionFilterBottomSheet = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    val fragmentManager = (LocalContext.current as AppCompatActivity).supportFragmentManager
    val booksEventCallback: (BookEvent) -> Unit = { bookEvent ->
        if (bookEvent.showBookSelection) {
            coroutineScope.launch {
                booksBottomSheet.show()
            }
        } else {
            coroutineScope.launch {
                booksBottomSheet.hide()
            }
        }
        bookEvent.bookId?.let {
            booksViewModel.saveSelectedBook(it)
        }
    }
    HomeScreen(
        navController,
        transactionUiState,
        booksBottomSheet,
        transactionFilterBottomSheet,
        TransactionEvents(booksEventCallback = booksEventCallback, filterEvent = {
            if (it.shouldBookSelection) {
                coroutineScope.launch {
                    transactionFilterBottomSheet.show()
                }
            } else {
                coroutineScope.launch {
                    transactionFilterBottomSheet.hide()
                }
                applySelectedFilter(it.filterItem, transactionViewModel, fragmentManager)
            }
        })
    )
}