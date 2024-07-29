package com.devstudio.category.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devstudio.category.CategoryViewModel
import com.devstudio.category.R
import com.devstudio.category.listeners.CategoryCallback
import com.devstudio.database.models.Category
import com.devstudio.designSystem.appColors
import com.devstudio.designSystem.components.MaterialAlert
import com.devstudio.designSystem.icons.EMAppIcons

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategoryList(categoryStateList: List<Category>) {
    if (categoryStateList.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(color = appColors.material.surface),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Tap + to create categories",
            )
        }
    } else {
        val categoryViewModel: CategoryViewModel = hiltViewModel()
        val context = LocalContext.current
        var shouldShowDialog by remember {
            mutableStateOf(false)
        }
        var selectedCategory by remember {
            mutableStateOf(Category(categoryType = ""))
        }
        if (shouldShowDialog) {
            CreateCategoryDialog(
                context,
                selectedCategory,
                object : CategoryCallback {

                    override fun onDismiss() {
                        shouldShowDialog = false
                    }

                    override fun onAddCategory(category: Category) {
                        shouldShowDialog = false
                        categoryViewModel.updateCategory(category)
                    }
                },
            )
        }

        val shouldShowDeleteDialog = remember {
            mutableStateOf(false)
        }
        if (shouldShowDeleteDialog.value) {
            MaterialAlert(
                title = stringResource(R.string.delete_category),
                icon = EMAppIcons.Delete,
                content = stringResource(R.string.category_once_deleted_can_t_be_recovered_transactions_for_the_corresponding_category_won_t_be_deleted),
                positiveText = "Delete",
                negativeText = "No",
                positiveCallback = {
                    categoryViewModel.deleteTransaction(selectedCategory)
                    shouldShowDeleteDialog.value = false
                },
                negativeCallback = {
                    shouldShowDeleteDialog.value = false
                },
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
        ) {
            items(categoryStateList) {
                Card(
                    modifier = Modifier
                        .padding(vertical = 4.dp, horizontal = 16.dp)
                        .combinedClickable(onLongClick = {
                            shouldShowDeleteDialog.value = true
                        }) {
                            shouldShowDialog = true
                            selectedCategory = it
                        },
                ) {
                    Text(
                        text = it.name,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                    )
                }
            }
        }
    }
}
