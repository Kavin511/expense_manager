package com.devstudio.expensemanager.ui.home.composables

import HomeActions
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.devstudio.core_model.models.ExpressWalletAppState
import com.devstudio.transactions.acivity.TransactionActivity
import com.devstudio.transactions.composables.transacionDashboard.TransactionDashBoard
import com.devstudio.transactions.composables.transactionFilter.TransactionFilterBottomSheet
import com.devstudioworks.core.ui.R
import com.devstudioworks.ui.theme.appColors
import kotlinx.coroutines.launch


@ExperimentalMaterial3Api
@Composable
fun HomeScreen(
    navController: NavHostController,
    bottomSheetScaffoldState: BottomSheetScaffoldState
) {
    val coroutineScope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    BottomSheetScaffold(modifier = Modifier.pointerInput(Unit) {
        detectTapGestures {
            coroutineScope.launch {
                bottomSheetScaffoldState.bottomSheetState.hide()
            }
        }
    }, sheetContent = {
        TransactionFilterBottomSheet(coroutineScope, bottomSheetScaffoldState)
    }, scaffoldState = bottomSheetScaffoldState) {
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
                TopAppBar(title = { Text(stringResource(id = R.string.app_name)) }, actions = {
                    HomeActions(navController, snackBarHostState)
                })
            },
        ) {
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                TransactionDashBoard(bottomSheetScaffoldState)
            }
        }
    }
}
@Composable
fun HomeBottomActions(navController: NavHostController) {
    BottomAppBar(
        actions = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Box(modifier = Modifier.clickable {
                    navController.navigate(ExpressWalletAppState.HomeScreen.route) {
                        launchSingleTop = true
                    }
                }) {
                    Icon(Icons.Filled.Home, contentDescription = "Transaction")
                }
                Box(modifier = Modifier.clickable {
                    navController.navigate(ExpressWalletAppState.HomeScreen.CategoryScreen.route) {
                        launchSingleTop = true
                    }
                }) {
                    Icon(Icons.Filled.Category, contentDescription = "Category")
                }
                Box(modifier = Modifier.clickable {
                    navController.navigate(ExpressWalletAppState.HomeScreen.AccountScreen.route) {
                        launchSingleTop = true
                    }
                }) {
                    Icon(Icons.Filled.AccountCircle, contentDescription = "Profile")
                }
            }
        },
        floatingActionButton = {
            AddTransactions()
        },
        containerColor = appColors.material.surfaceVariant
    )
}

data class BottomNavigationItem(
    val name: String,
    val icon: Icons
)

@Composable
private fun AddTransactions() {
    val context = LocalContext.current
    FloatingActionButton(
        onClick = onClickAddTransaction(context),
        modifier = Modifier.padding(start = 6.dp, end = 6.dp, top = 4.dp, bottom = 4.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = stringResource(R.string.add_transaction)
        )
    }
}


@Composable
fun onClickAddTransaction(context: Context): () -> Unit = {
    val intent = Intent(context, TransactionActivity::class.java)
    context.startActivity(intent)
}
