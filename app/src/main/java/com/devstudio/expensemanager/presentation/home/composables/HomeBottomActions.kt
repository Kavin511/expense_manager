package com.devstudio.expensemanager.presentation.home.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.devstudio.core_model.models.ExpressWalletAppState
import com.devstudio.expensemanager.R
import com.devstudioworks.ui.theme.appColors

/**
 * @Author: Kavin
 * @Date: 16/12/23
 */

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
            .height(56.dp), content = {
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
        }, containerColor = appColors.material.surfaceVariant
    )
}

@Composable
private fun getBottomNavigationItems(): List<BottomNavigationItem> {
    return listOf(
        BottomNavigationItem(
            name = stringResource(id = R.string.transaction),
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            navigationRoute = ExpressWalletAppState.HomeScreen.route
        ), BottomNavigationItem(
            name = stringResource(id = R.string.category),
            selectedIcon = Icons.Rounded.Category,
            unselectedIcon = Icons.Outlined.Category,
            navigationRoute = ExpressWalletAppState.HomeScreen.CategoryScreen.route
        ), BottomNavigationItem(
            name = stringResource(id = R.string.profile),
            selectedIcon = Icons.Rounded.AccountCircle,
            unselectedIcon = Icons.Outlined.AccountCircle,
            navigationRoute = ExpressWalletAppState.HomeScreen.AccountScreen.route
        )
    )
}