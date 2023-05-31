package com.devstudio.category.composables

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.devstudio.category.listeners.CategoryCallback
import com.devstudio.expensemanager.db.models.Category
import com.devstudioworks.core.ui.R


@Composable
fun CreateCategoryDialog(context: Context, category: Category, categoryCallback: CategoryCallback) {
    var categoryValue by remember {
        mutableStateOf(category.name)
    }
    var isError by remember {
        mutableStateOf(false)
    }
    Dialog(onDismissRequest = {
        categoryCallback::onDismiss.invoke()
    }) {
        Card(shape = RoundedCornerShape(dimensionResource(id = R.dimen.default_radius))) {
            Column(
                modifier = Modifier
                    .background(color = Color.White)
                    .padding(16.dp)
            ) {
                TextField(value = categoryValue, onValueChange = {
                    if (it.isNotBlank()) {
                        isError = false
                    }
                    categoryValue = it
                }, isError = isError)

                FilledIconButton(
                    onClick = {
                        if (categoryValue.isNotBlank()) {
                            category.name = categoryValue
                            category.timeStamp = System.currentTimeMillis()
                            categoryCallback::onAddCategory.invoke(
                                category
                            )
                        } else {
                            isError = true
                            Toast.makeText(context, "Invalid category length", Toast.LENGTH_SHORT)
                                .show()
                        }
                    },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .padding(8.dp)
                        .align(Alignment.CenterHorizontally),
                    shape = RoundedCornerShape(dimensionResource(id = R.dimen.default_radius))
                ) {
                    Text(text = "Add")
                }
            }
        }
    }
}