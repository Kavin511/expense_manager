package com.devstudio.expensemanager

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.devstudio.core_model.models.ExpressWalletAppState
import com.devstudio.expensemanager.ui.home.composables.HomeScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigation(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = ExpressWalletAppState.HomeScreen.route
    ) {
        composable(route = ExpressWalletAppState.HomeScreen.route) {
            HomeScreen()
        }
    }
}