package com.devstudio.category.composables

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.devstudio.category.CategoryViewModel
import com.devstudio.category.R
import com.devstudio.category.listeners.CategoryCallback
import com.devstudio.expensemanager.db.models.Category
import com.devstudioworks.ui.components.ExpressWalletFab
import com.devstudioworks.ui.theme.appColors

@Composable
fun CategoryFloatingActionButton() {
    val context = LocalContext.current
    val categoryViewModel = hiltViewModel<CategoryViewModel>()
    var shouldShowDialog by remember {
        mutableStateOf(false)
    }
    if (shouldShowDialog) {
        val category = Category(categoryType = "")
        CreateCategoryDialog(
            context,
            category,
            object : CategoryCallback {
                override fun onDismiss() {
                    shouldShowDialog = false
                }

                override fun onAddCategory(category: Category) {
                    shouldShowDialog = false
                    categoryViewModel.insertCategory(category)
                }
            },
        )
    }
    ExpressWalletFab(appColors, stringResource(R.string.add_category)) {
        shouldShowDialog = true
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
) {
    TopAppBar(
        title = {
            Text(text = "Categories")
        },
        scrollBehavior = scrollBehavior,
    )
}
