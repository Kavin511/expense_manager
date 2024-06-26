package com.devstudio.data.repository

import com.devstudio.expensemanager.db.dao.CategoryDao
import com.devstudio.expensemanager.db.models.Category
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface CategoryRepository {
    fun insertCategory(category: Category)
    fun findCategoryById(id: String): Category?
    fun deleteCategory(category: Category)
    fun updateCategory(category: Category)
    fun getCategoriesStream(type: String): Flow<List<Category>>
    fun getAllCategories(): Flow<List<Category>>
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

    override fun getCategoriesStream(type: String): Flow<List<Category>> {
        return categoryDao.getCategoriesStream(type)
    }

    override fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories()
    }

    override fun findCategoryById(id: String): Category? {
        return categoryDao.findCategoryById(id)
    }
}
