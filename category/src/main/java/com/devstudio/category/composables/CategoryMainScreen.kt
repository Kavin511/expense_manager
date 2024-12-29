package com.devstudio.category.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devstudio.category.CategoryState
import com.devstudio.category.CategoryViewModel
import com.devstudio.utils.utils.AppConstants.Companion.ALL
import com.devstudio.utils.utils.toPascalCase
import com.devstudio.designSystem.components.Screen
import com.devstudio.designSystem.appColors
import com.devstudio.utils.utils.TransactionMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryMainScreen() {
    val categoryViewModel = hiltViewModel<CategoryViewModel>()
    val selectedFilterType = remember {
        mutableStateOf("")
    }
    val topAppBarState = rememberTopAppBarState()
    TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
    Screen(title = "Categories", fab = { CategoryFloatingActionButton() }) {
        Column {
            CategoryActions(selectedFilterType)
            with(categoryViewModel.categoryState.collectAsState()) {
                when (value) {
                    is CategoryState.LOADING -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(color = appColors.material.surface),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                color = appColors.material.onSurface,
                            )
                            categoryViewModel.loadCategoriesForSelectedType(selectedFilterType.value)
                        }
                    }

                    is CategoryState.COMPLETED -> {
                        val categoryList =
                            (categoryViewModel.categoryState.collectAsState().value as CategoryState.COMPLETED).categoryList
                        CategoryList(categoryList.collectAsState(initial = emptyList()).value)
                    }

                    else -> {
                        Text("Error occurred")
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CategoryActions(
    selectedFilterType: MutableState<String>,
) {
    val categoryViewModel = hiltViewModel<CategoryViewModel>()
    LazyRow(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
    ) {
        item {
            FilterChip(
                modifier = Modifier.padding(4.5.dp),
                selected = selectedFilterType.value == "",
                onClick = {
                    selectedFilterType.value = ""
                    categoryViewModel.categoryState.value = CategoryState.LOADING
                },
                label = { Text(text = ALL.toPascalCase()) },
            )
        }

        item {
            FilterChip(
                modifier = Modifier.padding(4.5.dp),
                selected = selectedFilterType.value == TransactionMode.EXPENSE.title,
                onClick = {
                    selectedFilterType.value = TransactionMode.EXPENSE.title
                    categoryViewModel.categoryState.value = CategoryState.LOADING
                },
                label = { Text(text = TransactionMode.EXPENSE.title.toPascalCase()) },
            )
        }
        item {
            FilterChip(
                modifier = Modifier.padding(4.5.dp),
                selected = selectedFilterType.value == TransactionMode.INCOME.title,
                onClick = {
                    selectedFilterType.value = TransactionMode.INCOME.title
                    categoryViewModel.categoryState.value = CategoryState.LOADING
                },
                label = { Text(text = TransactionMode.INCOME.title.toPascalCase()) },
            )
        }

        item {
            FilterChip(
                modifier = Modifier.padding(4.5.dp),
                selected = selectedFilterType.value == TransactionMode.INVESTMENT.title,
                onClick = {
                    selectedFilterType.value = TransactionMode.INVESTMENT.title
                    categoryViewModel.categoryState.value = CategoryState.LOADING
                },
                label = { Text(text = TransactionMode.INVESTMENT.title.toPascalCase()) },
            )
        }
    }
}
