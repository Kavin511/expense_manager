package com.devstudio.utils.utils

enum class TransactionMode(val categoryList: List<String>, val title: String) {
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
        ),"EXPENSE"
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
        ), "INCOME"
    ),
    INVESTMENT(listOf("Stocks", "Mutual Funds", "Fixed Deposits", "Other"), "INVESTMENT"),
}
