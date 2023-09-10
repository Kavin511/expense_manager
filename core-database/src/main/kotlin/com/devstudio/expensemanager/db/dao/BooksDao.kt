package com.devstudio.expensemanager.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.devstudio.expensemanager.db.models.Books

/**
 * @Author: Kavin
 * @Date: 06/09/23
 */
@Dao
interface BooksDao {
    @Insert
    fun insertBooks(books: Books)

    @Query("SELECT * FROM BOOKS_TABLE")
    fun getBooks(): List<Books>
}