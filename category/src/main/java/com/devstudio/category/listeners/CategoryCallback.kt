package com.devstudio.category.listeners

import com.devstudio.expensemanager.db.models.Category

interface CategoryCallback {
    fun onDismiss()
    fun onAddCategory(category: Category)
}
