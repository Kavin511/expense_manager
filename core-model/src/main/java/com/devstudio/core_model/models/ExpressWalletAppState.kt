package com.devstudio.core_model.models

sealed class ExpressWalletAppState(val route: String) {
    object HomeScreen : ExpressWalletAppState("/home") {
        object TransactionsScreen : ExpressWalletAppState("/transactions")
        object CategoryScreen : ExpressWalletAppState("/category")
        object EditCategoryScreen : ExpressWalletAppState("/editCategory")
        object AccountScreen : ExpressWalletAppState("/account")
    }
}