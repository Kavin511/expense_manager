package com.devstudio.expensemanager.ui.home.composables

import HomeActions
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.sharp.Home
import androidx.compose.material.icons.twotone.AccountCircle
import androidx.compose.material.icons.twotone.Category
import androidx.compose.material.icons.twotone.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
import com.devstudioworks.ui.components.ExpressWalletFab
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
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
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
                }, scrollBehavior = scrollBehavior)
            },
        ) {
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
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
    val selectedIndex = remember {
        mutableStateOf(0)
    }
    val bottomNavigationList = getBottomNavigationItems()
    BottomAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = appColors.material.secondaryContainer,
                shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
            )
            .height(56.dp),
        content = {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                itemsIndexed(bottomNavigationList) { index, it ->
                    Box(modifier = Modifier
                        .clickable {
                            selectedIndex.value = index
                            navController.navigate(it.navigationRoute) {
                                launchSingleTop = true
                            }
                        }
                        .fillMaxHeight()
                        .fillParentMaxSize(0.33f)) {

                        Icon(
                            if (selectedIndex.value == index) it.selectedIcon else it.unselectedIcon,
                            contentDescription = it.name,
                            modifier = Modifier
                                .fillParentMaxSize(0.5f)
                                .align(alignment = Alignment.Center)
                        )
                    }
                }
            }
        },
        containerColor = appColors.material.surfaceVariant
    )
}

@Composable
private fun getBottomNavigationItems(): List<BottomNavigationItem> {
    return listOf(
        BottomNavigationItem(
            name = stringResource(id = com.devstudio.expensemanager.R.string.transaction),
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            navigationRoute = ExpressWalletAppState.HomeScreen.route
        ),
        BottomNavigationItem(
            name = stringResource(id = com.devstudio.expensemanager.R.string.category),
            selectedIcon = Icons.Rounded.Category,
            unselectedIcon = Icons.Outlined.Category,
            navigationRoute = ExpressWalletAppState.HomeScreen.CategoryScreen.route
        ),
        BottomNavigationItem(
            name = stringResource(id = com.devstudio.expensemanager.R.string.profile),
            selectedIcon = Icons.Rounded.AccountCircle,
            unselectedIcon = Icons.Outlined.AccountCircle,
            navigationRoute = ExpressWalletAppState.HomeScreen.AccountScreen.route
        )
    )
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
