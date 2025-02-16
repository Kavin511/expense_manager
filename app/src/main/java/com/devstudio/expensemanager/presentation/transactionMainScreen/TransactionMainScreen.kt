package com.devstudio.expensemanager.presentation.transactionMainScreen

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.devstudio.designSystem.appColors
import com.devstudio.designSystem.components.DefaultLoader
import com.devstudio.designSystem.components.ExpressWalletFab
import com.devstudio.designSystem.components.Screen
import com.devstudio.designSystem.icons.EMAppIcons
import com.devstudio.expensemanager.R
import com.devstudio.expensemanager.presentation.home.composables.HomeSnackBar
import com.devstudio.expensemanager.presentation.transactionMainScreen.model.BookEvent
import com.devstudio.expensemanager.presentation.transactionMainScreen.model.TransactionEvents
import com.devstudio.model.models.OnEvent
import com.devstudio.transactions.acivity.TransactionActivity
import com.devstudio.transactions.composables.transacionDashboard.TransactionDashBoard
import com.devstudio.transactions.models.BottomSheetEvent
import com.devstudio.transactions.models.TransactionUiState

/**
 * @Author: Kavin
 * @Date: 15/10/23
 */

@ExperimentalMaterial3Api
@Composable
fun TransactionMainScreen(
    snackBarHostState: SnackbarHostState,
    scrollBehavior: TopAppBarScrollBehavior,
    booksEvent: OnEvent,
    uiState: TransactionUiState,
) {
    when (uiState) {
        is TransactionUiState.Loading -> {
            DefaultLoader()
        }

        is TransactionUiState.Success -> {
            Screen(title = {
                BookSelectionTitle(uiState.data.bookName) {
                    booksEvent.invoke(BookEvent(showBottomSheet = true))
                }
            }, action = {
                IconButton(onClick = {
                    booksEvent.invoke(BottomSheetEvent(true, null))
                }) {
                    Icon(Icons.Rounded.MoreVert, "More")
                }
            }, snackbarHost = {
                SnackbarHost(hostState = snackBarHostState) {
                    HomeSnackBar(snackBarHostState)
                }
            }) {
                TransactionDashBoard(uiState, booksEvent)
            }
        }

        else -> {
            Text(text = "Error")
        }
    }
}

@Composable
private fun AddTransactions() {
    val context = LocalContext.current
    ExpressWalletFab(appColors, stringResource(R.string.add_transaction)) {
        onClickAddTransaction(context)
    }
}

fun onClickAddTransaction(context: Context) {
    val intent = Intent(context, TransactionActivity::class.java)
    context.startActivity(intent)
}

@Composable
fun BookSelectionTitle(
    homeScreenData: String,
    bookSelectionEvent: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable {
            bookSelectionEvent.invoke()
        },
    ) {
        Text(text = homeScreenData)
        Image(
            imageVector = EMAppIcons.DropDown,
            colorFilter = ColorFilter.tint(appColors.material.onSurfaceVariant),
            contentDescription = "Book selection arrow",
        )
    }
}
