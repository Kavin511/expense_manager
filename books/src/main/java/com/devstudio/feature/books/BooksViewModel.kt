package com.devstudio.feature.books

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devstudio.core_data.repository.BooksRepository
import com.devstudio.core_data.repository.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BooksViewModel @Inject constructor(private val booksRepository: BooksRepository,private val userDataRepository: UserDataRepository) : ViewModel() {
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

    fun storeSelectedBook(id: Long) {
        viewModelScope.launch {
            userDataRepository.updateSelectedBookId(id)
        }
    }
}