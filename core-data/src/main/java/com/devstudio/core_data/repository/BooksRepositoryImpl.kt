package com.devstudio.core_data.repository

import com.devstudio.expensemanager.db.dao.BooksDao
import com.devstudio.expensemanager.db.models.Books
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BooksRepositoryImpl @Inject constructor(private val booksDao: BooksDao,private val userDataRepository: UserDataRepository) : BooksRepository {
    override fun getBooks(): List<Books> {
        return booksDao.getBooks()
    }

    override suspend fun getSelectedBook(): Flow<Long> {
        return userDataRepository.getSelectedBookId()
    }

    override fun getBookById(it: Long): Books?  = booksDao.getBookById(it)
    override fun insertBook(it: Books) = booksDao.insertBook(it)

    override fun createBook(book: Books) {
    }

    override fun updateBook(book: Books) {
    }
}

interface BooksRepository {
    fun getBooks(): List<Books>
    fun createBook(book: Books)
    fun updateBook(book: Books)
    suspend fun getSelectedBook(): Flow<Long>
    fun getBookById(it: Long): Books?
    fun insertBook(it: Books)
}
