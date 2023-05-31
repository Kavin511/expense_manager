package com.devstudio.category.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.devstudio.category.CategoryState
import com.devstudio.category.CategoryViewModel
import com.devstudioworks.ui.theme.appColors

@Composable
fun CategoryMainScreen() {
    val categoryViewModel = hiltViewModel<CategoryViewModel>()
    with(categoryViewModel.categoryState.collectAsState()) {
        when (this.value) {
            is CategoryState.LOADING -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .background(color = appColors.material.surface),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = appColors.material.onSurface
                    )
                }
            }

            is CategoryState.COMPLETED -> {
                val categoryStateList = (this.value as CategoryState.COMPLETED).categoryState
                Scaffold(topBar = {
                    CategoryTopBar()
                }, floatingActionButton = {
                    CategoryFloatingActionButton()
                }) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(it)
                    ) {
                        CategoryList(categoryStateList)
                    }
                }
            }

            else -> {
                Text("Error occurred")
            }
        }
    }
}


