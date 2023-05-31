package com.devstudio.core_data.repository

import com.devstudio.expensemanager.db.dao.CategoryDao
import com.devstudio.expensemanager.db.models.Category
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface CategoryRepository {
    fun insertCategory(category: Category)
    fun getCategoriesFlow(): Flow<List<Category>>
    fun findCategoryById(id: Long): Category
    fun deleteCategory(category: Category)
    fun updateCategory(category: Category)
}

@Singleton
class CategoryRepositoryImpl @Inject constructor(private val categoryDao: CategoryDao) :
    CategoryRepository {
    override fun insertCategory(category: Category) {
        return categoryDao.insertCategory(category)
    }

    override fun deleteCategory(category: Category) {
        return categoryDao.deleteCategory(category.id)
    }

    override fun updateCategory(category: Category) {
        return categoryDao.updateCategory(category)
    }

    override fun getCategoriesFlow(): Flow<List<Category>> {
        return categoryDao.getCategoriesFlow()
    }

    override fun findCategoryById(id: Long): Category {
        return categoryDao.findCategoryById(id)
    }
}