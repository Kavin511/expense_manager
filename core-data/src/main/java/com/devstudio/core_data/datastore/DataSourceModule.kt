package com.devstudio.core_data.datastore

import androidx.datastore.core.DataStore
import com.devstudio.core_data.Theme_proto
import com.devstudio.core_data.UserPreferences
import com.devstudio.core_data.copy
import com.devstudio.data.model.Theme
import com.devstudio.data.model.Theme.DARK
import com.devstudio.data.model.Theme.LIGHT
import com.devstudio.data.model.Theme.SYSTEM_DEFAULT
import com.devstudio.data.model.UserPreferencesData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataSourceModule @Inject constructor(val userPreferencesDataStore: DataStore<UserPreferences>) {
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
                    SYSTEM_DEFAULT -> Theme_proto.SYSTEM_DEFAULT
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
                    else -> SYSTEM_DEFAULT
                }, selectedBookId = userData.selectedBookId
            )
        }

}

