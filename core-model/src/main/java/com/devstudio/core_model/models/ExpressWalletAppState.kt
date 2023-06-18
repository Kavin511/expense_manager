package com.devstudio.core_model.models

sealed class ExpressWalletAppState(val route: String) {
    object HomeScreen : ExpressWalletAppState("/")
    object CategoryScreen : ExpressWalletAppState("/category")
    object EditCategoryScreen : ExpressWalletAppState("/editCategory")
}