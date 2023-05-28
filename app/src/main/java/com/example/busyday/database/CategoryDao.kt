package com.example.busyday.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories")
    suspend fun getAllCategories(): List<CategoryEntity>

    @Insert
    suspend fun insertCategory(category: CategoryEntity)

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    @Insert
    suspend fun insertCategories(initialCategories: List<CategoryEntity>)
}
