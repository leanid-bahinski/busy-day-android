package com.example.busyday

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.busyday.adapters.CategoryAdapter
import com.example.busyday.database.ActivityDao
import com.example.busyday.database.ActivityEntity
import com.example.busyday.database.AppDatabase
import com.example.busyday.database.CategoryDao
import com.example.busyday.database.CategoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoryActivity : AppCompatActivity() {

    private lateinit var categoryAdapter: CategoryAdapter
    private val categories = mutableListOf<CategoryEntity>()
    private lateinit var categoryDao: CategoryDao
    private lateinit var activityDao: ActivityDao
    private val activities = mutableListOf<ActivityEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val appDatabase = AppDatabase.getDatabase(this)
        categoryDao = appDatabase.categoryDao()
        activityDao = appDatabase.activityDao()

        val recyclerView = findViewById<RecyclerView>(R.id.categoryRecyclerView)
        categoryAdapter = CategoryAdapter(categories, activities)
        recyclerView.adapter = categoryAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadCategories()
        loadActivities()
    }

    private fun loadCategories() {
        GlobalScope.launch {
            val categoryList = categoryDao.getAllCategories()
            categories.clear()
            categories.addAll(categoryList)
            withContext(Dispatchers.Main) {
                categoryAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun loadActivities() {
        GlobalScope.launch {
            val activityList = activityDao.getAllActivities()
            activities.clear()
            activities.addAll(activityList)
            withContext(Dispatchers.Main) {
                categoryAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
