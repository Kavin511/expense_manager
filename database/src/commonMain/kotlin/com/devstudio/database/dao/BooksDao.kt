package com.devstudio.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.IGNORE
import androidx.room.Query
import com.devstudio.database.models.Books
import kotlinx.coroutines.flow.Flow

/**
 * @Author: Kavin
 * @Date: 06/09/23
 */
@Dao
interface BooksDao {
    @Insert(onConflict = IGNORE)
    fun insertBook(books: Books)

    @Query("SELECT * FROM BOOKS_TABLE")
    fun getBooksFlow(): Flow<List<Books>>

    @Query("SELECT * FROM BOOKS_TABLE")
    fun getBooks(): List<Books>

    @Query("SELECT * FROM BOOKS_TABLE WHERE ID=:id")
    fun getBookById(id: Long): Books?
    @Query("SELECT * FROM BOOKS_TABLE WHERE name=:bookName")
    fun findBookByName(bookName: String): Books?
}