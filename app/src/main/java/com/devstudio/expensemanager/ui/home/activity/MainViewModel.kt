package com.devstudio.expensemanager.ui.home.activity

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devstudio.core_data.UserPreferences
import com.devstudio.profile.EditableSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userPreferencesDataStore: DataStore<UserPreferences>
) : ViewModel() {
    val mainUiState: StateFlow<MainUiState> = userPreferencesDataStore.data.map { userData ->
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