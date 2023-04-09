package com.devstudio.core_data.di

import com.devstudio.core_data.TransactionsRepository
import com.devstudio.core_data.TransactionsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class TransactionModule {
    @Binds
    abstract fun providesTransactionRepository(transactionsRepositoryImpl: TransactionsRepositoryImpl): TransactionsRepository
}