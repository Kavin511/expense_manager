package com.devstudio.expensemanager.ui.home.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devstudio.core_data.repository.UserDataRepository
import com.devstudio.data.model.UserPreferencesData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted.Companion.Eagerly
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    userDataRepository: UserDataRepository
) : ViewModel() {
    val mainUiState: StateFlow<MainUiState> = userDataRepository.userData.map { userData ->
        MainUiState.Success(
            MainUiData(
                settings = userData
            )
        )
    }.stateIn(
        scope = viewModelScope,
        started = Eagerly,
        initialValue = MainUiState.Loading
    )


}

sealed class MainUiState {
    object Loading : MainUiState()
    data class Success(val data: MainUiData) : MainUiState()
}

data class MainUiData(
    val settings: UserPreferencesData
)