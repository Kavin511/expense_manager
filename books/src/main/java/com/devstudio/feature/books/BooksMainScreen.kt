package com.devstudio.feature.books

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.devstudio.expensemanager.db.models.Books
import com.devstudioworks.ui.theme.appColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BooksMainScreen(
    booksViewModel: BooksViewModel = hiltViewModel(),
    navController: NavHostController,
    callback: () -> Unit
) {
    val booksUiState by booksViewModel.booksUiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val sheetState: SheetState = rememberModalBottomSheetState()
    ModalBottomSheet(onDismissRequest = {
        closeSheet(coroutineScope, navController, callback, sheetState)
    }, sheetState = sheetState, scrimColor = Color(0x4D87878A)) {
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
                            BookItem(book) {
                                closeSheet(coroutineScope, navController, callback, sheetState)
                                booksViewModel.storeSelectedBook(book.id)
                            }
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
}

@OptIn(ExperimentalMaterial3Api::class)
private fun closeSheet(
    coroutineScope: CoroutineScope,
    navController: NavHostController,
    callback: () -> Unit,
    sheetState: SheetState
) {
    coroutineScope.launch {
        navController.popBackStack()
        callback.invoke()
        sheetState.hide()
    }
}

@Composable
fun BookItem(book: Books, itemSelectionCallback: (Books) -> Unit) {
    Text(
        text = book.name,
        modifier = Modifier
            .padding(vertical = 16.dp, horizontal = 20.dp)
            .clickable {
                itemSelectionCallback.invoke(book)
            })
}


sealed interface BooksUiState {
    object Loading : BooksUiState

    class COMPLETED(val books: List<Books>) : BooksUiState

    object Error : BooksUiState
}
