package com.devstudio.expensemanager.ui.home.composables

import HomeActions
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.devstudio.transactions.acivity.TransactionActivity
import com.devstudio.transactions.composables.transacionDashboard.TransactionDashBoard
import com.devstudio.transactions.composables.transactionFilter.TransactionFilterBottomSheet
import com.devstudioworks.core.ui.R
import kotlinx.coroutines.launch


@ExperimentalMaterial3Api
@Composable
fun HomeScreen(navController: NavHostController) {
    val coroutineScope = rememberCoroutineScope()
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = SheetState(
            initialValue = SheetValue.Hidden, skipPartiallyExpanded = true
        )
    )
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
            floatingActionButton = {
                AddTransactions()
            },
            topBar = {
                TopAppBar(title = { Text(stringResource(id = R.string.app_name)) }, actions = {
                    HomeActions(navController)
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
