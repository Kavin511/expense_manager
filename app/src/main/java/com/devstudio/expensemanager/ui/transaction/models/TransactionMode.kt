package com.devstudio.expensemanager.ui.transaction.models

enum class TransactionMode(val categoryList: List<String>) {
    EXPENSE(
        listOf(
            "Dining",
            "Rent",
            "Transportation",
            "Groceries",
            "Home and utilities",
            "Insurance",
            "Bills & emis",
            "Education",
            "Health and personal care",
            "Entertainment",
            "Sports"
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
            "Royalty Income"
        )
    )
}