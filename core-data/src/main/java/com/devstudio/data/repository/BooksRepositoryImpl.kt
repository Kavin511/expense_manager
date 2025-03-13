package com.devstudio.data.repository

import android.content.Context
import com.devstudio.database.ApplicationModule
import com.devstudio.database.models.Books
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BooksRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context,
    private val userDataRepository: UserDataRepository
) : BooksRepository {
    val db = ApplicationModule.config.factory.getRoomInstance()
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
