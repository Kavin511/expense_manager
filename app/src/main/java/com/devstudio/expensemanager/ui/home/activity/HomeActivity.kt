package com.devstudio.expensemanager.ui.home.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.devstudio.expensemanager.Navigation
import com.devstudio.expensemanager.ui.home.composables.HomeBottomActions
import com.devstudioworks.ui.theme.MaterialTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                Scaffold(bottomBar = {
                    HomeBottomActions(navController)
                }) {
                    Box(modifier = Modifier.padding(it)){
                        Navigation(navController)
                    }
                }
            }
        }
    }

}