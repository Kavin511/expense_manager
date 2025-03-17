package com.devstudio.profile.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devstudio.account.R
import com.devstudio.data.model.Theme
import com.devstudio.data.repository.Remainder
import com.devstudio.data.repository.RemainderRepository
import com.devstudio.data.repository.TransactionsRepository
import com.devstudio.data.repository.UserDataRepository
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.koin.dsl.module

class ProfileViewModel(
    private val transactionRepository: TransactionsRepository,
    private val remainderRepository: RemainderRepository,
    val userPreferencesDataStore: UserDataRepository,
) : ViewModel() {
    val profileUiState: StateFlow<ProfileUiState> =
        userPreferencesDataStore.userData.map { userData ->
            ProfileUiState.Success(
                EditableSettings(
                    theme = userData.theme,
                ),
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = ProfileUiState.Loading
        )

    fun getTotalAssets(): Double {
        return transactionRepository.getTotalAssets()
    }

    fun getTotalNumberOfTransactions(): Int {
        return transactionRepository.getTotalTransactionCount()
    }

    suspend fun updateTheme(theme: Theme) {
        userPreferencesDataStore.updateTheme(theme)
    }

    fun authenticateUser(context: Context) {
        BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(context.resources.getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(true)
                    .build(),
            )
            .build()
    }

    fun setRemainders(remainder: List<Remainder>, context: Context) {
        remainderRepository.setRemainders(remainder, context)
    }
}

data class EditableSettings(
    val theme: Theme,
)

sealed interface ProfileUiState {
    object Loading : ProfileUiState
    data class Success(val data: EditableSettings) : ProfileUiState
}
