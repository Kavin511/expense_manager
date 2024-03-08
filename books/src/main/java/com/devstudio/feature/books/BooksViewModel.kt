package com.devstudio.feature.books

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devstudio.data.repository.BooksRepository
import com.devstudio.data.repository.UserDataRepository
import com.devstudio.expensemanager.db.models.Books
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BooksViewModel @Inject constructor(
    private val booksRepository: BooksRepository,
    private val userDataRepository: UserDataRepository,
) : ViewModel() {
    val booksUiState = booksRepository.getBooksFlow().map { books ->
        BooksUiState.COMPLETED(books)
    }.stateIn(viewModelScope, started = SharingStarted.Lazily, initialValue = BooksUiState.Loading)

    fun saveSelectedBook(id: Long) {
        viewModelScope.launch {
            userDataRepository.updateSelectedBookId(id)
        }
    }

    fun insertBook(name: String) {
        viewModelScope.launch {
            val book = Books()
            book.name = name
            booksRepository.insertBook(book)
        }
    }
}
