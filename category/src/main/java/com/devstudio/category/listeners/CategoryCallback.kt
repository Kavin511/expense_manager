package com.devstudio.category.listeners

import com.devstudio.database.models.Category

interface CategoryCallback {
    fun onDismiss()
    fun onAddCategory(category: Category)
}
