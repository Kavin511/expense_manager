package com.devstudio.expensemanager.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.Query
import androidx.room.Update
import com.devstudio.expensemanager.db.models.Category
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface CategoryDao {
    @Insert(onConflict = IGNORE)
    fun insertCategory(category: Category)

    @Insert(onConflict = IGNORE)
    fun insertCategories(category: List<Category>)

    @Query("SELECT * FROM CATEGORY_TABLE ORDER BY timeStamp desc")
    fun getCategoriesFlow(): Flow<List<Category>>

    @Query("SELECT * FROM CATEGORY_TABLE WHERE  id=:categoryId order by timeStamp desc")
    fun findCategoryById(categoryId: UUID): Category

    @Query("DELETE FROM CATEGORY_TABLE WHERE ID=:categoryId")
    fun deleteCategory(categoryId: UUID)

    @Update
    fun updateCategory(category: Category)

    @Query("SELECT * FROM CATEGORY_TABLE WHERE categoryType==:type ORDER BY timeStamp desc")
    fun getCategoriesStream(type: String): Flow<List<Category>>

    @Query("SELECT * FROM CATEGORY_TABLE ORDER BY timeStamp desc")
    fun getAllCategories(): Flow<List<Category>>

    @Query("SELECT * FROM CATEGORY_TABLE WHERE categoryType==:type ORDER BY timeStamp desc")
    fun getCategories(type: String): List<Category>
}