package com.devstudio.core_data.repository

import com.devstudio.data.model.Theme
import com.devstudio.data.model.UserPreferencesData
import com.example.datastore.di.DataSourceModule
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * @Author: Kavin
 * @Date: 23/09/23
 */

interface UserDataRepository {
    suspend fun updateSelectedBookId(id: Long)
    suspend fun updateTheme(theme: Theme)
    suspend fun getSelectedBookId(): Flow<Long>

    val userData: Flow<UserPreferencesData>
}

class UserDataRepositoryImpl @Inject constructor(val userDataSource: DataSourceModule) :
    UserDataRepository {
    override val userData: Flow<UserPreferencesData>
        get() = userDataSource.userData

    override suspend fun updateSelectedBookId(id: Long)  = userDataSource.updateSelectedBookId(id)
    override suspend fun updateTheme(theme: Theme) = userDataSource.updateTheme(theme)
    override suspend fun getSelectedBookId(): Flow<Long> = userDataSource.getSelectedBookId()
}

