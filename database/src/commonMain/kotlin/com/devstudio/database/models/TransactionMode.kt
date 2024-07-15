package com.devstudio.database.models

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
            "Other",
        ),
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
            "Other",
        ),
    ),
    INVESTMENT(listOf("Stocks", "Mutual Funds", "Fixed Deposits", "Other")),
}
