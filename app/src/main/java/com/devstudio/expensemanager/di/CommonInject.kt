package com.devstudio.expensemanager.di

import android.app.Application
import com.devstudio.category.CategoryViewModel
import com.devstudio.data.datastore.DataSourceModule
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
import com.devstudio.expensemanager.presentation.home.viewmodel.HomeActionsViewModel
import com.devstudio.expensemanager.presentation.mainScreen.viewmodel.MainViewModel
import com.devstudio.feature.books.BooksViewModel
import com.devstudio.profile.viewmodels.ProfileViewModel
import com.devstudio.sharedmodule.importData.presentation.CsvImportViewModel
import com.devstudio.transactions.viewmodel.GetTransactionBook
import com.devstudio.transactions.viewmodel.TransactionViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * @Author: Kavin
 * @Date: 14/03/25
 */
val commonModules = module {
    singleOf(::UserDataRepositoryImpl) { bind<UserDataRepository>() }
    viewModel { HomeActionsViewModel(androidContext() as Application) }
    viewModelOf(::MainViewModel)
    viewModel { BooksViewModel(get(), get()) }
    viewModel { CategoryViewModel(get()) }
    viewModel { ProfileViewModel(get(), get(), get()) }
    viewModel { CsvImportViewModel() }
    viewModel { TransactionViewModel(get(), get(), get(), get()) }
    single { DataSourceModule() }
    single<UserDataRepository> { UserDataRepositoryImpl() }
    single { GetTransactionBook() }

    singleOf(::TransactionsRepositoryImpl) { bind<TransactionsRepository>() }
    singleOf(::CategoryRepositoryImpl) { bind<CategoryRepository>() }
    singleOf(::RemainderRepository) { bind<RemainderRepositoryInterface>() }
    singleOf(::BooksRepositoryImpl) {
        bind<BooksRepository>()
    }

}