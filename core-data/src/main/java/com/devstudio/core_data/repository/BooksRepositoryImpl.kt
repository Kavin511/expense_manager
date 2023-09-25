package com.devstudio.core_data.repository

import com.devstudio.expensemanager.db.dao.BooksDao
import com.devstudio.expensemanager.db.models.Books
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BooksRepositoryImpl @Inject constructor(private val booksDao: BooksDao) : BooksRepository {
    override fun getBooks(): List<Books> {
        return booksDao.getBooks()
    }

    override fun getSelectedBook(): Books {
        return booksDao.getBooks()[0]
    }

    override fun getBookById(it: Long): Books?  = booksDao.getBookById(it)
    override fun createBook(book: Books) {
    }

    override fun updateBook(book: Books) {
    }
}

interface BooksRepository {
    fun getBooks(): List<Books>
    fun createBook(book: Books)
    fun updateBook(book: Books)
    fun getSelectedBook(): Books
    fun getBookById(it: Long): Books?
}
