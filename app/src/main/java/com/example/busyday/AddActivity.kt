package com.example.busyday

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.busyday.database.ActivityEntity
import com.example.busyday.database.ActivityEntity.Companion.bitmapToByteArray
import com.example.busyday.database.AppDatabase
import com.example.busyday.database.CategoryDao
import com.example.busyday.database.CategoryEntity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class AddActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var startTimeEditText: EditText
    private lateinit var endTimeEditText: EditText
    private lateinit var imageViewPhoto: ImageView
    private var selectedImage: Bitmap? = null
    private lateinit var categorySpinner: Spinner
    private lateinit var categoryDao: CategoryDao

    companion object {
        private const val REQUEST_IMAGE_PICKER = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        titleEditText = findViewById(R.id.titleEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        startTimeEditText = findViewById(R.id.start_time_edit_text)
        endTimeEditText = findViewById(R.id.end_time_edit_text)
        imageViewPhoto = findViewById(R.id.imageViewPhoto)
        categorySpinner = findViewById(R.id.category_spinner)

        val btnSelectPhoto = findViewById<Button>(R.id.btnSelectPhoto)
        val addButton = findViewById<Button>(R.id.addButton)

        val appDatabase = AppDatabase.getDatabase(this)
        categoryDao = appDatabase.categoryDao()

        GlobalScope.launch {
            val categories = loadCategories()
            setupCategorySpinner(categories)
        }

        btnSelectPhoto.setOnClickListener { openImagePicker() }
        addButton.setOnClickListener { addActivity() }
    }

    private suspend fun loadCategories(): List<CategoryEntity> {
        return categoryDao.getAllCategories()
    }

    private fun setupCategorySpinner(categories: List<CategoryEntity>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter
    }

    private fun addActivity() {
        val title = titleEditText.text.toString()
        val description = descriptionEditText.text.toString()
        val startTime = startTimeEditText.text.toString()
        val endTime = endTimeEditText.text.toString()
        val selectedCategory = categorySpinner.selectedItem.toString()

        val activityDao = AppDatabase.getDatabase(this).activityDao()
        val activityEntity = ActivityEntity(
            title = title,
            description = description,
            startTime = startTime,
            endTime = endTime,
            category = selectedCategory,
            photoBytes = bitmapToByteArray(selectedImage))

        GlobalScope.launch {
            activityDao.insertActivity(activityEntity)
        }

        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICKER)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AddActivity.REQUEST_IMAGE_PICKER && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data
            selectedImageUri?.let {
                try {
                    val inputStream = contentResolver.openInputStream(selectedImageUri)
                    selectedImage = BitmapFactory.decodeStream(inputStream)
                    inputStream?.close()
                    imageViewPhoto.setImageBitmap(selectedImage)
                    imageViewPhoto.isVisible = true

                    Toast.makeText(this, "Photo selected", Toast.LENGTH_SHORT).show()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}
