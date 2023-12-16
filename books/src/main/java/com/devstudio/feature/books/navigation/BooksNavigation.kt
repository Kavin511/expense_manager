package com.devstudio.feature.books.navigation

sealed class BooksNavigation(val route: String) {
    object Books : BooksNavigation("books_route")
}