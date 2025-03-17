package com.devstudio.expensemanager.presentation.mainScreen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devstudio.data.repository.UserDataRepository
import com.devstudio.profile.viewmodels.EditableSettings
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.koin.java.KoinJavaComponent.inject

class MainViewModel : ViewModel() {
    private val userPreferencesDataStore: UserDataRepository by inject(UserDataRepository::class.java)
    val mainUiState: StateFlow<MainUiState> = userPreferencesDataStore.userData.map { userData ->
        MainUiState.Success(
            MainUiData(
                settings = EditableSettings(
                    theme = userData.theme,
                ),
            ),
        )
    }.stateIn(
        scope = viewModelScope,
        started = kotlinx.coroutines.flow.SharingStarted.Eagerly,
        initialValue = MainUiState.Loading,
    )
}

sealed class MainUiState {
    object Loading : MainUiState()
    data class Success(val data: MainUiData) : MainUiState()
}

data class MainUiData(
    val settings: EditableSettings,
)
