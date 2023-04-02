package com.devstudio.expensemanager.ui.home.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.devstudio.expensemanager.ui.home.composables.TransactionDashboard
import com.devstudio.transactions.viewmodel.TransactionViewModel
import com.devstudioworks.ui.theme.MaterialTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {
    private val transactionViewModel:TransactionViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                TransactionDashboard(transactionViewModel)
            }
        }
    }

}