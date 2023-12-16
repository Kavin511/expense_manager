package com.devstudio.expensemanager.presentation.mainScreen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devstudio.core_data.repository.UserDataRepository
import com.devstudio.profile.viewmodels.EditableSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    userPreferencesDataStore: UserDataRepository
) : ViewModel() {
    val mainUiState: StateFlow<MainUiState> = userPreferencesDataStore.userData.map { userData ->
        MainUiState.Success(
            MainUiData(
                settings = EditableSettings(
                    theme = userData.theme
                )
            )
        )
    }.stateIn(
        scope = viewModelScope,
        started = kotlinx.coroutines.flow.SharingStarted.Eagerly,
        initialValue = MainUiState.Loading
    )


}

sealed class MainUiState {
    object Loading : MainUiState()
    data class Success(val data: MainUiData) : MainUiState()
}

data class MainUiData(
    val settings: EditableSettings
)