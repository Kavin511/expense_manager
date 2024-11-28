package com.devstudio.expensemanager.presentation.mainScreen.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.rememberNavController
import com.devstudio.data.model.Theme
import com.devstudio.designSystem.AppMaterialTheme
import com.devstudio.expensemanager.presentation.mainScreen.ExpressWalletNavHost
import com.devstudio.expensemanager.presentation.mainScreen.viewmodel.MainUiState
import com.devstudio.expensemanager.presentation.mainScreen.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var uiState = mutableStateOf<MainUiState>(MainUiState.Loading)
    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
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
                if (darkTheme) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO,
            )
            AppMaterialTheme(darkTheme) {
                val navController = rememberNavController()
                ExpressWalletNavHost(navController)
            }
        }
    }

    @Composable
    private fun shouldUseDarkTheme(uiState: MutableState<MainUiState>): Boolean {
        return when (uiState.value) {
            MainUiState.Loading -> isSystemInDarkTheme()
            is MainUiState.Success -> {
                when ((uiState.value as MainUiState.Success).data.settings.theme) {
                    Theme.DARK -> true
                    Theme.LIGHT -> false
                    else -> {
                        isSystemInDarkTheme()
                    }
                }
            }
        }
    }
}
