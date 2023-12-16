package com.devstudio.category.composables

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.devstudio.category.listeners.CategoryCallback
import com.devstudio.expensemanager.db.models.Category
import com.devstudio.utils.utils.AppConstants.Companion.supportedTransactionTypes
import com.devstudio.core.designsystem.R


@Composable
fun CreateCategoryDialog(context: Context, category: Category, categoryCallback: CategoryCallback) {
    var categoryName by remember {
        mutableStateOf(category.name)
    }
    var categoryType by remember {
        mutableStateOf(if (category.categoryType.isEmpty()) supportedTransactionTypes[0] else category.categoryType)
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
                    .padding(16.dp)
            ) {
                TextField(value = categoryName, onValueChange = {
                    if (it.isNotBlank()) {
                        isError = false
                    }
                    categoryName = it
                }, isError = isError)
                LazyColumn {
                    items(supportedTransactionTypes.size) { index ->
                        Row {
                            RadioButton(
                                selected = categoryType == supportedTransactionTypes[index],
                                onClick = {
                                    categoryType = supportedTransactionTypes[index]
                                },
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                            Text(text = supportedTransactionTypes[index], modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .clickable {
                                    categoryType = supportedTransactionTypes[index]
                                })
                        }
                    }
                }


                OutlinedButton(
                    onClick = {
                        if (categoryName.isNotBlank()) {
                            category.name = categoryName
                            category.categoryType = categoryType
                            category.timeStamp = System.currentTimeMillis()
                            categoryCallback::onAddCategory.invoke(category)
                        } else {
                            isError = true
                            Toast.makeText(
                                context,
                                "Category name cannot be empty",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .padding(8.dp)
                        .align(Alignment.CenterHorizontally),
                    shape = RoundedCornerShape(dimensionResource(id = R.dimen.default_radius))
                ) {
                    Text(text = if (category.name.isNotBlank()) "Update" else "Add")
                }
            }
        }
    }
}