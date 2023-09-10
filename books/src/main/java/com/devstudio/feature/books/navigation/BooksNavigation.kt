package com.devstudio.feature.books.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.devstudio.feature.books.BooksMainScreen
import com.devstudio.feature.books.BooksMainScreenRoute

fun NavController.navigateToBooksGraph() {
    this.navigate(BooksNavigation.Books.route)
}

fun NavGraphBuilder.booksGraph() {
    composable(route = BooksNavigation.Books.route) {
        BooksMainScreenRoute()
    }
}

sealed class BooksNavigation(val route: String) {
    object Books : BooksNavigation("books_route")
}