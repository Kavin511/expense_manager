package com.devstudio.expensemanager.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.devstudio.expensemanager.db.dao.BooksDao
import com.devstudio.expensemanager.db.dao.CategoryDao
import com.devstudio.expensemanager.db.dao.TransactionDao
import com.devstudio.expensemanager.db.models.Books
import com.devstudio.expensemanager.db.models.Category
import com.devstudio.expensemanager.db.models.Transaction

@Database(entities = [Transaction::class,Category::class,Books::class], version = 5, exportSchema = false)
abstract class ExpenseManagerDataBase : RoomDatabase() {
    abstract fun transactionsDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun booksDao(): BooksDao
}