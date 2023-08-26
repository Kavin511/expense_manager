package com.devstudio.profile.viewmodels

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devstudio.account.R
import com.devstudio.core_data.Theme_proto
import com.devstudio.core_data.UserPreferences
import com.devstudio.core_data.copy
import com.devstudio.core_data.repository.Remainder
import com.devstudio.core_data.repository.RemainderRepository
import com.devstudio.core_data.repository.TransactionsRepository
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val transactionRepository: TransactionsRepository,
    private val remainderRepository: RemainderRepository,
    val userPreferencesDataStore: DataStore<UserPreferences>
) :
    ViewModel() {

    val profileUiState: StateFlow<ProfileUiState> = userPreferencesDataStore.data.map { userData ->
        ProfileUiState.Success(
            EditableSettings(
                theme = userData.theme
            )
        )
    }.stateIn(scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = ProfileUiState.Loading)

    fun getTotalAssets(): Double {
        return transactionRepository.getTotalAssets()
    }

    fun getTotalNumberOfTransactions(): Int {
        return transactionRepository.getTotalTransactionCount()
    }

    suspend fun updateTheme(theme: Theme_proto) {
        userPreferencesDataStore.updateData {
            it.copy {
                this.theme = theme
            }
        }
    }

    fun getActiveDays(): Long {
        return Calendar.getInstance().timeInMillis - (transactionRepository.transactions()
            .lastOrNull()?.transactionDate?.toLong() ?: 0L)
    }

    fun authenticateUser(context: Context) {
        BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(context.resources.getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(true)
                    .build()
            )
            .build();
    }

    suspend fun getTheme() {
        return userPreferencesDataStore.data.collectLatest {
            it.theme
        }
    }

    fun setRemainders(remainder: List<Remainder>,context: Context) {
        remainderRepository.setRemainders(remainder,context)
    }
}

data class EditableSettings(
    val theme: Theme_proto
)

sealed interface ProfileUiState {
    object Loading : ProfileUiState
    data class Success(val data: EditableSettings) : ProfileUiState
}