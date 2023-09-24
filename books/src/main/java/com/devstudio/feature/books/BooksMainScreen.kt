package com.devstudio.feature.books

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devstudio.expensemanager.db.models.Books

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BooksMainScreenRoute() {
    val booksViewModel = hiltViewModel<BooksViewModel>()
//    ModalBottomSheet(onDismissRequest = { }) {
    BooksMainScreen(booksViewModel)
//    }
}

@Composable
fun BooksMainScreen(booksViewModel: BooksViewModel) {
        val booksUiState by booksViewModel.booksUiState.collectAsState()
    when (booksUiState) {
        is BooksUiState.Loading -> {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                CircularProgressIndicator()
            }
        }

        is BooksUiState.COMPLETED -> {
            val books = (booksUiState as BooksUiState.COMPLETED).books
            if (books.isNotEmpty()) {
                LazyColumn(content = {
                    items(books.size) { index ->
                        val book = books[index]
                        BookItem(book)
                    }
                })
            } else {
                Column {
                    Text(text = "No Books Found", modifier = Modifier.padding(16.dp))
                }
            }
        }

        is BooksUiState.Error -> {
        }
    }
}

@Composable
fun BookItem(book: Books) {
    Text(text = book.name, modifier = Modifier.padding(vertical = 16.dp, horizontal = 20.dp))
}


sealed interface BooksUiState {
    object Loading : BooksUiState

    class COMPLETED(val books: List<Books>) : BooksUiState

    object Error : BooksUiState
}