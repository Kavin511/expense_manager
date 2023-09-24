package com.example.datastore.di

import androidx.datastore.core.DataStore
import com.devstudio.core_data.Theme_proto
import com.devstudio.core_data.UserPreferences
import com.devstudio.core_data.copy
import com.devstudio.core_model.models.Theme
import com.devstudio.core_model.models.Theme.DARK
import com.devstudio.core_model.models.Theme.LIGHT
import com.devstudio.core_model.models.Theme.SYSTEM
import com.devstudio.core_model.models.UserPreferencesData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * @Author: Kavin
 * @Date: 23/09/23
 */
class DataSourceModule @Inject constructor(
    private val userPreferencesDataStore: DataStore<UserPreferences>
) {
    suspend fun updateSelectedBookId(id: Long) {
        userPreferencesDataStore.updateData {
            it.copy { this.selectedBookId = id }
        }
    }

    suspend fun updateTheme(theme: Theme) {
        userPreferencesDataStore.updateData {
            it.copy {
                this.theme = when (theme) {
                    LIGHT -> Theme_proto.LIGHT
                    DARK -> Theme_proto.DARK
                    SYSTEM -> Theme_proto.SYSTEM_DEFAULT
                }
            }
        }
    }

    fun getSelectedBookId(): Flow<Long> {
        return userPreferencesDataStore.data.map { it.selectedBookId }
    }

    val userData: Flow<UserPreferencesData>
        get() = userPreferencesDataStore.data.map { userData ->
            UserPreferencesData(
                theme = when (userData.theme) {
                    Theme_proto.LIGHT -> LIGHT
                    Theme_proto.DARK -> DARK
                    else -> SYSTEM
                }, selectedBookId = userData.selectedBookId
            )
        }
}