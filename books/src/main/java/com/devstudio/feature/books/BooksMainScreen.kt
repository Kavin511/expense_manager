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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.devstudio.expensemanager.db.models.Books
import com.devstudioworks.ui.components.InputDialog
import com.devstudioworks.ui.components.InputEnterDialog
import com.devstudioworks.ui.icons.EMAppIcons
import com.devstudioworks.ui.theme.appColors
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BooksMainScreen(
    sheetState: SheetState,
    hideBottomSheet: (Long?) -> Unit
) {
    val booksViewModel: BooksViewModel  = viewModel()
    val booksUiState by booksViewModel.booksUiState.collectAsState()
    var shouldShowBookCreationDialog by remember {
        mutableStateOf(false)
    }
    ModalBottomSheet(onDismissRequest = {
        hideBottomSheet.invoke(null)
    }, sheetState = sheetState) {
        when (booksUiState) {
            is BooksUiState.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    CircularProgressIndicator()
                }
            }

            is BooksUiState.COMPLETED -> {
                val books = (booksUiState as BooksUiState.COMPLETED).books
                if (shouldShowBookCreationDialog) {
                    InputEnterDialog(
                        inputDialog = InputDialog.Builder.setHeading("Create Book")
                            .setHint("Enter book name").setNegativeButtonText("Cancel")
                            .setPositiveButtonText("Save").setInputLeadIcon(EMAppIcons.Book)
                            .build(), {
                            shouldShowBookCreationDialog = false
                        }
                    ) {
                        booksViewModel.insertBook(it)
                        shouldShowBookCreationDialog = false
                    }
                }
                Column(
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 8.dp)
                ) {
                    BooksHeading {
                        shouldShowBookCreationDialog = true
                    }
                    if (books.isNotEmpty()) {
                        BooksContent(books, hideBottomSheet)
                    } else {
                        Column {
                            Text(text = "No Books Found", modifier = Modifier.padding(16.dp))
                        }
                    }
                }
            }

            is BooksUiState.Error -> {
                Column {
                    Text(text = "No Books Found", modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}

@Composable
private fun BooksContent(
    books: List<Books>,
    hideBottomSheet: (Long?) -> Unit
) {
    LazyColumn(content = {
        items(books.size) { index ->
            val book = books[index]
            BookItem(book) {
                hideBottomSheet.invoke(it.id)
            }
        }
    })
}

@Composable
private fun BooksHeading(createBookCallback: () -> Unit) {
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
            contentDescription = "Add books",
            modifier = Modifier.clickable {
                createBookCallback.invoke()
            }
        )
    }
}

@Composable
fun BookItem(book: Books, itemSelectionCallback: (Books) -> Unit) {
    Text(
        text = book.name,
        modifier = Modifier
            .fillMaxWidth()
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
