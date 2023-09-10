package com.devstudio.expensemanager

import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.devstudio.category.composables.CategoryMainScreen
import com.devstudio.core_model.models.ExpressWalletAppState
import com.devstudio.expensemanager.ui.home.composables.HomeScreen
import com.devstudio.feature.books.navigation.booksGraph
import com.devstudio.profile.BudgetScreen
import com.devstudio.profile.composables.ProfileMainScreen
import com.devstudio.profile.composables.RemainderScreen
import com.devstudio.profile.ThemeSelectionScreen
import com.devstudio.transactions.composables.transacionDashboard.TransactionDashBoard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigation(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = ExpressWalletAppState.HomeScreen.route
    ) {
        var bottomSheetScaffoldState: BottomSheetScaffoldState? = null
        composable(route = ExpressWalletAppState.HomeScreen.route) {
            bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
                bottomSheetState = SheetState(
                    initialValue = SheetValue.Hidden, skipPartiallyExpanded = true
                )
            )
            HomeScreen(navController, bottomSheetScaffoldState!!)
        }
        composable(route = ExpressWalletAppState.BudgetScreen.route) {
            BudgetScreen()
        }
        booksGraph()
        navigation(route = "/", startDestination = ExpressWalletAppState.HomeScreen.route) {
            composable(route = ExpressWalletAppState.HomeScreen.TransactionsScreen.route,
                arguments = listOf(
                    navArgument("bottomSheetState") {
                    }
                )) {
                TransactionDashBoard(bottomSheetScaffoldState!!)
            }

            composable(route = ExpressWalletAppState.HomeScreen.CategoryScreen.route,
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern =
                            "android:app://com.devstudio.expensemanager.ui.home.activity.HomeActivity/categoryMainScreen"
                    }
                )) {
                CategoryMainScreen()
            }
            composable(route = ExpressWalletAppState.HomeScreen.CategoryScreen.route,
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern =
                            "android:app://com.devstudio.expensemanager.ui.home.activity.HomeActivity/categoryMainScreen"
                    }
                )) {
                CategoryMainScreen()
            }

            composable(route = ExpressWalletAppState.HomeScreen.AccountScreen.route) {
                ProfileMainScreen(navController)
            }
            composable(route = ExpressWalletAppState.ThemeScreen.route) {
                ThemeSelectionScreen(navController)
            }
            composable(route = ExpressWalletAppState.RemainderScreen.route) {
                RemainderScreen(navController)
            }
        }
    }
}