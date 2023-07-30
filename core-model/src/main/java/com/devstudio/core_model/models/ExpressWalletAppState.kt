package com.devstudio.core_model.models

sealed class ExpressWalletAppState(val route: String) {
    object HomeScreen : ExpressWalletAppState("/home") {
        object TransactionsScreen : ExpressWalletAppState("/transactions")
        object CategoryScreen : ExpressWalletAppState("/category")
        object EditCategoryScreen : ExpressWalletAppState("/editCategory")
        object AccountScreen : ExpressWalletAppState("/account")
    }

    object ThemeScreen : ExpressWalletAppState("/theme")
    object BudgetScreen : ExpressWalletAppState("/budget")
    object RemainderScreen : ExpressWalletAppState("/remainder")
}