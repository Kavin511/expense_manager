package com.devstudioworks.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.waterfallPadding
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
import com.devstudioworks.ui.icons.EMAppIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
inline fun Page(
    title: String,
    navController: NavController = rememberNavController(),
    shouldNavigateUp: Boolean = false,
    crossinline action: @Composable () -> Unit = {},
    crossinline fab: @Composable () -> Unit = {},
    crossinline content: @Composable () -> Unit
) {
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
    Scaffold(topBar = {
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
    }, floatingActionButton = {
        fab()
    }) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier
                    .padding(it)
                    .safeContentPadding()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .widthIn(max = 640.dp)
                    .waterfallPadding()
                    .align(alignment = Alignment.CenterHorizontally),
            ) {
                content()
            }
        }
    }
}