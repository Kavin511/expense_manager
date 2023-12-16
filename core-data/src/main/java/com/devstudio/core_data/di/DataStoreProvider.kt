package com.devstudio.core_data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.devstudio.core_data.UserPreferences
import com.devstudio.core_data.datastore.UserPreferenceSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreProvider {
    @Provides
    @Singleton
    fun provideDataSourceModule(
        userPreferenceSerializer: UserPreferenceSerializer,
        @ApplicationContext context: Context
    ): DataStore<UserPreferences> {
        return DataStoreFactory.create(
            serializer = userPreferenceSerializer,
            scope = CoroutineScope(Dispatchers.IO),
            migrations = listOf()
        ) {
            context.dataStoreFile("user_preferences.pb")
        }

    }
}