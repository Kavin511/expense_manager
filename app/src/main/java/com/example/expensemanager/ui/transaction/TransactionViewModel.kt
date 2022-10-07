package com.example.expensemanager.ui.transaction

import androidx.lifecycle.ViewModel
import com.example.expensemanager.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import java.util.*

class TransactionViewModel() : ViewModel() {
    val _cursorPosition = MutableStateFlow(0)
    val cursorPosition=_cursorPosition

    val category = mapOf(
        "Daily essentials" to R.drawable.ic_daily_needs,
        "Bills" to R.drawable.ic_communication,
        "Travel" to R.drawable.ic_travel,
        "Entertainment" to R.drawable.ic_entertainment,
        "Food" to R.drawable.ic_food,
        "Medical" to R.drawable.ic_baseline_local_hospital_24,
        "Sports" to R.drawable.ic_sports,
    )


}