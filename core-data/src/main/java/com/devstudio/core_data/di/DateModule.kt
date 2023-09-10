package com.devstudio.core_data.di

import com.devstudio.core_data.repository.BooksRepository
import com.devstudio.core_data.repository.CategoryRepository
import com.devstudio.core_data.repository.CategoryRepositoryImpl
import com.devstudio.core_data.repository.BooksRepositoryInterface
import com.devstudio.core_data.repository.RemainderRepository
import com.devstudio.core_data.repository.RemainderRepositoryInterface
import com.devstudio.core_data.repository.TransactionsRepository
import com.devstudio.core_data.repository.TransactionsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class DateModule {
    @Binds
    abstract fun providesTransactionRepository(transactionsRepositoryImpl: TransactionsRepositoryImpl): TransactionsRepository

    @Binds
    abstract fun providesCategoryRepository(categoryRepositoryImpl: CategoryRepositoryImpl): CategoryRepository

    @Binds
    abstract fun providesRemainderRepository(remainderRepository: RemainderRepository): RemainderRepositoryInterface

    @Binds
    abstract fun providesBooksRepository(booksRepository: BooksRepository): BooksRepositoryInterface
}