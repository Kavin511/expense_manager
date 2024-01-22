package com.devstudioworks.ui.theme.model

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color


data class AppColor(
    val material: ColorScheme,
    val transactionIncomeColor: Color,
    val transactionExpenseColor: Color,
    val transactionInvestmentColor: Color,
    val incomeIconTint: Color,
    val expenseIconTint: Color,
)