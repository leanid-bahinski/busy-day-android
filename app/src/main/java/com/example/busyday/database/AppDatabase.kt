package com.example.busyday.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

@Database(entities = [ActivityEntity::class, CategoryEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun activityDao(): ActivityDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                val database = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database_10"
                ).build()
                instance = database
                runBlocking {
                    database.populateInitialCategories()
                }
                database
            }
        }
    }

    suspend fun populateInitialCategories() {
        withContext(Dispatchers.IO) {
            val categoryDao = categoryDao()
            if (categoryDao != null) {
                val categories = categoryDao.getAllCategories()
                if (categories.isEmpty()) {
                    val initialCategories = listOf(
                        CategoryEntity(0, "Учеба"),
                        CategoryEntity(0, "Еда"),
                        CategoryEntity(0, "Сон"),
                        CategoryEntity(0, "Работа"),
                        CategoryEntity(0, "Развлечения")
                    )
                    categoryDao.insertCategories(initialCategories)
                }
            }
        }
    }
}