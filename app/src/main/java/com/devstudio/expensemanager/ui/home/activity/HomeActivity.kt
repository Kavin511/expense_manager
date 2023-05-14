package com.devstudio.expensemanager.ui.home.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.compose.rememberNavController
import com.devstudio.expensemanager.Navigation
import com.devstudio.expensemanager.viewmodel.HomeViewModel
import com.devstudio.transactions.viewmodel.TransactionViewModel
import com.devstudioworks.ui.theme.MaterialTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Navigation(rememberNavController())
            }
        }
    }

}