package com.devstudio.model.models

@JvmInline
value class OnEvent(val invoke: (ExpressWalletEvent) -> Unit)

interface ExpressWalletEvent

sealed class ExpressWalletAppState(public val route: String) : ExpressWalletEvent{
    data object HomeScreen : ExpressWalletAppState("/home") {
        data object TransactionsScreen : ExpressWalletAppState("/transactions") {
            data object BooksMainScreen : ExpressWalletAppState("/books")
        }
        data object CategoryScreen : ExpressWalletAppState("/category")
        data object EditCategoryScreen : ExpressWalletAppState("/editCategory")
        data object AccountScreen : ExpressWalletAppState("/account")
    }
    data object ImportScreen : ExpressWalletAppState("/import")
    data object ThemeScreen : ExpressWalletAppState("/theme")
    data object BudgetScreen : ExpressWalletAppState("/budget")
    data object ImportCsv : ExpressWalletAppState("/importCsv")
    data object RemainderScreen : ExpressWalletAppState("/remainder")
    data object DateRangeSelection : ExpressWalletAppState("/dateRangeSelection")
}
