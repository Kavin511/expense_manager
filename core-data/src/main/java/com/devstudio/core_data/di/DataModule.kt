package com.devstudio.core_data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.devstudio.core_data.UserPreferences
import com.devstudio.core_data.repository.BooksRepository
import com.devstudio.core_data.repository.CategoryRepository
import com.devstudio.core_data.repository.CategoryRepositoryImpl
import com.devstudio.core_data.repository.BooksRepositoryInterface
import com.devstudio.core_data.repository.RemainderRepository
import com.devstudio.core_data.repository.RemainderRepositoryInterface
import com.devstudio.core_data.repository.TransactionsRepository
import com.devstudio.core_data.repository.TransactionsRepositoryImpl
import com.devstudio.core_data.repository.UserDataRepository
import com.devstudio.core_data.repository.UserDataRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    abstract fun providesTransactionRepository(transactionsRepositoryImpl: TransactionsRepositoryImpl): TransactionsRepository

    @Binds
    abstract fun providesCategoryRepository(categoryRepositoryImpl: CategoryRepositoryImpl): CategoryRepository

    @Binds
    abstract fun providesRemainderRepository(remainderRepository: RemainderRepository): RemainderRepositoryInterface

    @Binds
    abstract fun providesBooksRepository(booksRepository: BooksRepository): BooksRepositoryInterface

    @Binds
    abstract fun providesUserDataRepository(booksRepository: UserDataRepositoryImpl): UserDataRepository
}