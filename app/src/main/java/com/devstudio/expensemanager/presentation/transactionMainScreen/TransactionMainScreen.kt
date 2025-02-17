package com.devstudio.expensemanager.presentation.transactionMainScreen

import HomeActions
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.navigation.NavHostController
import com.devstudio.core.designsystem.R
import com.devstudio.expensemanager.presentation.home.composables.HomeSnackBar
import com.devstudio.expensemanager.presentation.transactionMainScreen.model.BookEvent
import com.devstudio.expensemanager.presentation.transactionMainScreen.model.TransactionEvents
import com.devstudio.transactions.acivity.TransactionActivity
import com.devstudio.transactions.composables.transacionDashboard.TransactionDashBoard
import com.devstudio.transactions.models.TransactionUiState
import com.devstudioworks.ui.components.DefaultLoader
import com.devstudioworks.ui.components.ExpressWalletFab
import com.devstudioworks.ui.icons.EMAppIcons
import com.devstudioworks.ui.theme.appColors

/**
 * @Author: Kavin
 * @Date: 15/10/23
 */

@ExperimentalMaterial3Api
@Composable
fun TransactionMainScreen(
    snackBarHostState: SnackbarHostState,
    navController: NavHostController,
    scrollBehavior: TopAppBarScrollBehavior,
    transactionEvents: TransactionEvents,
    uiState: TransactionUiState,
) {
    val booksEvent = transactionEvents.booksEventCallback
    when (uiState) {
        is TransactionUiState.Loading -> {
            DefaultLoader()
        }

        is TransactionUiState.Success -> {
            Scaffold(
                snackbarHost = {
                    SnackbarHost(hostState = snackBarHostState) {
                        HomeSnackBar(snackBarHostState)
                    }
                },
                floatingActionButton = {
                    AddTransactions()
                },
                topBar = {
                    TopAppBar(title = {
                        BookSelectionTitle(uiState.data.bookName) {
                            booksEvent.invoke(BookEvent(true))
                        }
                    }, actions = {
                        HomeActions(navController, snackBarHostState)
                    }, scrollBehavior = scrollBehavior)
                },
            ) {
                Box(
                    contentAlignment = Alignment.TopCenter,
                    modifier = Modifier
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                        .fillMaxSize()
                        .padding(it),
                ) {
                    TransactionDashBoard(uiState, transactionEvents.filterEvent)
                }
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
