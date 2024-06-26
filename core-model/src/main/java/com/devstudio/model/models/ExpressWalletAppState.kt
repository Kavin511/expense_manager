package com.devstudio.model.models

sealed class ExpressWalletAppState(val route: String) {
    object HomeScreen : ExpressWalletAppState("/home") {
        object TransactionsScreen : ExpressWalletAppState("/transactions") {
            object BooksMainScreen : ExpressWalletAppState("/books")
        }
        object CategoryScreen : ExpressWalletAppState("/category")
        object EditCategoryScreen : ExpressWalletAppState("/editCategory")
        object AccountScreen : ExpressWalletAppState("/account")
    }

    object ThemeScreen : ExpressWalletAppState("/theme")
    object BudgetScreen : ExpressWalletAppState("/budget")
    object RemainderScreen : ExpressWalletAppState("/remainder")
    object DateRangeSelection : ExpressWalletAppState("/dateRangeSelection")
}
