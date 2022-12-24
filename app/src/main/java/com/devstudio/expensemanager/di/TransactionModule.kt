package com.devstudio.expensemanager.di

import com.devstudio.expensemanager.db.repository.TransactionsRepository
import com.devstudio.expensemanager.db.repository.TransactionsRepositoryImpl
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