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

    @Insert(onConflict = IGNORE)
    fun insertCategories(category: List<Category>)

    @Query("SELECT * FROM CATEGORY_TABLE WHERE  id=:categoryId")
    fun findCategoryById(categoryId: String): Category?

    @Query("DELETE FROM CATEGORY_TABLE WHERE ID=:categoryId")
    fun deleteCategory(categoryId: String)

    @Update
    fun updateCategory(category: Category)

    @Query("SELECT * FROM CATEGORY_TABLE WHERE categoryType==:type ORDER BY timeStamp asc")
    fun getCategoriesStream(type: String): Flow<List<Category>>

    @Query("SELECT * FROM CATEGORY_TABLE ORDER BY timeStamp asc")
    fun getAllCategories(): Flow<List<Category>>
}
