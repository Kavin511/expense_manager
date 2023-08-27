package com.devstudio.expensemanager.ui.home.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.rememberNavController
import com.devstudio.core_data.Theme_proto
import com.devstudio.expensemanager.Navigation
import com.devstudio.expensemanager.ui.home.composables.HomeBottomActions
import com.devstudioworks.ui.theme.MaterialTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private var uiState = mutableStateOf<MainUiState>(MainUiState.Loading)
    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash  = installSplashScreen()
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.mainUiState
                    .onEach {
                        uiState.value = it
                    }
                    .collect()
            }
        }
        splash.setKeepOnScreenCondition { uiState.value is MainUiState.Loading }
        setContent {
            val darkTheme = shouldUseDarkTheme(uiState)
            AppCompatDelegate.setDefaultNightMode(
                if (darkTheme) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
            MaterialTheme(darkTheme) {
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

    @Composable
    private fun shouldUseDarkTheme(uiState: MutableState<MainUiState>): Boolean {
        return when (uiState.value) {
            MainUiState.Loading -> isSystemInDarkTheme()
            is MainUiState.Success -> {
                when ((uiState.value as MainUiState.Success).data.settings.theme) {
                    Theme_proto.DARK -> true
                    Theme_proto.LIGHT -> false
                    else -> {
                        isSystemInDarkTheme()
                    }
                }
            }
        }
    }

}