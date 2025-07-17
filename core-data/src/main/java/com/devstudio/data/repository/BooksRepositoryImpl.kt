package com.devstudio.data.repository

import com.devstudio.database.ApplicationModule
import com.devstudio.database.models.Books
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent

class BooksRepositoryImpl(
    private val userDataRepository: UserDataRepository
) : BooksRepository, KoinComponent {
    private val db = ApplicationModule.config.factory.getRoomInstance()
    private val booksDao = db.booksDao()

    override fun getBooksFlow(): Flow<List<Books>> {
        return booksDao.getBooksFlow()
    }

    override fun getBooks(): List<Books> {
        return booksDao.getBooks()
    }

    override fun findBookByName(name: String): Books? {
        return booksDao.findBookByName(name)
    }

    override suspend fun getSelectedBook(): Flow<Long> {
        return userDataRepository.getSelectedBookId()
    }

    override fun getBookById(it: Long): Books? = booksDao.getBookById(it)
    override fun insertBook(it: Books): Long = booksDao.insertBook(it)

    override fun createBook(book: Books) {
    }

    override fun updateBook(book: Books) {
    }
}

interface BooksRepository {
    fun getBooksFlow(): Flow<List<Books>>
    fun createBook(book: Books)
    fun updateBook(book: Books)
    suspend fun getSelectedBook(): Flow<Long>
    fun getBookById(it: Long): Books?
    fun insertBook(it: Books): Long
    fun getBooks(): List<Books>
    fun findBookByName(name: String): Books?
}
