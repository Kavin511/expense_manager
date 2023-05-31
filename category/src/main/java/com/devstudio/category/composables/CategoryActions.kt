package com.devstudio.category.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devstudio.category.CategoryViewModel
import com.devstudio.category.R
import com.devstudio.category.listeners.CategoryCallback
import com.devstudio.expensemanager.db.models.Category

@Composable
fun CategoryFloatingActionButton() {
    val context = LocalContext.current
    val categoryViewModel = hiltViewModel<CategoryViewModel>()
    var shouldShowDialog by remember {
        mutableStateOf(false)
    }
    if (shouldShowDialog) {
        val category = Category()
        CreateCategoryDialog(context,category,object : CategoryCallback {
            override fun onDismiss() {
                shouldShowDialog = false
            }

            override fun onAddCategory(category: Category) {
                shouldShowDialog = false
                categoryViewModel.insertCategory(category)
            }
        })

    }
    FloatingActionButton(
        onClick = {
            shouldShowDialog = true
        }, modifier = Modifier.padding(start = 6.dp, end = 6.dp, top = 4.dp, bottom = 4.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = stringResource(R.string.add_category)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryTopBar() {
    TopAppBar(title = {
        Text(text = "Categories")
    })
}