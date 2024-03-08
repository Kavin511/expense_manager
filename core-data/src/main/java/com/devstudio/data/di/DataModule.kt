package com.devstudio.data.di

import com.devstudio.data.repository.BooksRepository
import com.devstudio.data.repository.BooksRepositoryImpl
import com.devstudio.data.repository.CategoryRepository
import com.devstudio.data.repository.CategoryRepositoryImpl
import com.devstudio.data.repository.RemainderRepository
import com.devstudio.data.repository.RemainderRepositoryInterface
import com.devstudio.data.repository.TransactionsRepository
import com.devstudio.data.repository.TransactionsRepositoryImpl
import com.devstudio.data.repository.UserDataRepository
import com.devstudio.data.repository.UserDataRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

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
    abstract fun providesBooksRepository(booksRepositoryImpl: BooksRepositoryImpl): BooksRepository

    @Binds
    abstract fun providesUserDataRepository(booksRepository: UserDataRepositoryImpl): UserDataRepository
}
