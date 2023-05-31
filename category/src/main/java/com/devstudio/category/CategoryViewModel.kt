package com.devstudio.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devstudio.core_data.repository.CategoryRepository
import com.devstudio.expensemanager.db.models.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(val categoryRepository: CategoryRepository) :
    ViewModel() {
    val categoryState = MutableStateFlow<CategoryState>(CategoryState.LOADING)

    init {
        viewModelScope.launch {
            categoryRepository.getCategoriesFlow().collectLatest {
                categoryState.value = CategoryState.COMPLETED(it)
            }
        }
    }

    fun insertCategory(categoryName: Category) {
        viewModelScope.launch {
            categoryRepository.insertCategory(categoryName)
        }
    }

    fun deleteTransaction(category: Category) {
        return categoryRepository.deleteCategory(category)
    }

    fun updateCategory(category: Category) {
        return categoryRepository.updateCategory(category)
    }
}