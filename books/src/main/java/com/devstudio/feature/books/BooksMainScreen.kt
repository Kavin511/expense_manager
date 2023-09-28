package com.devstudio.feature.books

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devstudio.expensemanager.db.models.Books
import com.devstudioworks.ui.icons.EMAppIcons
import com.devstudioworks.ui.theme.appColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BooksMainScreen(
    sheetState: SheetState,
    hideBottomSheet: () -> Unit
) {
    val booksViewModel: BooksViewModel = hiltViewModel()
    val booksUiState by booksViewModel.booksUiState.collectAsState()
    ModalBottomSheet(onDismissRequest = {
        hideBottomSheet.invoke()
    }, sheetState = sheetState) {
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
                    Column(
                        modifier = Modifier
                            .padding(vertical = 8.dp, horizontal = 8.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "Select Books",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Image(
                                imageVector = EMAppIcons.RoundedAddCircleOutline,
                                colorFilter = ColorFilter.tint(appColors.material.tertiary),
                                contentDescription = "Add books"
                            )
                        }
                        LazyColumn(content = {
                            items(books.size) { index ->
                                val book = books[index]
                                BookItem(book) {
                                    booksViewModel.storeSelectedBook(book.id)
                                    hideBottomSheet.invoke()
                                }
                            }
                        })
                    }
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
}

@Composable
fun BookItem(book: Books, itemSelectionCallback: (Books) -> Unit) {
    Text(
        text = book.name,
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 8.dp)
            .clickable {
                itemSelectionCallback.invoke(book)
            })
}


sealed interface BooksUiState {
    object Loading : BooksUiState

    class COMPLETED(val books: List<Books>) : BooksUiState

    object Error : BooksUiState
}
