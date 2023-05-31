package com.devstudio.category.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devstudio.category.CategoryViewModel
import com.devstudio.category.listeners.CategoryCallback
import com.devstudio.expensemanager.db.models.Category
import com.devstudioworks.ui.components.MaterialAlert


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategoryList(categoryStateList: List<Category>) {
    if (categoryStateList.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Text(
                text = "Tap + to create categories"
            )
        }
    } else {
        val categoryViewModel: CategoryViewModel = hiltViewModel()
        val context = LocalContext.current
        var shouldShowDialog by remember {
            mutableStateOf(false)
        }
        var selectedCategory by remember {
            mutableStateOf(Category())
        }
        if (shouldShowDialog) {
            CreateCategoryDialog(context, selectedCategory, object : CategoryCallback {

                override fun onDismiss() {
                    shouldShowDialog = false
                }

                override fun onAddCategory(category: Category) {
                    shouldShowDialog = false
                    categoryViewModel.updateCategory(category)
                }
            })
        }
        LazyColumn() {
            items(categoryStateList) {

                Card(modifier = Modifier
                    .padding(4.dp)
                    .combinedClickable(onLongClick = {
                        MaterialAlert(
                            context = context,
                            title = "Are you sure to delete this category",
                            negativeText = "No",
                            positiveText = "Delete", positiveCallback = { dialogInterface ->
                                categoryViewModel.deleteTransaction(it)
                                dialogInterface.dismiss()
                            }, negativeCallback = {
                                it.dismiss()
                            }
                        )
                    }) {
                        shouldShowDialog = true
                        selectedCategory = it
                    }) {
                    Text(
                        text = it.name, modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}
