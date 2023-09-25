package com.devstudio.feature.books

import androidx.lifecycle.ViewModel
import com.devstudio.core_data.repository.BooksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class BooksViewModel @Inject constructor(private val booksRepository: BooksRepository) : ViewModel() {
    val booksUiState: MutableStateFlow<BooksUiState> = MutableStateFlow(BooksUiState.Loading)

    init {
        loadBooks()
    }

    private fun loadBooks() {
        booksUiState.value = BooksUiState.Loading
        try {
            val books = booksRepository.getBooks()
            booksUiState.value = BooksUiState.COMPLETED(books)
        } catch (e: Exception) {
            booksUiState.value = BooksUiState.Error
        }
    }
}