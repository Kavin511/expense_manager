package com.devstudio.profile.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devstudio.account.R
import com.devstudio.core_data.repository.Remainder
import com.devstudio.core_data.repository.RemainderRepository
import com.devstudio.core_data.repository.TransactionsRepository
import com.devstudio.core_data.repository.UserDataRepository
import com.devstudio.data.model.Theme
import com.devstudio.data.model.UserPreferencesData
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val transactionRepository: TransactionsRepository,
    private val remainderRepository: RemainderRepository,
    val userDataRepository: UserDataRepository
) :
    ViewModel() {

    val profileUiState: StateFlow<ProfileUiState> = userDataRepository.userData.map { userData ->
        ProfileUiState.Success(
            userData
        )
    }.stateIn(scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = ProfileUiState.Loading)

    fun getTotalAssets(): Double {
        return transactionRepository.getTotalAssets()
    }

    fun getTotalNumberOfTransactions(): Int {
        return transactionRepository.getTotalTransactionCount()
    }

    fun updateTheme(theme: Theme) {
        viewModelScope.launch {
            userDataRepository.updateTheme(theme)
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

    fun setRemainders(remainder: List<Remainder>,context: Context) {
        remainderRepository.setRemainders(remainder,context)
    }
}

sealed interface ProfileUiState {
    object Loading : ProfileUiState
    data class Success(val data: UserPreferencesData) : ProfileUiState
}