package com.example.busyday

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.busyday.database.ActivityDao
import com.example.busyday.database.ActivityEntity
import com.example.busyday.database.ActivityEntity.Companion.bitmapToByteArray
import com.example.busyday.database.ActivityEntity.Companion.byteArrayToBitmap
import com.example.busyday.database.AppDatabase
import com.example.busyday.database.CategoryDao
import com.example.busyday.database.CategoryEntity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException

class EditActivity : AppCompatActivity() {

    private lateinit var activityDao: ActivityDao
    private lateinit var categoryDao: CategoryDao
    private lateinit var imageViewPhoto: ImageView
    private var selectedImage: Bitmap? = null
    private lateinit var categorySpinner: Spinner

    companion object {
        private const val REQUEST_IMAGE_PICKER = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        imageViewPhoto = findViewById(R.id.imageEditViewPhoto)
        val btnSelectPhoto = findViewById<Button>(R.id.btnEditSelectPhoto)
        btnSelectPhoto.setOnClickListener { openImagePicker() }

        val appDatabase = AppDatabase.getDatabase(this)
        activityDao = appDatabase.activityDao()
        categoryDao = appDatabase.categoryDao()

        val activity = intent.getParcelableExtra<ActivityEntity>("activity")
        val idEditText = findViewById<EditText>(R.id.id_edit_text)
        val titleEditText = findViewById<EditText>(R.id.title_edit_text)
        val descriptionEditText = findViewById<EditText>(R.id.description_edit_text)
        val startTimeEditText = findViewById<EditText>(R.id.start_time_edit_text)
        val endTimeEditText = findViewById<EditText>(R.id.end_time_edit_text)
        val saveButton = findViewById<Button>(R.id.save_button)
        categorySpinner = findViewById(R.id.category_spinner)

        selectedImage = byteArrayToBitmap(activity?.photoBytes)
        imageViewPhoto.setImageBitmap(byteArrayToBitmap(activity?.photoBytes) ?: defaultImage())
        idEditText.setText(activity?.id.toString())
        titleEditText.setText(activity?.title)
        descriptionEditText.setText(activity?.description)
        startTimeEditText.setText(activity?.startTime)
        endTimeEditText.setText(activity?.endTime)

        GlobalScope.launch {
            val categories = loadCategories()
            setupCategorySpinner(categories, activity)
        }

        saveButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val description = descriptionEditText.text.toString()
            val startTime = startTimeEditText.text.toString()
            val endTime = endTimeEditText.text.toString()
            val selectedCategory = categorySpinner.selectedItem.toString()

            val updatedActivity = ActivityEntity(
                activity?.id ?: 0,
                title,
                description,
                startTime,
                endTime,
                selectedCategory,
                bitmapToByteArray(selectedImage))

            GlobalScope.launch {
                activityDao.updateActivity(updatedActivity)
            }

            setResult(android.app.Activity.RESULT_OK)
            finish()
        }
    }

    private suspend fun loadCategories(): List<CategoryEntity> {
        return categoryDao.getAllCategories()
    }

    private fun setupCategorySpinner(categories: List<CategoryEntity>, activity: ActivityEntity?) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter

        val selectedCategoryIndex = categories.indexOfFirst { it.name == activity?.category }
        categorySpinner.setSelection(selectedCategoryIndex)
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, EditActivity.REQUEST_IMAGE_PICKER)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EditActivity.REQUEST_IMAGE_PICKER && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data
            selectedImageUri?.let {
                try {
                    val inputStream = contentResolver.openInputStream(selectedImageUri)
                    selectedImage = BitmapFactory.decodeStream(inputStream)
                    inputStream?.close()
                    imageViewPhoto.setImageBitmap(selectedImage)

                    Toast.makeText(this, "Photo selected", Toast.LENGTH_SHORT).show()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun defaultImage(): Bitmap {
        return BitmapFactory.decodeResource(this.resources, R.drawable.ic_activity)
    }
}
