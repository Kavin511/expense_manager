package com.devstudio.data.repository

import android.content.Context
import com.devstudio.database.ApplicationModule
import com.devstudio.database.models.Category
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface CategoryRepository {
    fun insertCategory(category: Category)
    fun findCategoryById(id: String): Category?
    fun getCategoryByName(name: String): Category?
    fun deleteCategory(category: Category)
    fun updateCategory(category: Category)
    fun getCategoriesStream(type: String): Flow<List<Category>>
    fun getAllCategories(): Flow<List<Category>>
}

class CategoryRepositoryImpl : CategoryRepository, KoinComponent {
    private val context: Context by inject()
    private val db = ApplicationModule.config.factory.getRoomInstance()
    private val categoryDao = db.categoryDao()

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

    override fun getCategoryByName(name: String): Category? {
        return categoryDao.getCategoryByName(name)
    }

    override fun findCategoryById(id: String): Category? {
        return categoryDao.findCategoryById(id)
    }
}