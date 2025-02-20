package com.devstudio.expensemanager.presentation.home.composables

import HomeActionsBottomSheet
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults.enterAlwaysScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.devstudio.category.composables.CategoryMainScreen
import com.devstudio.expensemanager.presentation.transactionMainScreen.TransactionMainScreen
import com.devstudio.expensemanager.presentation.transactionMainScreen.model.BookEvent
import com.devstudio.expensemanager.presentation.transactionMainScreen.model.HomeScreenState
import com.devstudio.feature.books.BooksMainScreen
import com.devstudio.model.models.ExpressWalletAppState
import com.devstudio.model.models.ExpressWalletAppState.HomeScreen.AccountScreen
import com.devstudio.model.models.ExpressWalletAppState.HomeScreen.CategoryScreen
import com.devstudio.model.models.ExpressWalletAppState.HomeScreen.TransactionsScreen
import com.devstudio.model.models.OnEvent
import com.devstudio.profile.composables.ProfileMainScreen
import com.devstudio.transactions.composables.transactionFilter.TransactionFilterBottomSheet
import com.devstudio.transactions.models.TransactionOptionsEvent
import com.devstudio.transactions.models.TransactionUiState

@ExperimentalMaterial3Api
@Composable
fun HomeScreenNavHost(
    navController: NavHostController,
    uiState: TransactionUiState,
    homeScreenState: HomeScreenState,
    onEvent: OnEvent,
) {
    NavHost(navController = navController, startDestination = TransactionsScreen.route) {
        composable(route = TransactionsScreen.route) {
            val snackBarHostState = remember { SnackbarHostState() }
            val booksBottomSheet = homeScreenState.booksBottomSheet
            val transactionFilterBottomSheet = homeScreenState.transactionFilterBottomSheet
            val moreOptionsBottomSheet = homeScreenState.moreOptionsBottomSheet
            if (booksBottomSheet.currentValue == SheetValue.Expanded) {
                BooksMainScreen(booksBottomSheet) {
                    onEvent.invoke(BookEvent(false, it))
                }
            }
            if (transactionFilterBottomSheet.currentValue == SheetValue.Expanded) {
                TransactionFilterBottomSheet(transactionFilterBottomSheet) {
                    onEvent.invoke(TransactionOptionsEvent(false, it))
                }
            }
            if (moreOptionsBottomSheet.currentValue == SheetValue.Expanded) {
                HomeActionsBottomSheet(snackBarHostState, onEvent)
            }
            TransactionMainScreen(
                snackBarHostState,
                enterAlwaysScrollBehavior(rememberTopAppBarState()),
                onEvent,
                uiState,
            )
        }
        composable(
            route = CategoryScreen.route,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern =
                        "android:app://com.devstudio.expensemanager.ui.home.activity.HomeActivity/categoryMainScreen"
                },
            ),
        ) {
            CategoryMainScreen()
        }

        composable(route = AccountScreen.route) {
            ProfileMainScreen(onEvent)
        }

    }
}