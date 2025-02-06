package com.devstudio.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.devstudio.database.dao.BooksDao
import com.devstudio.database.dao.CategoryDao
import com.devstudio.database.dao.TransactionDao
import com.devstudio.database.models.Books
import com.devstudio.database.models.Category
import com.devstudio.database.models.Transaction

@Database(entities = [Transaction::class, Category::class, Books::class], version = 6, exportSchema = false)
abstract class ExpenseManagerDataBase : RoomDatabase() {
    abstract fun transactionsDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun booksDao(): BooksDao
}
