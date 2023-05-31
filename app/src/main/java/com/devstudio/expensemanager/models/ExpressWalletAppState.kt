package com.devstudio.expensemanager.models

sealed class ExpressWalletAppState(val route: String) {
    object HomeScreen : ExpressWalletAppState("/")
    object CategoryScreen : ExpressWalletAppState("/category")
}