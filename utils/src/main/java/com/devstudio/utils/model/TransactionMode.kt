package com.devstudio.utils.model

enum class TransactionMode(val categoryList: List<String>) {
    EXPENSE(
        listOf(
            "Food",
            "Rent",
            "Transportation",
            "Groceries",
            "Home and utilities",
            "Insurance",
            "Bills & emis",
            "Education",
            "Health and personal care",
            "Entertainment",
            "Sports",
            "Other"
        )
    ),
    INCOME(
        listOf(
            "Earned Income",
            "Profit Income",
            "Interest Income",
            "Dividend Income",
            "Rental Income",
            "Capital Gains Income",
            "Royalty Income",
            "Other"
        )
    )
}