package com.devstudio.expensemanager.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.Query
import androidx.room.Update
import com.devstudio.expensemanager.db.models.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Insert(onConflict = IGNORE)
    fun insertCategory(category: Category)

    @Query("SELECT * FROM CATEGORY_TABLE ORDER BY id")
    fun getCategoriesFlow(): Flow<List<Category>>

    @Query("SELECT * FROM CATEGORY_TABLE WHERE  id=:categoryId")
    fun findCategoryById(categoryId: Long): Category

    @Query("DELETE FROM CATEGORY_TABLE WHERE ID=:categoryId")
    fun deleteCategory(categoryId: Long)

    @Update
    fun updateCategory(category: Category)
}