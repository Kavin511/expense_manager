package com.devstudio.core_data.repository

import com.devstudio.expensemanager.db.dao.BooksDao
import com.devstudio.expensemanager.db.models.Books
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BooksRepository @Inject constructor(private val booksDao: BooksDao) : BooksRepositoryInterface {
    override fun getBooks(): List<Books> {
        return booksDao.getBooks()
    }

    override fun getSelectedBook(): Books {
        return booksDao.getBooks()[0]
    }

    override fun createBook(book: Books) {
    }

    override fun updateBook(book: Books) {
    }
}

interface BooksRepositoryInterface {
    fun getBooks(): List<Books>
    fun createBook(book: Books)
    fun updateBook(book: Books)
    fun getSelectedBook(): Books
}
