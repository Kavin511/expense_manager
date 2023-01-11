package com.devstudio.expensemanager.ui.home.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.NavHost
import androidx.navigation.Navigation
import com.devstudio.expensemanager.ui.home.composables.Home
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
                Home(transactionViewModel)
            }
        }
    }

}