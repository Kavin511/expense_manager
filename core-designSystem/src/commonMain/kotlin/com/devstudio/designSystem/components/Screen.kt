package com.devstudio.designSystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.devstudio.designSystem.defaultHorizontalPadding
import com.devstudio.designSystem.icons.EMAppIcons
import com.devstudio.designSystem.maxScreenWidth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Screen(
    title: String = "",
    navController: NavController = rememberNavController(),
    shouldNavigateUp: Boolean = false,
    action: @Composable () -> Unit = {},
    fab: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
    Scaffold(topBar = {
        if (title.isNotEmpty()) {
            TopAppBar(title = {
                Text(text = title)
            }, actions = {
                action()
            }, scrollBehavior = scrollBehavior, navigationIcon = {
                if (shouldNavigateUp) {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(imageVector = EMAppIcons.Back, contentDescription = "Navigate Up")
                    }
                }
            })
        }
    }, floatingActionButton = {
        fab()
    }) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Surface(
                modifier = Modifier.padding(it)
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .widthIn(max = maxScreenWidth)
                    .align(alignment = Alignment.CenterHorizontally)
                    .padding(horizontal = defaultHorizontalPadding),
            ) {
                content()
            }
        }
    }
}